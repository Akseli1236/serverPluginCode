package org.Akseli.firstPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

public class RespawnAnimalOnDeath implements Listener {

    private final Plugin plugin;

    public RespawnAnimalOnDeath(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnimalDeath(EntityDeathEvent event) {

        if (event.getEntity() instanceof Animals) {
            Animals animal = (Animals) event.getEntity();
            EntityType type = animal.getType(); // Get the animal type
            Location location = animal.getLocation(); // Get the death location

            // Schedule respawn after a short delay
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // Respawn the animal at the death location
                location.getWorld().spawnEntity(location, type);
            }, 20L); // Delay is in ticks (20 ticks = 1 second)


        }

    }
}
