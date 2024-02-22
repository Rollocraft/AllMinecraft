package de.rollocraft.allminecraft.Minecraft.Commands;
import de.rollocraft.allminecraft.Minecraft.Manager.BackpackManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveAllCommand implements CommandExecutor {
    private final BackpackManager backpackManager;

    public SaveAllCommand(BackpackManager backpackManager) {
        this.backpackManager = backpackManager;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("allminecraft.command.save-allminecraft")) {
                player.sendMessage("You saved all minecraft!");
                return true;
            } else {
                player.sendMessage("You don't have permission to use this command.");
                return true;
            }
        }

        return false;
    }
}