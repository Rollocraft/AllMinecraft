package de.rollocraft.allminecraft.Manager;

import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.utils.Config;

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
        Config config = Main.getInstance().getConfiguration();
        String base64 = config.getConfig().getString("backpack");

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
        Config config = Main.getInstance().getConfiguration();
        config.getConfig().set("backpack", backpack.toBase64());
    }
}