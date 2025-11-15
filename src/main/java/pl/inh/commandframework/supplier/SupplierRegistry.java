package pl.inh.commandframework.supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.inh.commandframework.supplier.builtin.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SupplierRegistry {

    private final Map<String, TabSupplier> suppliers = new ConcurrentHashMap<>();
    private final Map<Class<?>, TabSupplier> typeSuppliers = new ConcurrentHashMap<>();

    public SupplierRegistry() {
        registerBuiltInSuppliers();
    }

    public void register(@NotNull String name, @NotNull TabSupplier supplier) {
        suppliers.put(name.toLowerCase(), supplier);
    }

    public void registerForType(@NotNull Class<?> type, @NotNull TabSupplier supplier) {
        typeSuppliers.put(type, supplier);
    }

    @Nullable
    public TabSupplier get(@NotNull String name) {
        return suppliers.get(name.toLowerCase());
    }

    @Nullable
    public TabSupplier getForType(@NotNull Class<?> type) {
        return typeSuppliers.get(type);
    }

    @Nullable
    public TabSupplier resolve(@NotNull String argName,
                               @NotNull Class<?> argType,
                               @Nullable String explicitName) {

        if (explicitName != null && !explicitName.isEmpty()) {
            TabSupplier supplier = get(explicitName);
            if (supplier != null) return supplier;
        }

        TabSupplier supplier = get(argName);
        if (supplier != null) return supplier;

        return getForType(argType);
    }

    private void registerBuiltInSuppliers() {
        TabSupplier playerSupplier = new PlayerSupplier();
        register("player", playerSupplier);
        register("players", playerSupplier);
        register("target", playerSupplier);
        registerForType(org.bukkit.entity.Player.class, playerSupplier);

        TabSupplier worldSupplier = new WorldSupplier();
        register("world", worldSupplier);
        register("worlds", worldSupplier);
        registerForType(org.bukkit.World.class, worldSupplier);

        TabSupplier materialSupplier = new MaterialSupplier();
        register("item", materialSupplier);
        register("items", materialSupplier);
        register("material", materialSupplier);
        register("block", materialSupplier);
        registerForType(org.bukkit.Material.class, materialSupplier);

        TabSupplier numberSupplier = new NumberSupplier();
        register("amount", numberSupplier);
        register("count", numberSupplier);
        register("number", numberSupplier);
        registerForType(Integer.class, numberSupplier);
        registerForType(int.class, numberSupplier);
        registerForType(Double.class, numberSupplier);
        registerForType(double.class, numberSupplier);

        TabSupplier booleanSupplier = new BooleanSupplier();
        register("boolean", booleanSupplier);
        register("enabled", booleanSupplier);
        register("toggle", booleanSupplier);
        registerForType(Boolean.class, booleanSupplier);
        registerForType(boolean.class, booleanSupplier);

        TabSupplier gameModeSupplier = new GameModeSupplier();
        register("gamemode", gameModeSupplier);
        register("mode", gameModeSupplier);
        registerForType(org.bukkit.GameMode.class, gameModeSupplier);

        TabSupplier enchantmentSupplier = new EnchantmentSupplier();
        register("enchantment", enchantmentSupplier);
        register("enchant", enchantmentSupplier);
        registerForType(org.bukkit.enchantments.Enchantment.class, enchantmentSupplier);
    }
}