package net.aniby.hybrid.backpack;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Backpack {
    final Component name;
    final String identifier;
    @Getter(AccessLevel.NONE)
    final File configurationFile;
    @Setter
    List<ItemStack> items;
    final int size = 27;

    public Backpack(Component name, String identifier) {
        this.name = name;
        this.identifier = identifier;
        Path worldPath = Bukkit.getWorlds().getFirst().getWorldFolder().toPath();
        this.configurationFile = worldPath.resolve("hybrid/backpacks/" + identifier + ".yml").toFile();
    }

    public void load() throws IOException {
        if (this.configurationFile.exists()) {
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(
                    this.configurationFile
            );
            this.items = new ArrayList<>(
                    (List<ItemStack>) configuration.getList("items")
            );
        } else {
            this.items = Collections.nCopies(this.size, ItemStack.empty());
            this.save();
        }
    }

    public void save() throws IOException {
        FileConfiguration configuration = new YamlConfiguration();
        configuration.set("items", this.items);
        configuration.save(configurationFile);
    }
}
