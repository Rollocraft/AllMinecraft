package de.rollocraft.allminecraft.Minecraft.Listener.DisableWhileStop;

import de.rollocraft.allminecraft.Minecraft.Timer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    private final Timer timer;

    public PlayerInteractListener(Timer timer) {
        this.timer = timer;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!timer.isRunning()) {
            event.setCancelled(true);
        }
    }
}