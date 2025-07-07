package org.server.gtamc;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class PlayerData implements Listener {
    private final Map<String, Player> players = new HashMap<>();
    private final Map<String, Location> lastDeathLocations = new HashMap<>();
    private final Map<String, Shoot> playerShoots = new HashMap<>();
    private final Map<String, ModifiedItemBehavior> usableItemBehaviors = new HashMap<>();
    private final Map<String, BukkitTask> removalTasks = new HashMap<>();

    private final Plugin plugin;
    private final Weapon weapon;
    private final WASD wasd;


    public PlayerData(Plugin plugin, WASD wasd) {
        weapon = new Weapon();
        weapon.weaponUpdate(plugin);
        this.plugin = plugin;
        this.wasd = wasd;

    }

    public Weapon getWeapon(){return weapon;}

    public Map<String, Shoot> getPlayerShoots() {
        return playerShoots;
    }

    public Map<String, ModifiedItemBehavior> getUsableItemBehaviors() {
        return usableItemBehaviors;
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event){
        Player player = event.getEntity();

        lastDeathLocations.put(player.getName(), player.getLocation());
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
	String playerName = player.getName();

	if (removalTasks.containsKey(playerName)){
	    removalTasks.get(playerName).cancel();
	    removalTasks.remove(playerName);
	}
	
        if (!playerShoots.containsKey(playerName)) {
            Shoot shoot = new Shoot(plugin, weapon, wasd);
            ModifiedItemBehavior modifiedItemBehavior = new ModifiedItemBehavior(plugin);
            shoot.loadWeapons(player.getInventory().getItemInMainHand(), player);
            playerShoots.put(playerName, shoot);
            players.put(playerName, player);
            usableItemBehaviors.put(playerName, modifiedItemBehavior);
        }else {
            playerShoots.get(playerName).loadWeapons(player.getInventory().getItemInMainHand(), player);
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
	String playerName = player.getName();
	plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
		playerShoots.remove(playerName);
	    }, 20L * 60 * 10);
	
    }

    public Player getPlayer(String playerName) {return players.get(playerName);}

    public Location getLastDeathLocation(String playerName) {return lastDeathLocations.get(playerName);}
}
