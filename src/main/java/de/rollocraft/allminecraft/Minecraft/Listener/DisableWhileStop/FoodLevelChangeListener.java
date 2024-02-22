package de.rollocraft.allminecraft.Minecraft.Listener.DisableWhileStop;

import de.rollocraft.allminecraft.Minecraft.Manager.TimerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {
    private final TimerManager timerManager;

    public FoodLevelChangeListener(TimerManager timerManager) {
        this.timerManager = timerManager;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {

        if (!timerManager.isRunning()) {
            event.setCancelled(true);
        }
    }
}
