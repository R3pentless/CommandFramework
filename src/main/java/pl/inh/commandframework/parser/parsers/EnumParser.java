package pl.inh.commandframework.parser.parsers;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pl.inh.commandframework.parser.ArgumentParser;

public class EnumParser<T extends Enum<T>> implements ArgumentParser<T> {

    private final Class<T> enumClass;

    public EnumParser(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public @NotNull T parse(@NotNull CommandSender sender, @NotNull String input) throws Exception {
        try {
            return Enum.valueOf(enumClass, input.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try case-insensitive search
            for (T enumConstant : enumClass.getEnumConstants()) {
                if (enumConstant.name().equalsIgnoreCase(input)) {
                    return enumConstant;
                }
            }

            // Build available values message
            StringBuilder available = new StringBuilder();
            T[] constants = enumClass.getEnumConstants();
            for (int i = 0; i < constants.length; i++) {
                available.append(constants[i].name().toLowerCase());
                if (i < constants.length - 1) {
                    available.append(", ");
                }
            }

            throw new IllegalArgumentException("Invalid " + enumClass.getSimpleName() + ": '" + input +
                    "'. Available: " + available);
        }
    }
}