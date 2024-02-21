package de.rollocraft.allminecraft.Minecraft.Listener.DisableWhileStop;
import de.rollocraft.allminecraft.Minecraft.Timer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherChangeListener implements Listener {
    private final Timer timer;

    public WeatherChangeListener(Timer timer) {
        this.timer = timer;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (!timer.isRunning()) {
            event.setCancelled(true);
        }
    }
}
