package pl.inh.commandframework.supplier;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TabContext {

    private final CommandSender sender;
    private final String[] args;
    private final int currentArgIndex;
    private final String currentInput;

    public TabContext(CommandSender sender, String[] args, int currentArgIndex) {
        this.sender = sender;
        this.args = args;
        this.currentArgIndex = currentArgIndex;
        this.currentInput = args.length > currentArgIndex ? args[currentArgIndex] : "";
    }

    @NotNull
    public CommandSender getSender() {
        return sender;
    }

    @Nullable
    public Player getPlayer() {
        return sender instanceof Player ? (Player) sender : null;
    }

    @NotNull
    public String[] getArgs() {
        return args;
    }

    public int getCurrentArgIndex() {
        return currentArgIndex;
    }

    @NotNull
    public String getCurrentInput() {
        return currentInput;
    }

    @NotNull
    public String getPreviousArg(int offset) {
        int index = currentArgIndex - offset;
        return index >= 0 && index < args.length ? args[index] : "";
    }

    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @NotNull
    public List<String> filter(List<String> suggestions) {
        if (currentInput.isEmpty()) {
            return suggestions;
        }

        String lower = currentInput.toLowerCase();
        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(lower))
                .toList();
    }
}