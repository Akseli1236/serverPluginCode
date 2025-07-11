package org.server.mapcrates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Airdrop{

    private Plugin plugin;
    private List<Map<?, ?>> regions;
    private BukkitTask dropTask = null;
    private Random generator = new Random(System.currentTimeMillis());
    private List<BukkitTask> fallingChests = new ArrayList<>();

    public Airdrop(Plugin plugin){
        this.plugin = plugin;
        this.regions = plugin.getConfig().getMapList("regions");
    }

    public void startDrops(String delay, String fallSpeed){
        dropTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                createDrops(fallSpeed);
            }, 0, 20 * Integer.parseInt(delay));
    }


    public void stopDrops(){
        if (dropTask != null){
           dropTask.cancel();
           dropTask = null;
        }

        if (!fallingChests.isEmpty()){
            fallingChests.clear();
        }

    }

    private void createDrops(String fallSpeed){
        regions.forEach(region -> {
                World world = Bukkit.getWorld((String) region.get("world"));
                RegionManager regionManager = WorldGuard.getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .get(BukkitAdapter.adapt(world));

                ProtectedRegion selectedRegion = regionManager.getRegion((String) region.get("region"));

                Vector3 min = selectedRegion.getMinimumPoint().toVector3();
                Vector3 max = selectedRegion.getMaximumPoint().toVector3();

                int xCoord = generator.nextInt(Math.abs(min.blockX()-max.blockX())) + min.blockX();
                int zCoord = generator.nextInt(Math.abs(min.blockZ()-max.blockZ())) + min.blockZ();

                Location loc = new Location(world, xCoord, 175, zCoord);
                Block block = world.getBlockAt(loc);
                block.setType(Material.CHEST);

                fallingChests.addFirst(
                    new BukkitRunnable() {
                        Location currentLoc = block.getLocation();

                        @Override
                        public void run() {

                            Block oldBlock = currentLoc.getBlock();

                            currentLoc.setY(currentLoc.getY() - 1);
                            Block newBlock = currentLoc.getBlock();

                            if (!newBlock.getType().isAir()){
                                this.cancel();
                                currentLoc.getWorld().createExplosion(
                                        null,             // source entity (can be a Player, Arrow, etc.)
                                        currentLoc,       // location of explosion
                                        10.0F,            // explosion power (4.0F is TNT, 10.0F is huge)
                                        false,            // do not set fire
                                        false             // do not break blocks
                                );

                                return;
                            }

                            oldBlock.setType(Material.AIR);
                            newBlock.setType(Material.CHEST);
                        }
                    }.runTaskTimer(plugin, 0L, Integer.parseInt(fallSpeed)));
            });
    }
}
