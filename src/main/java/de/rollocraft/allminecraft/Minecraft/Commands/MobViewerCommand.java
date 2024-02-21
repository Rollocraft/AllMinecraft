package de.rollocraft.allminecraft.Minecraft.Commands;

import de.rollocraft.allminecraft.Minecraft.Manager.MobViewerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MobViewerCommand implements CommandExecutor {

    private final MobViewerManager mobViewerManager;

    public MobViewerCommand() {
        this.mobViewerManager = new MobViewerManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            mobViewerManager.openPage(player, 0);
            return true;
        } else {
            sender.sendMessage("You must be a player to use this command.");
            return false;
        }
    }
}