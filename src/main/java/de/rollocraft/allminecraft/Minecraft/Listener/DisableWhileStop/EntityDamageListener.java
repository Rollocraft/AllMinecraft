package de.rollocraft.allminecraft.Minecraft.Listener.DisableWhileStop;

import de.rollocraft.allminecraft.Minecraft.Manager.TimerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {
    private final TimerManager timerManager;

    public EntityDamageListener(TimerManager timerManager) {
        this.timerManager = timerManager;
    }

    @EventHandler
    public void onPlayerGetDamage(EntityDamageEvent event) {
        if (!timerManager.isRunning()) {
            event.setCancelled(true);
        }
    }
}