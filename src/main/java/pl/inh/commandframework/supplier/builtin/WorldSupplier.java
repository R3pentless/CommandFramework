package pl.inh.commandframework.supplier.builtin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import pl.inh.commandframework.supplier.TabContext;
import pl.inh.commandframework.supplier.TabSupplier;

import java.util.List;

public class WorldSupplier implements TabSupplier {

    @Override
    public List<String> getSuggestions(TabContext context) {
        return Bukkit.getWorlds().stream()
                .map(World::getName)
                .toList();
    }
}