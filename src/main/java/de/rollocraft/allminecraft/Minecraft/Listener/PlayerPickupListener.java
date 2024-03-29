package de.rollocraft.allminecraft.Minecraft.Listener;

import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Minecraft.Manager.BossBarManager;
import de.rollocraft.allminecraft.Minecraft.Manager.TabListManager;
import de.rollocraft.allminecraft.Minecraft.Manager.TimerManager;
import org.bukkit.ChatColor;
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
    private final TimerManager timerManager;

    public PlayerPickupListener(Main plugin, ItemDatabaseManager itemdatabaseManager, BossBarManager bossBarManager, TabListManager tabListManager, TimerManager timerManager) {
        this.plugin = plugin;
        this.itemDatabaseManager = itemdatabaseManager;
        this.bossBarManager = bossBarManager;
        this.tabListManager = tabListManager;
        this.timerManager = timerManager;
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!timerManager.isRunning()) {
            event.setCancelled(true);
            return;
        }
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack itemStack = event.getItem().getItemStack();
            String itemName = itemStack.getType().name();
            try {
                String currentItem = itemDatabaseManager.getCurrentItem(); // Get the current item from the database
                if (itemName.equals(currentItem)) {
                    itemDatabaseManager.markItemAsDone(itemName);
                    String newItem = itemDatabaseManager.getRandomItem();
                    plugin.getServer().broadcastMessage(ChatColor.AQUA + "[AllItems] " + ChatColor.WHITE + "Das Item: " + itemDatabaseManager.getDisplayName(currentItem) + ", wurde von " + player.getName() + " gefunden!");
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