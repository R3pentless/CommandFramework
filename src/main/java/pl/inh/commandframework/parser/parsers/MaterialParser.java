package pl.inh.commandframework.parser.parsers;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import pl.inh.commandframework.parser.ArgumentParser;

public class MaterialParser implements ArgumentParser<Material> {

    @Override
    public Material parse(CommandSender sender, String input) throws Exception {
        try {
            Material material = Material.valueOf(input.toUpperCase());
            if (!material.isItem()) {
                throw new IllegalArgumentException("'" + input + "' is not a valid item!");
            }
            return material;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Material '" + input + "' not found!");
        }
    }
}