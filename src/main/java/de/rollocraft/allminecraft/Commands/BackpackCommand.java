package de.rollocraft.allminecraft.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Manager.Backpack;

public class BackpackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        Backpack backpack = Main.getInstance().getBackpackManager().getBackpack(player.getUniqueId());

        player.openInventory(backpack.getInventory());
        return true;
    }

}