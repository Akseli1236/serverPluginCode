package me.stormyzz.wanted.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;

public class MobBurnPreventionListener implements Listener {

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        Entity entity = event.getEntity();

        // Prevent burning for tagged mobs
        if ((entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.SKELETON)){
            event.setCancelled(true);
        }
    }
}
