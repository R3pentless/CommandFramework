package pl.inh.commandframework.parser.parsers;

import org.bukkit.command.CommandSender;
import pl.inh.commandframework.parser.ArgumentParser;

public class BooleanParser implements ArgumentParser<Boolean> {

    @Override
    public Boolean parse(CommandSender sender, String input) throws Exception {
        if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("yes") || input.equals("1")) {
            return true;
        } else if (input.equalsIgnoreCase("false") || input.equalsIgnoreCase("no") || input.equals("0")) {
            return false;
        }
        throw new IllegalArgumentException("'" + input + "' is not a valid boolean!");
    }
}