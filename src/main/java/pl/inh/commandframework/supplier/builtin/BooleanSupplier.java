package pl.inh.commandframework.supplier.builtin;

import pl.inh.commandframework.supplier.TabContext;
import pl.inh.commandframework.supplier.TabSupplier;

import java.util.List;

public class BooleanSupplier implements TabSupplier {

    @Override
    public List<String> getSuggestions(TabContext context) {
        return List.of("true", "false");
    }
}