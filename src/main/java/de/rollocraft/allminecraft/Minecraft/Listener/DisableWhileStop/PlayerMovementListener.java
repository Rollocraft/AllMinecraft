package de.rollocraft.allminecraft.Minecraft.Listener.DisableWhileStop;

import de.rollocraft.allminecraft.Minecraft.Manager.TimerManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener implements Listener {
    private final TimerManager timerManager;

    public PlayerMovementListener(TimerManager timerManager) {
        this.timerManager = timerManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!timerManager.isRunning()) {
            Location from = event.getFrom();
            Location to = event.getTo();

            // Check if the player has moved
            if (to != null && (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ())) {
                Player player = event.getPlayer();
                player.sendTitle(ChatColor.RED + "Der Timer ist gestoppt!", "Warte bis er weiter läuft!", 0, 100, 20);
                event.setCancelled(true);
            }
        }
    }
}