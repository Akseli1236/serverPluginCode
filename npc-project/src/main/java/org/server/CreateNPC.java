package org.server;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class CreateNPC {

    private Plugin plugin;

    public CreateNPC(Plugin plugin) {
        this.plugin = plugin;
        spawnNPC();

    }

    private void spawnNPC() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "fullwall");
            Location newLocation = plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getLocation)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
            npc.spawn(newLocation);
        }, 0, 20 * 5);
    }
}
