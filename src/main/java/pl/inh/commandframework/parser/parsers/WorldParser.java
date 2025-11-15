package pl.inh.commandframework.parser.parsers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import pl.inh.commandframework.parser.ArgumentParser;

public class WorldParser implements ArgumentParser<World> {

    @Override
    public World parse(CommandSender sender, String input) throws Exception {
        World world = Bukkit.getWorld(input);
        if (world == null) {
            throw new IllegalArgumentException("World '" + input + "' not found!");
        }
        return world;
    }
}