package pl.inh.commandframework.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.inh.commandframework.parser.parsers.*;

import java.util.HashMap;
import java.util.Map;

public class ParserRegistry {

    private final Map<Class<?>, ArgumentParser<?>> parsers = new HashMap<>();

    public ParserRegistry() {
        registerDefaultParsers();
    }

    public <T> void register(@NotNull Class<T> type, @NotNull ArgumentParser<T> parser) {
        parsers.put(type, parser);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> ArgumentParser<T> getParser(@NotNull Class<T> type) {
        // Check if parser already exists
        ArgumentParser<T> parser = (ArgumentParser<T>) parsers.get(type);
        if (parser != null) {
            return parser;
        }

        // Auto-create enum parser if type is enum
        if (type.isEnum()) {
            EnumParser<? extends Enum> enumParser = new EnumParser<>((Class<? extends Enum>) type);
            parsers.put(type, enumParser);
            return (ArgumentParser<T>) enumParser;
        }

        return null;
    }

    private void registerDefaultParsers() {
        register(org.bukkit.entity.Player.class, new PlayerParser());
        register(org.bukkit.World.class, new WorldParser());
        register(org.bukkit.Material.class, new MaterialParser());
        register(Integer.class, new IntegerParser());
        register(int.class, new IntegerParser());
        register(Double.class, new DoubleParser());
        register(double.class, new DoubleParser());
        register(Boolean.class, new BooleanParser());
        register(boolean.class, new BooleanParser());
        register(String.class, (sender, input) -> input);
        register(org.bukkit.GameMode.class, new EnumParser<>(org.bukkit.GameMode.class));
    }
}