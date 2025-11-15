package pl.inh.commandframework.parser.parsers;

import org.bukkit.command.CommandSender;
import pl.inh.commandframework.parser.ArgumentParser;

public class DoubleParser implements ArgumentParser<Double> {

    @Override
    public Double parse(CommandSender sender, String input) throws Exception {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + input + "' is not a valid number!");
        }
    }
}