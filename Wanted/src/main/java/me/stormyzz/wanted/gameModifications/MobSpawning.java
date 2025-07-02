package me.stormyzz.wanted.gameModifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import me.stormyzz.wanted.Wanted;
import me.stormyzz.wanted.Configurators.MobSpawnConfig;
import me.stormyzz.wanted.dataManagers.MobSpawnerManager;

public class MobSpawning{
    private Wanted plugin;
    private Player player;
    private MobSpawnerManager spawnerManager;
    private BukkitTask mobSpawning = null;
    private int spawnSpeed = 0;
    
    public MobSpawning(Wanted plugin, Player player, MobSpawnerManager mobSpawnerManager){
	this.plugin = plugin;
	this.player = player;
	this.spawnerManager = mobSpawnerManager;
	
	
    }

    public void startSpawning(){
	mobSpawning = Bukkit.getScheduler().runTaskTimer(plugin,()-> {
		spawnMobsAroundPlayer();
	    }, 0, 20 * spawnSpeed);
    }

    private void spawnMobsAroundPlayer(){
	int minY = 0;
	int maxY = 0;
	double spawnRadius;
	int currentSpawnSpeed = spawnSpeed;
	
	LivingEntity entity = null;
	Location spawnLocation = null;
	
	for (MobSpawnConfig config : spawnerManager.getSpawnConfigs()) {
		if (!player.getWorld().getName().equalsIgnoreCase(config.getWorldName())) continue;
		if (!isLocationInRegion(player.getLocation(), config.getRegionName())) continue;

            // Check Y constraints
	    minY = config.getMinY();
	    maxY = config.getMaxY();
	    
	    spawnRadius = config.getSpawnRadius();
	    spawnSpeed = config.getSpawnSpeed();

	    spawnLocation = spawnEntityAroundPlayer(player, spawnRadius);

            // Check allowed mobs
            Map<EntityType, MobSpawnConfig.MobSettings> allowedMobs = config.getAllowedMobsWithSettings();
	    List<Map.Entry<EntityType, MobSpawnConfig.MobSettings>> entries = new ArrayList<>(allowedMobs.entrySet());
	    Random rand = new Random();

	    Map.Entry<EntityType, MobSpawnConfig.MobSettings> randomEntry = entries.get(rand.nextInt(entries.size()));
	    EntityType selectedType = randomEntry.getKey();
	    MobSpawnConfig.MobSettings mobSettings = randomEntry.getValue();

	    
	    entity = (LivingEntity) player.getWorld().spawnEntity(spawnLocation, selectedType);

            // Set custom name
            if (mobSettings.getCustomName() != null) {
                entity.setCustomName(ChatColor.translateAlternateColorCodes('&', mobSettings.getCustomName()));
                entity.setCustomNameVisible(true);
            }

			if (!mobSettings.isAllowArmor()){
				entity.getEquipment().setHelmet(null);
				entity.getEquipment().setChestplate(null);
				entity.getEquipment().setLeggings(null);
				entity.getEquipment().setBoots(null);
			}

			if (!mobSettings.isAllowItems()){
				entity.getEquipment().setItemInMainHand(null);
				entity.getEquipment().setItemInOffHand(null);
			}

            // Stop after first match
            break;
        }
	
	
	if (spawnLocation != null && entity != null) {
	    entity.teleport(spawnLocation);
	}
	if (currentSpawnSpeed != spawnSpeed){
	    stopSpawning();
	    startSpawning();
	}
	
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

    public void stopSpawning(){
	if (mobSpawning != null){
	    mobSpawning.cancel();
	    mobSpawning  = null;
	}
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
    

}
