package pl.inh.commandframework.supplier.builtin;

import pl.inh.commandframework.supplier.TabContext;
import pl.inh.commandframework.supplier.TabSupplier;

import java.util.ArrayList;
import java.util.List;

public class NumberSupplier implements TabSupplier {

    @Override
    public List<String> getSuggestions(TabContext context) {
        String input = context.getCurrentInput();

        if (input.isEmpty()) {
            return List.of("1", "5", "10", "64", "100");
        }

        try {
            int current = Integer.parseInt(input);
            List<String> suggestions = new ArrayList<>();
            suggestions.add(String.valueOf(current));
            if (current < 1000) {
                suggestions.add(String.valueOf(current * 2));
                suggestions.add(String.valueOf(current * 10));
            }
            return suggestions;
        } catch (NumberFormatException e) {
            return List.of("1", "10", "100");
        }
    }
}