package pl.inh.commandframework.parser.parsers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.inh.commandframework.parser.ArgumentParser;

public class PlayerParser implements ArgumentParser<Player> {

    @Override
    public Player parse(CommandSender sender, String input) throws Exception {
        Player player = Bukkit.getPlayerExact(input);
        if (player == null) {
            throw new IllegalArgumentException("Player '" + input + "' not found!");
        }
        return player;
    }
}