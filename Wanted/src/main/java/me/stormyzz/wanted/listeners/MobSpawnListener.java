package me.stormyzz.wanted.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
	if (event.getSpawnReason() == SpawnReason.NATURAL){
	    event.setCancelled(true);
	}
        //if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;
	
        // Entity entity = event.getEntity();
        // Location loc = entity.getLocation();
        // boolean allowed = false;
	

        // for (MobSpawnConfig config : spawnerManager.getSpawnConfigs()) {
        //     // if (!loc.getWorld().getName().equalsIgnoreCase(config.getWorldName())) continue;
        //     // if (!isLocationInRegion(loc, config.getRegionName())) continue;

        //     // Check Y constraints
        //     if (loc.getBlockY() < config.getMinY() || loc.getBlockY() > config.getMaxY()) continue;

        //     // Sky exposure check
        //     if (config.isRequireSky()) {
        //         int highest = loc.getWorld().getHighestBlockYAt(loc);
        //         if (highest > loc.getBlockY()) continue;
        //     }

        //     // Check allowed mobs
        //     Map<EntityType, MobSpawnConfig.MobSettings> allowedMobs = config.getAllowedMobsWithSettings();
        //     MobSpawnConfig.MobSettings mobSettings = allowedMobs.get(entity.getType());

        //     if (mobSettings == null) continue;

        //     // Allowed spawn found, apply settings
        //     allowed = true;

        //     // Set custom name
        //     if (mobSettings.getCustomName() != null && entity instanceof LivingEntity living) {
        //         living.setCustomName(ChatColor.translateAlternateColorCodes('&', mobSettings.getCustomName()));
        //         living.setCustomNameVisible(true);
        //     }

        //     // Armor and item restrictions
        //     if (entity instanceof LivingEntity livingEntity) {
        //         EntityEquipment equipment = livingEntity.getEquipment();
        //         if (equipment != null) {
        //             if (!mobSettings.isAllowArmor()) {
        //                 equipment.setHelmet(new MaterialData(Material.AIR).toItemStack(1));
        //                 equipment.setChestplate(new MaterialData(Material.AIR).toItemStack(1));
        //                 equipment.setLeggings(new MaterialData(Material.AIR).toItemStack(1));
        //                 equipment.setBoots(new MaterialData(Material.AIR).toItemStack(1));
        //             }
        //             if (!mobSettings.isAllowItems()) {
        //                 equipment.setItemInMainHand(new MaterialData(Material.AIR).toItemStack(1));
        //                 equipment.setItemInOffHand(new MaterialData(Material.AIR).toItemStack(1));
        //             }
        //         }
        //     }

        //     // Stop after first match
        //     break;
        // }


	// for (String player : playersMap.keySet()){
	//     Player playerClass = playersMap.get(player);
	//     Location spawnLocation = spawnEntityAroundPlayer(playerClass, 30.0);
	//     if (spawnLocation != null){
	// 	entity.teleport(spawnLocation);
	//     }
	//     break;
	// }
	

        // if (!allowed || !canSpawn || spawnTask != null) {
        //     event.setCancelled(true);
	//     return;
        // }
	// canSpawn = false;
	// spawnTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
	// 	canSpawn = true;
	// 	spawnTask = null;
	//     }, 20*5);
    }

    private Location spawnEntityAroundPlayer(Player player, double radius) {
	World world = player.getLocation().getWorld();
	Random rand = new Random();
	for (int i = 0; i < 100; i++) {
       
	    double angle = rand.nextDouble() * 2 * Math.PI; // random angle in radians

	    double xOffset = radius * Math.cos(angle);
	    double zOffset = radius * Math.sin(angle);

	    Location playerLoc = player.getLocation();
	    Location spawnLoc = playerLoc.clone().add(xOffset, 0, zOffset);
	    spawnLoc.setY(player.getWorld().getHighestBlockYAt(spawnLoc)+2); // optional: place on top of ground
	    
	    Block blockAtFeet = world.getBlockAt(spawnLoc);
	    Block blockAbove = blockAtFeet.getRelative(BlockFace.UP);
	    if (blockAtFeet.isPassable() && blockAbove.isPassable()) {
		return spawnLoc; // safe spot found
	    }
	}

	return null;
    }

    private boolean isLocationInRegion(Location loc, String regionName) {
        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        var world = BukkitAdapter.adapt(loc.getWorld());
        RegionManager regionManager = container.get(world);
        if (regionManager == null) return false;

        ProtectedRegion region = regionManager.getRegion(regionName);
        if (region == null) return false;

        return region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
}
