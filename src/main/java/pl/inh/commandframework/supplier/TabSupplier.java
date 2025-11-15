package pl.inh.commandframework.supplier;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public interface TabSupplier {

    @NotNull
    List<String> getSuggestions(@NotNull TabContext context);

    static TabSupplier of(List<String> suggestions) {
        return context -> suggestions;
    }

    static TabSupplier of(java.util.function.Supplier<List<String>> supplier) {
        return context -> supplier.get();
    }
}