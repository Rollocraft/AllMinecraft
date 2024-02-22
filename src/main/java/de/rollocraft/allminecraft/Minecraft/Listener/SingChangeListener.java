package de.rollocraft.allminecraft.Minecraft.Listener;

import de.rollocraft.allminecraft.Minecraft.Database.ItemDatabaseManager;
import de.rollocraft.allminecraft.Minecraft.Manager.ItemViewerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SingChangeListener implements Listener {
    private final ItemViewerManager itemViewerManager;
    private final ItemDatabaseManager itemDatabaseManager;
    List<Material> matchingItems = new ArrayList<>();

    public SingChangeListener(ItemDatabaseManager itemDatabaseManager, ItemViewerManager itemViewerManager) {
        this.itemViewerManager = itemViewerManager;
        this.itemDatabaseManager = itemDatabaseManager;
    }
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Bukkit.broadcastMessage("SignChangeEvent");
        Player player = event.getPlayer();

        // Get the text the player entered into the sign
        String searchText = event.getLine(0);

        // Declare the list outside the try block
        List<Material> matchingItems = new ArrayList<>();

        try {
            matchingItems = itemDatabaseManager.searchItems(searchText);

            // Display the search results to the player
            if (matchingItems.isEmpty()) {
                player.sendMessage(ChatColor.AQUA + "[AllItems] " + ChatColor.RED + "No items found matching '" + searchText + "'.");
                Block block = event.getBlock();
                block.setType(Material.AIR);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage("An error occurred while searching for items.");
        }

        // Now you can access matchingItems outside the try block
        if (matchingItems.size() > 50) {
            player.sendMessage(ChatColor.AQUA + "[AllItems] " + ChatColor.RED + "Too many items found. Please refine your search.");
            Block block = event.getBlock();
            block.setType(Material.AIR);
            return;
        } else {
            player.sendMessage(ChatColor.AQUA + "[AllItems] " + ChatColor.WHITE + "Found " + matchingItems.size() + " items matching " + searchText);
            itemViewerManager.openSearchResultsPage(player, matchingItems, 0);
            Block block = event.getBlock();
            block.setType(Material.AIR);
        }
    }
}
