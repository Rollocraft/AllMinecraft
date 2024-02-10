package de.rollocraft.allminecraft.Events;

import de.rollocraft.allminecraft.Commands.BackpackCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {
    private BackpackCommand backpackCommand;

    public InventoryListener(BackpackCommand backpackCommand) {
        this.backpackCommand = backpackCommand;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(backpackCommand.getBackpack())) {
            backpackCommand.saveBackpack();
        }
    }
}