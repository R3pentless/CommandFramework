package pl.inh.commandframework.supplier.builtin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.inh.commandframework.supplier.TabContext;
import pl.inh.commandframework.supplier.TabSupplier;

import java.util.List;

public class PlayerSupplier implements TabSupplier {

    @Override
    public List<String> getSuggestions(TabContext context) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> {
                    Player player = Bukkit.getPlayerExact(name);
                    return player != null && !player.hasMetadata("vanished");
                })
                .toList();
    }
}