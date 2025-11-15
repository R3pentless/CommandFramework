package pl.inh.commandframework.supplier.builtin;

import org.bukkit.Material;
import pl.inh.commandframework.supplier.TabContext;
import pl.inh.commandframework.supplier.TabSupplier;

import java.util.Arrays;
import java.util.List;

public class MaterialSupplier implements TabSupplier {

    private static final List<String> COMMON_MATERIALS = Arrays.asList(
            "DIAMOND", "GOLD_INGOT", "IRON_INGOT", "EMERALD",
            "DIAMOND_SWORD", "DIAMOND_PICKAXE", "DIAMOND_AXE",
            "STONE", "DIRT", "GRASS_BLOCK", "COBBLESTONE"
    );

    @Override
    public List<String> getSuggestions(TabContext context) {
        String input = context.getCurrentInput().toUpperCase();

        if (input.length() < 3) {
            return COMMON_MATERIALS;
        }

        return Arrays.stream(Material.values())
                .filter(Material::isItem)
                .map(Enum::name)
                .filter(name -> name.contains(input))
                .limit(20)
                .toList();
    }
}