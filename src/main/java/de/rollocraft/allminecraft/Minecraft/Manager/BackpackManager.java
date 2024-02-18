package de.rollocraft.allminecraft.Minecraft.Manager;

import de.rollocraft.allminecraft.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class BackpackManager {

    private Backpack backpack;

    public BackpackManager() {
        load();
    }

    public Backpack getBackpack() {
        if (backpack == null) {
            backpack = new Backpack();
        }
        return backpack;
    }

    public void setBackpack(Backpack backpack) {
        this.backpack = backpack;
    }

    private void load() {
        FileConfiguration config = Main.getInstance().getConfig();
        String base64 = config.getString("backpack");

        if (base64 != null && !base64.isEmpty()) {
            try {
                backpack = new Backpack(base64);
            } catch (IOException e) {
                e.printStackTrace();
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