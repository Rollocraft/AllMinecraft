package de.rollocraft.allminecraft.Manager;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import de.rollocraft.allminecraft.utils.Base64;

import java.io.IOException;

public class Backpack {

    private final Inventory inventory;

    public Backpack() {
        this.inventory = Bukkit.createInventory(null, 27 /* multiples of 9 */, "Backpack");
    }

    public Backpack(String base64) throws IOException {
        this.inventory = Bukkit.createInventory(null, 27 /* multiples of 9 */, "Backpack");
        this.inventory.setContents(Base64.itemStackArrayFromBase64(base64));
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String toBase64() {
        return Base64.itemStackArrayToBase64(inventory.getContents());
    }
}