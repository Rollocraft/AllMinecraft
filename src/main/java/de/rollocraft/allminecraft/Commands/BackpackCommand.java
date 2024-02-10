package de.rollocraft.allminecraft.Commands;

import de.rollocraft.allminecraft.Manager.Database.BackpackDatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BackpackCommand implements CommandExecutor {
    private Inventory backpack;
    private BackpackDatabaseManager dbManager;

    public BackpackCommand() {
        this.dbManager = new BackpackDatabaseManager();
        this.backpack = Bukkit.createInventory(null, 27, "Backpack");
        this.backpack.setContents(dbManager.loadBackpack(backpack.getSize()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        player.openInventory(backpack);
        return true;
    }

    public void saveBackpack() {
        dbManager.saveBackpack(backpack.getContents());
    }
    public Inventory getBackpack() {
        return backpack;
    }
}