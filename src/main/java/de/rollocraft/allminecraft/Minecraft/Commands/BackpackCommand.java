package de.rollocraft.allminecraft.Minecraft.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Minecraft.Manager.Backpack;

public class BackpackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        Backpack backpack = Main.getInstance().getSharedBackpack();

        player.openInventory(backpack.getInventory());
        player.sendMessage(ChatColor.AQUA + "[Backpack] " + ChatColor.WHITE)
        return true;
    }
}