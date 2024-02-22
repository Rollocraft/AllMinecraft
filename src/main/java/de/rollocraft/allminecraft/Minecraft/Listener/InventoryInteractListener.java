package de.rollocraft.allminecraft.Minecraft.Listener;

import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Minecraft.Manager.*;
import de.rollocraft.allminecraft.Minecraft.Database.ItemDatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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
    private final TimerManager timerManager;
    private final ItemDatabaseManager itemDatabaseManager;
    private final BossBarManager bossBarManager;
    private final MobViewerManager mobViewerManager;
    private final ItemViewerManager itemViewerManager;

    public InventoryInteractListener(Main plugin, ItemDatabaseManager itemDatabaseManager, BossBarManager bossBarManager, TabListManager tablistManager, TimerManager timerManager, MobViewerManager mobViewerManager,ItemViewerManager itemViewerManager) {
        this.plugin = plugin;
        this.itemDatabaseManager = itemDatabaseManager;
        this.bossBarManager = bossBarManager;
        this.tabListManager = tablistManager;
        this.mobViewerManager = mobViewerManager;
        this.itemViewerManager = itemViewerManager;
        this.timerManager = timerManager;
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (!timerManager.isRunning()) {
            event.setCancelled(true);
            return;
        }
        ItemStack checkItem = event.getCurrentItem();
        if (checkItem == null) {
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
        /*
        !Below is the code for the Viewers! -> MobViewer, ItemViewer, SearchResults

         */
        if (event.getView().getTitle().startsWith("Mob Viewer")) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ARROW) {
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta.hasDisplayName() && meta.hasLore()) {
                    String displayName = meta.getDisplayName();
                    Player player = (Player) event.getWhoClicked();
                    String currentPageString = meta.getLore().get(0).split(": ")[1];
                    try {
                        int currentPage = Integer.parseInt(currentPageString) - 1;

                        if (displayName.equals("Previous Page") && currentPage > 0) {
                            mobViewerManager.openPage(player, currentPage - 1);
                        } else if (displayName.equals("Next Page") && (currentPage + 1) * mobViewerManager.getItemsPerPage() < mobViewerManager.getSpawnEggs().size()) {
                            mobViewerManager.openPage(player, currentPage + 1);
                        }
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().info("Could not parse page number: " + currentPageString);
                    }
                }
            }
        }

        //*Viewers
        //Item Viewer
        if (event.getView().getTitle().startsWith("Item Viewer")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();

            if (event.getCurrentItem() != null && (event.getCurrentItem().getType() == Material.ARROW )) {
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta.hasLore() && !meta.getLore().isEmpty()) {
                    String currentPageString = meta.getLore().get(0).split(": ")[1];
                    String displayName = meta.getDisplayName();

                    try {
                        int currentPage = Integer.parseInt(currentPageString) - 1;

                        if (displayName.equals("Previous Page") && currentPage > 0) {
                            itemViewerManager.openPage(player, currentPage - 1);
                        } else if (displayName.equals("Next Page") && (currentPage + 1) * itemViewerManager.getItemsPerPage() < itemViewerManager.getAllItems().size()) {
                            itemViewerManager.openPage(player, currentPage + 1);
                        }
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().info("Could not parse page number: " + currentPageString);
                    }
                }
            }
            if (event.getCurrentItem().getType() == Material.OAK_SIGN ||event.getCurrentItem().getType() == Material.HOPPER) {
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                String displayName = meta.getDisplayName();
                if (displayName.equals("Search")){

                    // Get the player's location
                    Location playerLocation = player.getLocation();

                    // Create a temporary sign at the player's x and z coordinates, at y=200
                    Block block = player.getWorld().getBlockAt(playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ());
                    block.setType(Material.OAK_SIGN);
                    Sign sign = (Sign) block.getState();
                    Bukkit.getLogger().info("Created temporary sign at " + block.getLocation());
                    // Force the player to edit the sign
                    player.openSign(sign);
                    Bukkit.getLogger().info("Opened sign editor for " + player.getName());

                }
                if (displayName.equals("Filter")) {
                    itemViewerManager.openOnlyFoundPage(player, 0);
                }
            }
        }

        //Done Items Viewer
        if (event.getView().getTitle().startsWith("Done Items Viewer")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();

            if (event.getCurrentItem() != null && (event.getCurrentItem().getType() == Material.ARROW)) {
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta.hasLore() && !meta.getLore().isEmpty()) {
                    String currentPageString = meta.getLore().get(0).split(": ")[1];
                    String displayName = meta.getDisplayName();

                    try {
                        int currentPage = Integer.parseInt(currentPageString) - 1;

                        if (displayName.equals("Previous Page") && currentPage > 0) {
                            itemViewerManager.openOnlyFoundPage(player, currentPage - 1);
                        } else if (displayName.equals("Next Page") && (currentPage + 1) * itemViewerManager.getItemsPerPage() < itemViewerManager.getAllItems().size()) {
                            itemViewerManager.openOnlyFoundPage(player, currentPage + 1);
                        }
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().info("Could not parse page number: " + currentPageString);
                    }
                }
            }
            if (event.getCurrentItem().getType() == Material.OAK_SIGN || event.getCurrentItem().getType() == Material.HOPPER) {
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                String displayName = meta.getDisplayName();
                if (displayName.equals("Search")) {

                    // Get the player's location
                    Location playerLocation = player.getLocation();

                    // Create a temporary sign at the player's x and z coordinates, at y=200
                    Block block = player.getWorld().getBlockAt(playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ());
                    block.setType(Material.OAK_SIGN);
                    Sign sign = (Sign) block.getState();
                    Bukkit.getLogger().info("Created temporary sign at " + block.getLocation());
                    // Force the player to edit the sign
                    player.openSign(sign);
                    Bukkit.getLogger().info("Opened sign editor for " + player.getName());

                }
                if (displayName.equals("Filter")) {
                    itemViewerManager.openOnlyNotFoundPage(player, 0);
                }
            }
        }

        //Missing Items Viewer
        if (event.getView().getTitle().startsWith("Missing Items Viewer")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();

            if (event.getCurrentItem() != null && (event.getCurrentItem().getType() == Material.ARROW)) {
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta.hasLore() && !meta.getLore().isEmpty()) {
                    String currentPageString = meta.getLore().get(0).split(": ")[1];
                    String displayName = meta.getDisplayName();

                    try {
                        int currentPage = Integer.parseInt(currentPageString) - 1;

                        if (displayName.equals("Previous Page") && currentPage > 0) {
                            itemViewerManager.openOnlyNotFoundPage(player, currentPage - 1);
                        } else if (displayName.equals("Next Page") && (currentPage + 1) * itemViewerManager.getItemsPerPage() < itemViewerManager.getAllItems().size()) {
                            itemViewerManager.openOnlyNotFoundPage(player, currentPage + 1);
                        }
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().info("Could not parse page number: " + currentPageString);
                    }
                }
            }
            if (event.getCurrentItem().getType() == Material.OAK_SIGN || event.getCurrentItem().getType() == Material.HOPPER) {
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                String displayName = meta.getDisplayName();
                if (displayName.equals("Search")) {

                    // Get the player's location
                    Location playerLocation = player.getLocation();

                    // Create a temporary sign at the player's x and z coordinates, at y=200
                    Block block = player.getWorld().getBlockAt(playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ());
                    block.setType(Material.OAK_SIGN);
                    Sign sign = (Sign) block.getState();
                    Bukkit.getLogger().info("Created temporary sign at " + block.getLocation());
                    // Force the player to edit the sign
                    player.openSign(sign);
                    Bukkit.getLogger().info("Opened sign editor for " + player.getName());

                }
                if (displayName.equals("Filter")) {
                    itemViewerManager.openPage(player, 0);
                }
            }
        }

        //Search Results
        if (event.getView().getTitle().startsWith("Search Results")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            if (event.getCurrentItem() != null && (event.getCurrentItem().getType() == Material.BARRIER)) {
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta.hasLore() && !meta.getLore().isEmpty()) {
                    String currentPageString = meta.getLore().get(0).split(": ")[1];
                    String displayName = meta.getDisplayName();

                    try {
                        int currentPage = Integer.parseInt(currentPageString) - 1;

                        if (displayName.equals("Go Back")) {
                            itemViewerManager.openPage(player, 0);
                        }
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().info("Could not parse page number: " + currentPageString);
                    }
                }
            }
        }
        //Viewers*
    }
}
