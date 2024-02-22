package de.rollocraft.allminecraft.Minecraft.Commands;

import de.rollocraft.allminecraft.Minecraft.Manager.ItemViewerManager;
import de.rollocraft.allminecraft.Minecraft.Manager.MobViewerManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ItemViewerCommand implements CommandExecutor {

    private final ItemViewerManager itemViewerManager;

    public ItemViewerCommand(ItemViewerManager itemViewerManager) {
        this.itemViewerManager = itemViewerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            itemViewerManager.openPage(player, 0);
            player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 1.0f);
            return true;
        } else {
            sender.sendMessage("You must be a player to use this command.");
            return false;
        }
    }
}
