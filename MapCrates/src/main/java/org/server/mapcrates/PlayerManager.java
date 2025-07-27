package org.server.mapcrates;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerManager implements EventListener {

    private Map<String, Player> serverPlayers = new HashMap<>();

    public PlayerManager() {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        serverPlayers.put(event.getPlayer().getName(), event.getPlayer());
    }

}
