package de.rollocraft.allminecraft.Minecraft.Listener;

import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Minecraft.Manager.BossBarManager;
import de.rollocraft.allminecraft.Minecraft.Manager.TabListManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import de.rollocraft.allminecraft.Minecraft.Database.ItemDatabaseManager;

import java.sql.SQLException;

public class PlayerPickupListener implements Listener {
    private final Main plugin;
    private final ItemDatabaseManager itemDatabaseManager;
    private final BossBarManager bossBarManager;
    private final TabListManager tabListManager;

    public PlayerPickupListener(Main plugin, ItemDatabaseManager itemdatabaseManager, BossBarManager bossBarManager, TabListManager tabListManager) {
        this.plugin = plugin;
        this.itemDatabaseManager = itemdatabaseManager;
        this.bossBarManager = bossBarManager;
        this.tabListManager = tabListManager;
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack itemStack = event.getItem().getItemStack();
            if (itemStack != null) {
                String itemName = itemStack.getType().name();
                try {
                    String currentItem = itemDatabaseManager.getCurrentItem(); // Get the current item from the database
                    if (itemName.equals(currentItem)) {
                        itemDatabaseManager.markItemAsDone(itemName);
                        String newItem = itemDatabaseManager.getRandomItem();
                        itemDatabaseManager.setCurrentItem(newItem);// Set new random item
                        bossBarManager.updateBossBar(); // Update the boss bar
                        tabListManager.updateTabList();

                    }
                } catch (SQLException e) {
                    plugin.getLogger().severe("Failed to mark item as done: " + e.getMessage());
                }
            }
        }
    }
}