package de.rollocraft.allminecraft.Minecraft.Listener.DisableWhileStop;

import de.rollocraft.allminecraft.Minecraft.Timer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {
    private final Timer timer;

    public EntityDamageListener(Timer timer) {
        this.timer = timer;
    }

    @EventHandler
    public void onPlayerGetDamage(EntityDamageEvent event) {
        if (!timer.isRunning()) {
            event.setCancelled(true);
        }
    }
}