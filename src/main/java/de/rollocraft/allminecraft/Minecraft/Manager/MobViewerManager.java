package de.rollocraft.allminecraft.Minecraft.Manager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MobViewerManager {

    private final List<Material> spawnEggs = new ArrayList<>();
    private final int itemsPerPage = 45;

    public MobViewerManager() {
        for (Material material : Material.values()) {
            if (material.name().endsWith("_SPAWN_EGG")) {
                spawnEggs.add(material);
            }
        }
    }

    public void openPage(Player player, int page) {
        player.closeInventory(); // Close the inventory

        Inventory inv = player.getServer().createInventory(null, 54, "Mob Viewer - Page " + (page + 1));

        int start = page * itemsPerPage;
        for (int i = start; i < start + itemsPerPage && i < spawnEggs.size(); i++) {
            inv.addItem(new ItemStack(spawnEggs.get(i), 1));
        }

        // Add navigation items
        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW, 1);
            ItemMeta meta = prevPage.getItemMeta();
            meta.setDisplayName("Previous Page");
            meta.setLore(Arrays.asList("Current Page: " + (page + 1)));
            prevPage.setItemMeta(meta);
            inv.setItem(45, prevPage);
        }

        if ((page + 1) * itemsPerPage < spawnEggs.size()) {
            ItemStack nextPage = new ItemStack(Material.ARROW, 1);
            ItemMeta meta = nextPage.getItemMeta();
            meta.setDisplayName("Next Page");
            meta.setLore(Arrays.asList("Current Page: " + (page + 1)));
            nextPage.setItemMeta(meta);
            inv.setItem(53, nextPage);
        }

        player.openInventory(inv);
    }

    public List<Material> getSpawnEggs() {
        return spawnEggs;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }
}