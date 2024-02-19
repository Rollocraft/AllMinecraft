package de.rollocraft.allminecraft.Minecraft.Listener;

import de.rollocraft.allminecraft.Minecraft.Manager.TabListManager;
import de.rollocraft.allminecraft.Minecraft.Manager.BossBarManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;

public class PlayerJoinListener implements Listener {

    private final BossBarManager bossBarManager;
    private final TabListManager tabListManager;

    public PlayerJoinListener(BossBarManager bossBarManager, TabListManager tabListManager) {
        this.bossBarManager = bossBarManager;
        this.tabListManager = tabListManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        bossBarManager.getBossBar().addPlayer(player);
        tabListManager.updateTabList();

        // Array of join messages
        String[] joinMessages = {
                "hilft jetzt mit!",
                "hat sich uns angeschlossen!",
                "ist gerade aufgetaucht!",
                "ist hier, um zu gewinnen!",

                "canis hic nunc est filius",
                "hat bemerkt das es 19:00 Uhr ist",
                "ist dem Call beigetreten!",
                "ist bereit, zu sIeGeN!"

        };

        // Generate a random index
        int randomIndex = new Random().nextInt(joinMessages.length);

        // Set the join message
        event.setJoinMessage(ChatColor.GOLD + "" + event.getPlayer().getName() + " " + joinMessages[randomIndex]);
    }
}