package de.rollocraft.allminecraft.Minecraft.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Minecraft.Manager.BackpackManager;
import org.bukkit.inventory.Inventory;

public class BackpackCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        Inventory sharedBackpack = Main.getInstance().getSharedBackpack();

        player.openInventory(sharedBackpack);
        player.sendMessage(ChatColor.AQUA + "[Backpack] " + ChatColor.WHITE + "Backpack geöffnet!");
        return true;
    }
}