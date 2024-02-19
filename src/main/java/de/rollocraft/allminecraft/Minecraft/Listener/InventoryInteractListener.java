package de.rollocraft.allminecraft.Minecraft.Listener;

import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Minecraft.Manager.BossBarManager;
import de.rollocraft.allminecraft.Minecraft.Database.ItemDatabaseManager;
import de.rollocraft.allminecraft.Minecraft.Manager.TabListManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class InventoryInteractListener implements Listener {
    private final TabListManager tabListManager;
    private final Main plugin;
    private final ItemDatabaseManager itemDatabaseManager;
    private final BossBarManager bossBarManager;

    public InventoryInteractListener(Main plugin, ItemDatabaseManager itemDatabaseManager, BossBarManager bossBarManager, TabListManager tablistManager) {
        this.plugin = plugin;
        this.itemDatabaseManager = itemDatabaseManager;
        this.bossBarManager = bossBarManager;
        this.tabListManager = tablistManager;
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack != null) {
                String itemName = itemStack.getType().name();
                try {
                    String currentItem = itemDatabaseManager.getCurrentItem();
                    if (itemName.equals(currentItem)) {
                        itemDatabaseManager.markItemAsDone(currentItem); // Mark the item as done
                        String newItem = itemDatabaseManager.getRandomItem();
                        itemDatabaseManager.setCurrentItem(newItem);// Set new random item
                        bossBarManager.updateBossBar(); // Update the boss bar
                        tabListManager.updateTabList();
                    }
                } catch (SQLException e) {
                    plugin.getLogger().severe("Failed to mark item as done or get a nwe Item: " + e.getMessage());
                }
            }
        }
    }
}
