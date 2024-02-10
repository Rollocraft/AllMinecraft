package de.rollocraft.allminecraft.Commands;

import de.rollocraft.allminecraft.Manager.BossBarManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.rollocraft.allminecraft.Manager.Database.ItemDatabaseManager;

import java.sql.SQLException;

public class SkipItemCommand implements CommandExecutor {

    private final ItemDatabaseManager databaseManager;
    private final BossBarManager bossBarManager;

    public SkipItemCommand(BossBarManager bossBarManager, ItemDatabaseManager databaseManager) {
        this.bossBarManager = bossBarManager;
        this.databaseManager = databaseManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            String currentItem = bossBarManager.getCurrentItem();
            try {
                databaseManager.markItemAsDone(currentItem);
                bossBarManager.updateBossBar(); // Update the boss bar
            }catch (SQLException e) {
                sender.sendMessage(ChatColor.RED + "Failed to mark item as done: " + e.getMessage()); // Send error message to the player !Console wäre besser! -> Überarbeiten
                return false;
            }
            sender.sendMessage(ChatColor.AQUA + "[All Items]" + ChatColor.WHITE + " Skipped item!");
            return true;
        } else {
            sender.sendMessage("Only players can use this command!");
            return false;
        }
    }
}