package de.rollocraft.allminecraft.Minecraft;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import de.rollocraft.allminecraft.Main;

import java.util.concurrent.TimeUnit;


public class Timer {

    private boolean running; // true or false
    private int time;

    public Timer(int loadedTime) {
        this.time = loadedTime;
        this.running = false;
        run();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    private static Timer timer;

    public void sendActionBar() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            if (!isRunning()) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED +
                        "Timer ist pausiert"));
                continue;
            }

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString() +
                    ChatColor.BOLD + formatTime()));
        }
    }

    public static Timer getTimer() {
        return timer;
    }

    public String formatTime() {
        long totalSeconds = getTime();
        long days = TimeUnit.SECONDS.toDays(totalSeconds);
        long hours = TimeUnit.SECONDS.toHours(totalSeconds) - (days * 24);
        long minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) - (TimeUnit.SECONDS.toHours(totalSeconds) * 60);
        long seconds = totalSeconds - (TimeUnit.SECONDS.toMinutes(totalSeconds) * 60);

        StringBuilder time = new StringBuilder();
        if (days > 0) {
            if (days == 1) {
                time.append(days).append(" day, ");
            } else {
                time.append(days).append(" days, ");
            }
        }
        if (hours > 0) {
            if (hours < 10) {
                time.append("0").append(hours).append(":");
            } else {
                time.append(hours).append(":");
            }
        }
        if (minutes > 0) {
            if (minutes < 10) {
                time.append("0").append(minutes).append(":");
            } else {
                time.append(minutes).append(":");
            }
        }
        if (seconds < 10) {
            time.append("0").append(seconds);
        } else {
            time.append(seconds).append("");
        }

        return time.toString();
    }

    private void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                sendActionBar();
                if (!isRunning()) {
                    return;
                }
                setTime(getTime() + 1);
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }
}