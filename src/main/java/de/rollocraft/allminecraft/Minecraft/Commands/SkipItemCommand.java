package de.rollocraft.allminecraft.Minecraft.Commands;

import de.rollocraft.allminecraft.Minecraft.Manager.BossBarManager;
import de.rollocraft.allminecraft.Minecraft.Manager.TabListManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.rollocraft.allminecraft.Minecraft.Database.ItemDatabaseManager;

import java.sql.SQLException;

public class SkipItemCommand implements CommandExecutor {

    private final ItemDatabaseManager itemDatabaseManager;
    private final BossBarManager bossBarManager;
    private final TabListManager tabListManager;

    public SkipItemCommand(BossBarManager bossBarManager, ItemDatabaseManager itemDatabaseManager, TabListManager tabListManager) {
        this.bossBarManager = bossBarManager;
        this.itemDatabaseManager = itemDatabaseManager;
        this.tabListManager = tabListManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            try {
                String currentItem = itemDatabaseManager.getCurrentItem();
                itemDatabaseManager.markItemAsDone(currentItem);
                String newItem = itemDatabaseManager.getRandomItem();
                itemDatabaseManager.setCurrentItem(newItem);// Set new random item
                bossBarManager.updateBossBar(); // Update the boss bar
                tabListManager.updateTabList();
                sender.sendMessage(ChatColor.AQUA + "[All Items]" + ChatColor.WHITE + " Skipped item!");
                return true;
            } catch (SQLException e) {
                sender.sendMessage(ChatColor.RED + "Failed to mark item as done: " + e.getMessage()); // Send error message to the player !Console wäre besser! -> Überarbeiten
                return false;
            }
        } else {
            sender.sendMessage("Only players can use this command!");
            return false;
        }
    }
}