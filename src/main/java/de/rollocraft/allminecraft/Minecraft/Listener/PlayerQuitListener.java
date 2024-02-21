package de.rollocraft.allminecraft.Minecraft.Listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Random;

public class PlayerQuitListener implements Listener {


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String[] quitMessages = {
                "hat das Spiel verlassen",
                "ist gegangen",
                "hat bemerkt das es 22:00 Uhr ist",
                "ist dem Call geleavet!"

        };

        // Generate a random index
        int randomIndex = new Random().nextInt(quitMessages.length);

        // Set the join message
        event.setQuitMessage(ChatColor.GOLD + event.getPlayer().getName() + " " + quitMessages[randomIndex]);
    }
}
