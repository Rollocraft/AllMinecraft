package de.rollocraft.allminecraft.Listener;

import de.rollocraft.allminecraft.Manager.Timer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

// Noch nicht registriert!!!!!!!!!!

public class FoodLevelChangeListener implements Listener {
    Timer timer = Timer.getTimer();
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event  ) {

        if (!timer.isRunning()){
            event.setCancelled(true);
        }
    }
}
