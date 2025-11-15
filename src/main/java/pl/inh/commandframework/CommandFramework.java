package pl.inh.commandframework;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pl.inh.commandframework.annotation.Command;
import pl.inh.commandframework.annotation.Cooldown;
import pl.inh.commandframework.annotation.Default;
import pl.inh.commandframework.annotation.SubCommand;
import pl.inh.commandframework.parser.ParserRegistry;
import pl.inh.commandframework.supplier.SupplierRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class CommandFramework {

    private final JavaPlugin plugin;
    private final Map<String, CommandInfo> commands = new HashMap<>();
    private final SupplierRegistry supplierRegistry;
    private final ParserRegistry parserRegistry;
    private final CooldownManager cooldownManager;
    private CommandMap commandMap;
    private Map<String, org.bukkit.command.Command> knownCommands;

    public CommandFramework(JavaPlugin plugin) {
        this.plugin = plugin;
        this.supplierRegistry = new SupplierRegistry();
        this.parserRegistry = new ParserRegistry();
        this.cooldownManager = new CooldownManager();

        initializeCommandMap();

        plugin.getServer().getScheduler().runTaskTimer(plugin,
                cooldownManager::cleanup, 20 * 60, 20 * 60);
    }

    @SuppressWarnings("unchecked")
    private void initializeCommandMap() {
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (CommandMap) commandMapField.get(Bukkit.getPluginManager());

                Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                knownCommands = (Map<String, org.bukkit.command.Command>) knownCommandsField.get(commandMap);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize CommandMap!");
            e.printStackTrace();
        }
    }

    public void register(Object commandObject) {
        Class<?> clazz = commandObject.getClass();

        if (!clazz.isAnnotationPresent(Command.class)) {
            throw new IllegalArgumentException("Class must have @Command annotation!");
        }

        Command commandAnnotation = clazz.getAnnotation(Command.class);
        CommandInfo commandInfo = new CommandInfo(commandAnnotation, commandObject);

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Default.class) || method.isAnnotationPresent(SubCommand.class)) {
                CommandMethod commandMethod = new CommandMethod(method);
                commandInfo.addMethod(commandMethod);
            }
        }

        commands.put(commandInfo.getName().toLowerCase(), commandInfo);

        registerCommandDynamically(commandInfo);

        plugin.getLogger().info("Registered command: /" + commandInfo.getName());
    }

    private void registerCommandDynamically(CommandInfo commandInfo) {
        try {
            String commandName = commandInfo.getName().toLowerCase();

            // Unregister existing command if present
            if (knownCommands.containsKey(commandName)) {
                knownCommands.remove(commandName);
                for (String alias : commandInfo.getAliases()) {
                    knownCommands.remove(alias.toLowerCase());
                }
            }

            // Create custom command
            CustomCommand command = new CustomCommand(
                    commandName,
                    commandInfo,
                    new CommandHandler(commandInfo),
                    new TabHandler(commandInfo)
            );

            // Set description and aliases
            if (!commandInfo.getDescription().isEmpty()) {
                command.setDescription(commandInfo.getDescription());
            }
            command.setAliases(commandInfo.getAliases());

            // Register in command map
            commandMap.register(plugin.getName().toLowerCase(), command);

            // Sync commands to clients
            syncCommands();

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register command: " + commandInfo.getName());
            e.printStackTrace();
        }
    }

    private void syncCommands() {
        try {
            // Sync commands to all online players
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.updateCommands();
            });
        } catch (Exception e) {
            // Ignore - not critical
        }
    }

    public void unregister(String commandName) {
        CommandInfo commandInfo = commands.remove(commandName.toLowerCase());
        if (commandInfo != null) {
            knownCommands.remove(commandName.toLowerCase());
            for (String alias : commandInfo.getAliases()) {
                knownCommands.remove(alias.toLowerCase());
            }
            syncCommands();
        }
    }

    public void unregisterAll() {
        for (String commandName : new ArrayList<>(commands.keySet())) {
            unregister(commandName);
        }
    }

    public SupplierRegistry getSupplierRegistry() {
        return supplierRegistry;
    }

    public ParserRegistry getParserRegistry() {
        return parserRegistry;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    // Custom Command class
    private class CustomCommand extends org.bukkit.command.Command {
        private final CommandInfo commandInfo;
        private final CommandExecutor executor;
        private final TabCompleter tabCompleter;

        public CustomCommand(String name, CommandInfo commandInfo, CommandExecutor executor, TabCompleter tabCompleter) {
            super(name);
            this.commandInfo = commandInfo;
            this.executor = executor;
            this.tabCompleter = tabCompleter;
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
            return executor.onCommand(sender, this, commandLabel, args);
        }

        @Override
        public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
            List<String> completions = tabCompleter.onTabComplete(sender, this, alias, args);
            return completions != null ? completions : List.of();
        }
    }

    // Command Handler
    private class CommandHandler implements CommandExecutor {
        private final CommandInfo commandInfo;

        public CommandHandler(CommandInfo commandInfo) {
            this.commandInfo = commandInfo;
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command,
                                 @NotNull String label, @NotNull String[] args) {

            if (!commandInfo.getPermission().isEmpty() && !sender.hasPermission(commandInfo.getPermission())) {
                sender.sendMessage("§cYou don't have permission to use this command!");
                return true;
            }

            CommandMethod method;
            String[] methodArgs;

            if (args.length > 0) {
                CommandMethod subCommand = commandInfo.getSubCommand(args[0]);
                if (subCommand != null) {
                    method = subCommand;
                    methodArgs = Arrays.copyOfRange(args, 1, args.length);
                } else {
                    method = commandInfo.getDefaultMethod();
                    methodArgs = args;
                }
            } else {
                method = commandInfo.getDefaultMethod();
                methodArgs = args;
            }

            if (method == null) {
                sender.sendMessage("§cUsage: /" + commandInfo.getName() + " <subcommand>");
                return true;
            }

            if (method.getPermission() != null && !sender.hasPermission(method.getPermission())) {
                sender.sendMessage("§cYou don't have permission to use this command!");
                return true;
            }

            Cooldown cooldown = method.getCooldown();
            if (cooldown != null && sender instanceof Player) {
                Player player = (Player) sender;
                String cooldownKey = commandInfo.getName() + "." + (method.getSubCommand() != null ? method.getSubCommand() : "default");

                if (cooldownManager.isOnCooldown(cooldownKey, player.getUniqueId())) {
                    long remaining = cooldownManager.getRemainingCooldown(cooldownKey, player.getUniqueId());
                    String message = cooldown.message().replace("{time}", String.valueOf(remaining));
                    sender.sendMessage(message.replace("&", "§"));
                    return true;
                }

                cooldownManager.setCooldown(cooldownKey, player.getUniqueId(), cooldown.seconds());
            }

            try {
                method.execute(commandInfo.getInstance(), sender, methodArgs, parserRegistry);
            } catch (CommandException e) {
                sender.sendMessage(e.getMessage());
            } catch (Exception e) {
                sender.sendMessage("§cAn error occurred while executing the command!");
                e.printStackTrace();
            }

            return true;
        }
    }

    // Tab Handler
    private class TabHandler implements TabCompleter {
        private final CommandInfo commandInfo;

        public TabHandler(CommandInfo commandInfo) {
            this.commandInfo = commandInfo;
        }

        @Override
        public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command,
                                          @NotNull String label, @NotNull String[] args) {

            if (!commandInfo.getPermission().isEmpty() && !sender.hasPermission(commandInfo.getPermission())) {
                return List.of();
            }

            if (args.length == 1) {
                List<String> subCommands = new ArrayList<>();
                for (CommandMethod method : commandInfo.getMethods()) {
                    if (method.getSubCommand() != null) {
                        if (method.getPermission() == null || sender.hasPermission(method.getPermission())) {
                            subCommands.add(method.getSubCommand());
                        }
                    }
                }

                String input = args[0].toLowerCase();
                return subCommands.stream()
                        .filter(s -> s.toLowerCase().startsWith(input))
                        .toList();
            }

            CommandMethod method;
            String[] methodArgs;

            if (args.length > 1) {
                CommandMethod subCommand = commandInfo.getSubCommand(args[0]);
                if (subCommand != null) {
                    method = subCommand;
                    methodArgs = Arrays.copyOfRange(args, 1, args.length);
                } else {
                    method = commandInfo.getDefaultMethod();
                    methodArgs = args;
                }
            } else {
                method = commandInfo.getDefaultMethod();
                methodArgs = args;
            }

            if (method == null) {
                return List.of();
            }

            if (method.getPermission() != null && !sender.hasPermission(method.getPermission())) {
                return List.of();
            }

            return method.getTabCompletions(sender, methodArgs, supplierRegistry);
        }
    }
}