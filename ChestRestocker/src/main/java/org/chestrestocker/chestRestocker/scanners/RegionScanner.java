package org.chestrestocker.chestRestocker.scanners;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class RegionScanner {

    private WorldGuardPlugin worldGuard;

    public RegionScanner() {
        // Get the WorldGuard plugin instance
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin instanceof WorldGuardPlugin) {
            worldGuard = (WorldGuardPlugin) plugin;
        }
    }

    public void fetchAllWorldRegions() {
        // Get all worlds
        List<World> worlds = Bukkit.getServer().getWorlds();

        for (World world : worlds) {
            // Fetch the RegionManager for this world
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regionManager = container.get((com.sk89q.worldedit.world.World) world);

            if (regionManager != null) {
                // Get all regions in this world
                regionManager.getRegions().forEach((name, region) -> {
                    // Here you can work with each region's name and other properties
                    System.out.println("Region Name: " + name + " in World: " + world.getName());
                    // Example: you could store these in a map or process them further
                });
            }
        }
    }
}
