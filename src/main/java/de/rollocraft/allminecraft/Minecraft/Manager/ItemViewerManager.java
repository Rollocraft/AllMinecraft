package de.rollocraft.allminecraft.Minecraft.Manager;

import de.rollocraft.allminecraft.Minecraft.Database.ItemDatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemViewerManager {

    private final List<Material> allItems = new ArrayList<>();
    private final List<Material> doneItems = new ArrayList<>();
    private final List<Material> notDoneItems = new ArrayList<>();
    private final int itemsPerPage = 45;
    private final ItemDatabaseManager itemDatabaseManager;

    public ItemViewerManager(ItemDatabaseManager itemDatabaseManager) {
        this.itemDatabaseManager = itemDatabaseManager;
        updateLists();
    }
    public void updateLists() {
        allItems.clear();
        doneItems.clear();
        notDoneItems.clear();
        try {
            List<String> allItemNames = itemDatabaseManager.getAllItemNames();
            for (String itemName : allItemNames) {
                Material material = Material.getMaterial(itemName);
                if (material != null) {
                    allItems.add(material);
                }
            }
            List<String> doneItemsNames = itemDatabaseManager.getDoneItemNames();
            for (String itemName : doneItemsNames) {
                Material material = Material.getMaterial(itemName);
                if (material != null) {
                    doneItems.add(material);
                }
            }
            List<String> notDoneItemsNames = itemDatabaseManager.getNotDoneItemNames();
            for (String itemName : notDoneItemsNames) {
                Material material = Material.getMaterial(itemName);
                if (material != null) {
                    notDoneItems.add(material);
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to load items from the database: " + e.getMessage());
        }
    }

    public void openPage(Player player, int page) {
        updateLists();
        Inventory inv = player.getServer().createInventory(null, 54, "Item Viewer - Page " + (page + 1));

        int start = page * itemsPerPage;
        for (int i = start; i < start + itemsPerPage && i < allItems.size(); i++) {
            Material itemMaterial = allItems.get(i);
            String itemName = itemMaterial.name();
            ItemStack item = new ItemStack(itemMaterial, 1);
            ItemMeta meta = item.getItemMeta();
            String displayName = itemName.replace("_", " ").toLowerCase();
            displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);
            try {
                if (itemDatabaseManager.isItemDone(itemName)) {
                    meta.setDisplayName(displayName + " - " + ChatColor.GREEN + "Found");
                } else {
                    meta.setDisplayName(displayName + " - " + ChatColor.RED + "Not Found");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            item.setItemMeta(meta);
            inv.addItem(item);
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

        if ((page + 1) * itemsPerPage < allItems.size()) {
            ItemStack nextPage = new ItemStack(Material.ARROW, 1);
            ItemMeta meta = nextPage.getItemMeta();
            meta.setDisplayName("Next Page");
            meta.setLore(Arrays.asList("Current Page: " + (page + 1)));
            nextPage.setItemMeta(meta);
            inv.setItem(53, nextPage);
        }
        // Add reset Barrier


        // Add search sign
        ItemStack searchSign = new ItemStack(Material.OAK_SIGN, 1);
        ItemMeta searchMeta = searchSign.getItemMeta();
        searchMeta.setDisplayName("Search");
        searchMeta.setLore(Arrays.asList("Search for items"));
        searchSign.setItemMeta(searchMeta);
        inv.setItem(49, searchSign); // Set the search sign at slot 49

        // Add filter hopper
        ItemStack filterHopper = new ItemStack(Material.HOPPER, 1);
        ItemMeta filterMeta = filterHopper.getItemMeta();
        filterMeta.setDisplayName("Filter");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "-> All Items");
        lore.add(ChatColor.WHITE + "Done Items");
        lore.add(ChatColor.WHITE + "Missing Items");
        filterMeta.setLore(lore);
        filterHopper.setItemMeta(filterMeta);
        inv.setItem(50, filterHopper); // Set the filter hopper at slot 50

        player.openInventory(inv);
    }
    public void openOnlyFoundPage(Player player, int page) {
        updateLists();

        Inventory inv = player.getServer().createInventory(null, 54, "Done Items Viewer - Page " + (page + 1));

        int start = page * itemsPerPage;
        for (int i = start; i < start + itemsPerPage && i < doneItems.size(); i++) {
            Material itemMaterial = doneItems.get(i);
            String itemName = itemMaterial.name();
            ItemStack item = new ItemStack(itemMaterial, 1);
            ItemMeta meta = item.getItemMeta();
            String displayName = itemName.replace("_", " ").toLowerCase();
            displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);
            try {
                if (itemDatabaseManager.isItemDone(itemName)) {
                    meta.setDisplayName(displayName + " - " + ChatColor.GREEN + "Found");
                } else {
                    meta.setDisplayName(displayName + " - " + ChatColor.RED + "Not Found");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            item.setItemMeta(meta);
            inv.addItem(item);
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

        if ((page + 1) * itemsPerPage < allItems.size()) {
            ItemStack nextPage = new ItemStack(Material.ARROW, 1);
            ItemMeta meta = nextPage.getItemMeta();
            meta.setDisplayName("Next Page");
            meta.setLore(Arrays.asList("Current Page: " + (page + 1)));
            nextPage.setItemMeta(meta);
            inv.setItem(53, nextPage);
        }
        // Add reset Barrier


        // Add search sign
        ItemStack searchSign = new ItemStack(Material.OAK_SIGN, 1);
        ItemMeta searchMeta = searchSign.getItemMeta();
        searchMeta.setDisplayName("Search");
        searchMeta.setLore(Arrays.asList("Search for items"));
        searchSign.setItemMeta(searchMeta);
        inv.setItem(49, searchSign); // Set the search sign at slot 49

        // Add filter hopper
        ItemStack filterHopper = new ItemStack(Material.HOPPER, 1);
        ItemMeta filterMeta = filterHopper.getItemMeta();
        filterMeta.setDisplayName("Filter");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "All Items");
        lore.add(ChatColor.YELLOW + "-> Done Items");
        lore.add(ChatColor.WHITE + "Missing Items");
        filterMeta.setLore(lore);
        filterHopper.setItemMeta(filterMeta);
        inv.setItem(50, filterHopper); // Set the filter hopper at slot 50

        player.openInventory(inv);
    }
    public void openOnlyNotFoundPage(Player player, int page) {
        updateLists();

        Inventory inv = player.getServer().createInventory(null, 54, "Missing Items Viewer - Page " + (page + 1));

        int start = page * itemsPerPage;
        for (int i = start; i < start + itemsPerPage && i < notDoneItems.size(); i++) {
            Material itemMaterial = notDoneItems.get(i);
            String itemName = itemMaterial.name();
            ItemStack item = new ItemStack(itemMaterial, 1);
            ItemMeta meta = item.getItemMeta();
            String displayName = itemName.replace("_", " ").toLowerCase();
            displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);
            try {
                if (itemDatabaseManager.isItemDone(itemName)) {
                    meta.setDisplayName(displayName + " - " + ChatColor.GREEN + "Found");
                } else {
                    meta.setDisplayName(displayName + " - " + ChatColor.RED + "Not Found");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            item.setItemMeta(meta);
            inv.addItem(item);
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

        if ((page + 1) * itemsPerPage < allItems.size()) {
            ItemStack nextPage = new ItemStack(Material.ARROW, 1);
            ItemMeta meta = nextPage.getItemMeta();
            meta.setDisplayName("Next Page");
            meta.setLore(Arrays.asList("Current Page: " + (page + 1)));
            nextPage.setItemMeta(meta);
            inv.setItem(53, nextPage);
        }
        // Add reset Barrier


        // Add search sign
        ItemStack searchSign = new ItemStack(Material.OAK_SIGN, 1);
        ItemMeta searchMeta = searchSign.getItemMeta();
        searchMeta.setDisplayName("Search");
        searchMeta.setLore(Arrays.asList("Search for items"));
        searchSign.setItemMeta(searchMeta);
        inv.setItem(49, searchSign); // Set the search sign at slot 49

        // Add filter hopper
        ItemStack filterHopper = new ItemStack(Material.HOPPER, 1);
        ItemMeta filterMeta = filterHopper.getItemMeta();
        filterMeta.setDisplayName("Filter");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "All Items");
        lore.add(ChatColor.WHITE + "Done Items");
        lore.add(ChatColor.YELLOW + "-> Missing Items");
        filterMeta.setLore(lore);
        filterHopper.setItemMeta(filterMeta);
        inv.setItem(50, filterHopper); // Set the filter hopper at slot 50

        player.openInventory(inv);
    }

    public List getAllItems() {
        return allItems;
    }
    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void openSearchResultsPage(Player player, List<Material> searchResults, int page) {
        Bukkit.getLogger().info("Opening search results page " + (page + 1) + " for " + player.getName());

        Inventory inv = player.getServer().createInventory(null, 54, "Search Results - Page " + (page + 1));

        int start = page * itemsPerPage;
        for (int i = start; i < start + itemsPerPage && i < searchResults.size(); i++) {
            Material itemMaterial = searchResults.get(i);
            String itemName = itemMaterial.name();
            ItemStack item = new ItemStack(itemMaterial, 1);
            ItemMeta meta = item.getItemMeta();
            String displayName = itemName.replace("_", " ").toLowerCase();
            displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);
            try {
                if (itemDatabaseManager.isItemDone(itemName)) {
                    meta.setDisplayName(displayName + " - " + ChatColor.GREEN + "Found");
                } else {
                    meta.setDisplayName(displayName + " - " + ChatColor.RED + "Not Found");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            item.setItemMeta(meta);
            inv.addItem(item);
        }

        // Add close
        ItemStack closeItem = new ItemStack(Material.BARRIER, 1);
        ItemMeta searchMeta = closeItem.getItemMeta();
        searchMeta.setDisplayName("Go Back");
        searchMeta.setLore(Arrays.asList("Go back to the Main menu"));
        closeItem.setItemMeta(searchMeta);
        inv.setItem(49, closeItem); // Set the search sign at slot 49

        player.openInventory(inv);
    }

}