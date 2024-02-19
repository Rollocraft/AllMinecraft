package de.rollocraft.allminecraft.Minecraft.Listener;

import de.rollocraft.allminecraft.Minecraft.Timer;
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
