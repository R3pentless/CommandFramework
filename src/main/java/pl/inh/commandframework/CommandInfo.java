package pl.inh.commandframework;

import pl.inh.commandframework.annotation.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandInfo {

    private final String name;
    private final String permission;
    private final String description;
    private final List<String> aliases;
    private final Object instance;
    private final List<CommandMethod> methods;

    public CommandInfo(Command command, Object instance) {
        this.name = command.name();
        this.permission = command.permission();
        this.description = command.description();
        this.aliases = new ArrayList<>(Arrays.asList(command.aliases()));
        this.instance = instance;
        this.methods = new ArrayList<>();
    }

    public void addMethod(CommandMethod method) {
        methods.add(method);
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public Object getInstance() {
        return instance;
    }

    public List<CommandMethod> getMethods() {
        return methods;
    }

    public CommandMethod getDefaultMethod() {
        return methods.stream()
                .filter(CommandMethod::isDefault)
                .findFirst()
                .orElse(null);
    }

    public CommandMethod getSubCommand(String subCommand) {
        return methods.stream()
                .filter(m -> m.getSubCommand() != null && m.getSubCommand().equalsIgnoreCase(subCommand))
                .findFirst()
                .orElse(null);
    }
}