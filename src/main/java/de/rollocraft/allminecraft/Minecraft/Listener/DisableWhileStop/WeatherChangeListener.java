package de.rollocraft.allminecraft.Minecraft.Listener.DisableWhileStop;
import de.rollocraft.allminecraft.Minecraft.Manager.TimerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherChangeListener implements Listener {
    private final TimerManager timerManager;

    public WeatherChangeListener(TimerManager timerManager) {
        this.timerManager = timerManager;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (!timerManager.isRunning()) {
            event.setCancelled(true);
        }
    }
}
