package me.stormyzz.wanted.listeners;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.stormyzz.wanted.Wanted;
import me.stormyzz.wanted.dataManagers.MobSpawnerManager;
import me.stormyzz.wanted.gameModifications.MobSpawning;

public class MobSpawnListener implements Listener {

    private final MobSpawnerManager spawnerManager;
    private final Map<String, Player> playersMap = new HashMap<>();
    private final Map<String, MobSpawning> playerMobSpawning = new HashMap<>();
    private Wanted plugin;
    
    public MobSpawnListener(MobSpawnerManager spawnerManager, Wanted plugin) {
        this.spawnerManager = spawnerManager;
	this.plugin = plugin;
	
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
	playersMap.put(event.getPlayer().getName(), event.getPlayer());
	MobSpawning playerMobs = new MobSpawning(plugin, event.getPlayer(), spawnerManager);
	playerMobSpawning.put(event.getPlayer().getName(), playerMobs);
	playerMobs.startSpawning();
	
       
    }
    @EventHandler void onPlayerQuit(PlayerQuitEvent event){
	playerMobSpawning.get(event.getPlayer().getName()).stopSpawning();
	playerMobSpawning.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == SpawnReason.NATURAL) {
            event.setCancelled(true);
        }
    }
}
