package de.rollocraft.allminecraft.Minecraft.Listener.DisableWhileStop;

import de.rollocraft.allminecraft.Minecraft.Timer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final Timer timer;

    public BlockBreakListener(Timer timer) {
        this.timer = timer;
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        if (!timer.isRunning()) {
            event.setCancelled(true);
        }
    }
}
