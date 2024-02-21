package de.rollocraft.allminecraft.Minecraft.Listener;

import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Minecraft.Manager.BossBarManager;
import de.rollocraft.allminecraft.Minecraft.Database.ItemDatabaseManager;
import de.rollocraft.allminecraft.Minecraft.Manager.MobViewerManager;
import de.rollocraft.allminecraft.Minecraft.Manager.TabListManager;
import de.rollocraft.allminecraft.Minecraft.Timer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;

public class InventoryInteractListener implements Listener {
    private final TabListManager tabListManager;
    private final Main plugin;
    private final Timer timer;
    private final ItemDatabaseManager itemDatabaseManager;
    private final BossBarManager bossBarManager;
    private final MobViewerManager mobViewerManager;

    public InventoryInteractListener(Main plugin, ItemDatabaseManager itemDatabaseManager, BossBarManager bossBarManager, TabListManager tablistManager, Timer timer, MobViewerManager mobViewerManager) {
        this.plugin = plugin;
        this.itemDatabaseManager = itemDatabaseManager;
        this.bossBarManager = bossBarManager;
        this.tabListManager = tablistManager;
        this.mobViewerManager = mobViewerManager;
        this.timer = timer;
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (!timer.isRunning()) {
            event.setCancelled(true);
            return;
        }
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
                        plugin.getServer().broadcastMessage(ChatColor.AQUA + "[AllItems] " + ChatColor.WHITE + "Das Item: " + itemDatabaseManager.getDisplayName(currentItem) + ", wurde von " + player.getName() + " gefunden!");
                        itemDatabaseManager.setCurrentItem(newItem);// Set new random item
                        bossBarManager.updateBossBar(); // Update the boss bar
                        tabListManager.updateTabList();
                    }
                } catch (SQLException e) {
                    plugin.getLogger().severe("Failed to mark item as done or get a new Item: " + e.getMessage());
                }
            }
        }
        if (event.getView().getTitle().startsWith("Mob Viewer")) {
            Bukkit.getLogger().info("Clicked in mob viewer");
            event.setCancelled(true);

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ARROW) {
                Bukkit.getLogger().info("Clicked on arrow");
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta.hasDisplayName() && meta.hasLore()) {
                    Bukkit.getLogger().info("Arrow has display name");
                    String displayName = meta.getDisplayName();
                    Player player = (Player) event.getWhoClicked();
                    String currentPageString = meta.getLore().get(0).split(": ")[1];
                    try {
                        int currentPage = Integer.parseInt(currentPageString) - 1;
                        Bukkit.getLogger().info("Current page: " + currentPage);

                        if (displayName.equals("Previous Page") && currentPage > 0) {
                            Bukkit.getLogger().info("Previous page");
                            mobViewerManager.openPage(player, currentPage - 1);
                        } else if (displayName.equals("Next Page") && (currentPage + 1) * mobViewerManager.getItemsPerPage() < mobViewerManager.getSpawnEggs().size()) {
                            Bukkit.getLogger().info("Next page");
                            mobViewerManager.openPage(player, currentPage + 1);
                        }
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().info("Could not parse page number: " + currentPageString);
                    }
                }
            }
        }
    }
}
