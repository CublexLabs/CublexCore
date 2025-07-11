package io.github.devbd1.cublexcore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryOpenLogger implements Listener {

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        //System.out.println("[Corlex] Intercepting inventory open event: " + event.getEventName());
    }

}
