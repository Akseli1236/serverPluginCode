package org.Akseli.firstPlugin;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class PlayerData implements Listener {
    private static final Map<String, Player> players = new HashMap<>();
    private static final Map<String, Location> lastDeathLocations = new HashMap<>();
    private static final Map<String, Shoot> playerShoots = new HashMap<>();

    private final Plugin plugin;
    private static Weapon weapon = null;


    public PlayerData(Plugin plugin) {
        this.plugin = plugin;

    }

    public static Weapon getWeapon(){
        return weapon;
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event){
        Player player = event.getEntity();
        System.out.println(player.getLocation());

        lastDeathLocations.put(player.getName(), player.getLocation());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (weapon == null) {
            weapon = new Weapon();
            weapon.weaponUpdate(plugin);

        }
        Player player = event.getPlayer();
        if (!playerShoots.containsKey(player.getName())) {
            Shoot shoot = new Shoot(plugin);
            player.setGameMode(GameMode.ADVENTURE);
            shoot.loadWeapons(player.getInventory().getItemInMainHand(), player);
            playerShoots.put(player.getName(), shoot);
        }else {
            playerShoots.get(player.getName()).loadWeapons(player.getInventory().getItemInMainHand(), player);
        }








    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerShoots.remove(player.getName());
    }

    public static Map<String, Player> getPlayers() {
        return players;
    }

    public static Location getLastDeath(String playerName) {
        return lastDeathLocations.get(playerName);
    }

    public static Map<String, Shoot> getPlayerShoots() {
        return playerShoots;
    }


}
