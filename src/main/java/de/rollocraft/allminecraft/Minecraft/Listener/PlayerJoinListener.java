package de.rollocraft.allminecraft.Minecraft.Listener;

import de.rollocraft.allminecraft.Minecraft.Manager.BossBarManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    Timer timer = Timer.getTimer;

    private final BossBarManager bossBarManager;

    public PlayerJoinListener(BossBarManager bossBarManager) {
        this.bossBarManager = bossBarManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!timer.isRunning){
            player.setTitle(ChatColor.RED + "Timer Stopped!", "", 1, 20, 1);
        }
        bossBarManager.getBossBar().addPlayer(player);
        event.setJoinMessage(null);
    }
}