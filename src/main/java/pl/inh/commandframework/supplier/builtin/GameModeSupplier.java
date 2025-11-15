package pl.inh.commandframework.supplier.builtin;

import org.bukkit.GameMode;
import pl.inh.commandframework.supplier.TabContext;
import pl.inh.commandframework.supplier.TabSupplier;

import java.util.Arrays;
import java.util.List;

public class GameModeSupplier implements TabSupplier {

    @Override
    public List<String> getSuggestions(TabContext context) {
        return Arrays.stream(GameMode.values())
                .map(gm -> gm.name().toLowerCase())
                .toList();
    }
}