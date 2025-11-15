package pl.inh.commandframework.supplier.builtin;

import org.bukkit.enchantments.Enchantment;
import pl.inh.commandframework.supplier.TabContext;
import pl.inh.commandframework.supplier.TabSupplier;

import java.util.Arrays;
import java.util.List;

public class EnchantmentSupplier implements TabSupplier {

    @Override
    public List<String> getSuggestions(TabContext context) {
        return Arrays.stream(Enchantment.values())
                .map(ench -> ench.getKey().getKey())
                .toList();
    }
}