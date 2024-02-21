package de.rollocraft.allminecraft.Minecraft.Manager;

import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Minecraft.Backpack;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class BackpackManager {

    private Backpack backpack;

    public BackpackManager() {
        load();
    }

    private void load() {
        FileConfiguration config = Main.getInstance().getConfig();
        String base64 = config.getString("backpack");

        if (base64 != null && !base64.isEmpty()) {
            try {
                backpack = new Backpack(base64);
            } catch (IOException e) {
                Bukkit.getLogger().info("Failed to load backpack from base64 string" + e);
            }
        } else {
            backpack = new Backpack();
        }
    }

    public void save() {
        FileConfiguration config = Main.getInstance().getConfig();
        config.set("backpack", backpack.toBase64());
        Main.getInstance().saveConfig();
    }
}