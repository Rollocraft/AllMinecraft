package de.rollocraft.allminecraft.Minecraft.Listener;

import de.rollocraft.allminecraft.Minecraft.Manager.BossBarManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final BossBarManager bossBarManager;

    public PlayerJoinListener(BossBarManager bossBarManager) {
        this.bossBarManager = bossBarManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        bossBarManager.getBossBar().addPlayer(player);
        event.setJoinMessage(null);
    }
}