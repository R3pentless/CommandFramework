package pl.inh.commandframework.parser;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ArgumentParser<T> {

    @NotNull
    T parse(@NotNull CommandSender sender, @NotNull String input) throws Exception;
}