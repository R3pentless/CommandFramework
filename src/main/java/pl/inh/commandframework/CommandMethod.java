package pl.inh.commandframework;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.inh.commandframework.annotation.*;
import pl.inh.commandframework.parser.ArgumentParser;
import pl.inh.commandframework.parser.ParserRegistry;
import pl.inh.commandframework.supplier.SupplierRegistry;
import pl.inh.commandframework.supplier.TabContext;
import pl.inh.commandframework.supplier.TabSupplier;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class CommandMethod {

    private final Method method;
    private final String subCommand;
    private final String permission;
    private final boolean isDefault;
    private final Cooldown cooldown;
    private final List<ArgumentInfo> arguments;

    public CommandMethod(Method method) {
        this.method = method;
        this.isDefault = method.isAnnotationPresent(Default.class);
        this.subCommand = method.isAnnotationPresent(SubCommand.class)
                ? method.getAnnotation(SubCommand.class).value()
                : null;
        this.permission = method.isAnnotationPresent(Permission.class)
                ? method.getAnnotation(Permission.class).value()
                : null;
        this.cooldown = method.getAnnotation(Cooldown.class);
        this.arguments = new ArrayList<>();

        parseArguments();
    }

    private void parseArguments() {
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];

            if (i == 0 && (CommandSender.class.isAssignableFrom(param.getType()) ||
                    Player.class.isAssignableFrom(param.getType()))) {
                continue;
            }

            Arg argAnnotation = param.getAnnotation(Arg.class);
            if (argAnnotation == null) {
                throw new IllegalArgumentException("Parameter " + param.getName() + " must have @Arg annotation!");
            }

            String argName = argAnnotation.value();
            Tab tabAnnotation = param.getAnnotation(Tab.class);
            Optional optionalAnnotation = param.getAnnotation(Optional.class);
            Range rangeAnnotation = param.getAnnotation(Range.class);

            arguments.add(new ArgumentInfo(
                    argName,
                    param.getType(),
                    tabAnnotation,
                    optionalAnnotation,
                    rangeAnnotation
            ));
        }
    }

    public Object execute(Object instance, CommandSender sender, String[] args, ParserRegistry parserRegistry) throws Exception {
        List<Object> methodArgs = new ArrayList<>();

        Parameter[] parameters = method.getParameters();
        if (parameters.length > 0 &&
                (CommandSender.class.isAssignableFrom(parameters[0].getType()) ||
                        Player.class.isAssignableFrom(parameters[0].getType()))) {

            if (parameters[0].getType() == Player.class && !(sender instanceof Player)) {
                throw new CommandException("§cOnly players can use this command!");
            }
            methodArgs.add(sender);
        }

        int argIndex = 0;
        for (ArgumentInfo argInfo : arguments) {
            if (argIndex >= args.length) {
                if (argInfo.isOptional()) {
                    methodArgs.add(argInfo.getDefaultValue(parserRegistry, sender));
                    continue;
                } else {
                    throw new CommandException("§cMissing argument: " + argInfo.getName());
                }
            }

            String input = args[argIndex++];
            ArgumentParser<?> parser = parserRegistry.getParser(argInfo.getType());

            if (parser == null) {
                throw new CommandException("§cNo parser found for type: " + argInfo.getType().getSimpleName());
            }

            try {
                Object parsed = parser.parse(sender, input);

                if (argInfo.hasRange()) {
                    argInfo.validateRange(parsed);
                }

                methodArgs.add(parsed);
            } catch (Exception e) {
                throw new CommandException("§cInvalid argument '" + argInfo.getName() + "': " + e.getMessage());
            }
        }

        return method.invoke(instance, methodArgs.toArray());
    }

    public List<String> getTabCompletions(CommandSender sender, String[] args, SupplierRegistry supplierRegistry) {
        int argIndex = args.length - 1;

        Parameter[] parameters = method.getParameters();
        int paramOffset = 0;
        if (parameters.length > 0 &&
                (CommandSender.class.isAssignableFrom(parameters[0].getType()) ||
                        Player.class.isAssignableFrom(parameters[0].getType()))) {
            paramOffset = 1;
        }

        int actualArgIndex = argIndex - paramOffset;
        if (actualArgIndex < 0 || actualArgIndex >= arguments.size()) {
            return List.of();
        }

        ArgumentInfo argInfo = arguments.get(actualArgIndex);
        TabSupplier supplier = argInfo.getTabSupplier(supplierRegistry);

        if (supplier == null) {
            return List.of();
        }

        TabContext context = new TabContext(sender, args, argIndex);
        List<String> suggestions = supplier.getSuggestions(context);

        return context.filter(suggestions);
    }

    public Method getMethod() {
        return method;
    }

    public String getSubCommand() {
        return subCommand;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public Cooldown getCooldown() {
        return cooldown;
    }

    public List<ArgumentInfo> getArguments() {
        return arguments;
    }

    private static class ArgumentInfo {
        private final String name;
        private final Class<?> type;
        private final Tab tab;
        private final Optional optional;
        private final Range range;

        public ArgumentInfo(String name, Class<?> type, Tab tab, Optional optional, Range range) {
            this.name = name;
            this.type = type;
            this.tab = tab;
            this.optional = optional;
            this.range = range;
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public boolean isOptional() {
            return optional != null;
        }

        public boolean hasRange() {
            return range != null;
        }

        public Object getDefaultValue(ParserRegistry parserRegistry, CommandSender sender) throws Exception {
            if (optional == null) return null;

            String defaultValue = optional.value();
            if (defaultValue.isEmpty()) {
                if (type == boolean.class || type == Boolean.class) return false;
                if (type == int.class || type == Integer.class) return 0;
                if (type == double.class || type == Double.class) return 0.0;
                return null;
            }

            ArgumentParser<?> parser = parserRegistry.getParser(type);
            if (parser == null) return null;

            return parser.parse(sender, defaultValue);
        }

        public void validateRange(Object value) throws CommandException {
            if (range == null) return;

            if (value instanceof Integer) {
                int intValue = (Integer) value;
                if (intValue < range.min() || intValue > range.max()) {
                    throw new CommandException("§c" + name + " must be between " + range.min() + " and " + range.max());
                }
            } else if (value instanceof Double) {
                double doubleValue = (Double) value;
                if (doubleValue < range.minDouble() || doubleValue > range.maxDouble()) {
                    throw new CommandException("§c" + name + " must be between " + range.minDouble() + " and " + range.maxDouble());
                }
            }
        }

        public TabSupplier getTabSupplier(SupplierRegistry registry) {
            String supplierName = tab != null ? tab.value() : null;
            return registry.resolve(name, type, supplierName);
        }
    }
}