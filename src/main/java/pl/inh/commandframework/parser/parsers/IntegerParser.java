package pl.inh.commandframework.parser.parsers;

import org.bukkit.command.CommandSender;
import pl.inh.commandframework.parser.ArgumentParser;

public class IntegerParser implements ArgumentParser<Integer> {

    @Override
    public Integer parse(CommandSender sender, String input) throws Exception {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + input + "' is not a valid number!");
        }
    }
}