package org.server.mapcrates;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Airdrop {

    private Plugin plugin;
    private List<Map<?, ?>> regions;
    private BukkitTask dropTask = null;
    private Random generator = new Random(System.currentTimeMillis());
    private List<BukkitTask> fallingChests = new ArrayList<>();
    private int fallSpeed;

    public Airdrop(Plugin plugin) {
        this.plugin = plugin;
        this.regions = plugin.getConfig().getMapList("regions");
    }

    public void startDrops(String delay, String fallSpeed) {
        this.fallSpeed = Integer.parseInt(fallSpeed);
        if (dropTask != null) {
            dropTask.cancel();
            dropTask = null;
        }

        dropTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            createDrops();
        }, 0, 20 * Integer.parseInt(delay));
    }

    public void stopDrops() {
        if (dropTask != null) {
            dropTask.cancel();
            dropTask = null;
        }
    }

    public void clear() {
        if (!fallingChests.isEmpty()) {
            fallingChests.forEach(task -> {
                task.cancel();
            });
            fallingChests.clear();
        }
    }

    public void drawLine(Location from, Location to, Map<String, List<BlockDisplay>> ropes) {
        Vector direction = to.toVector().subtract(from.toVector()).normalize();

        double distance = from.distance(to);
        double step = 0.5;
        int count = (int) (distance / step);
        String key = direction.getBlockX() + ":" + direction.getBlockY() + ":" + direction.getBlockZ();
        for (int i = 0; i < count; i++) {

            Location point = from.clone().add(direction.clone().multiply(i * step));
            if (!ropes.containsKey(key)) {
                BlockDisplay display = (BlockDisplay) point.getWorld().spawnEntity(point, EntityType.BLOCK_DISPLAY);
                display.setBlock(Material.END_ROD.createBlockData());

                Display.Billboard billboard = Display.Billboard.FIXED;
                display.setBillboard(billboard);
                ropes.put(key, new ArrayList<>());
                ropes.get(key).addLast(display);
                continue;

            }
            if (ropes.get(key).size() < count) {
                BlockDisplay display = (BlockDisplay) point.getWorld().spawnEntity(point, EntityType.BLOCK_DISPLAY);
                display.setBlock(Material.END_ROD.createBlockData());

                Display.Billboard billboard = Display.Billboard.FIXED;
                display.setBillboard(billboard);
                ropes.get(key).addLast(display);
                continue;
            }
            ropes.get(key).get(i).teleport(point);
        }
    }

    private void createParachute(Location loc, Map<String, BlockDisplay> parachuteBlocks,
            Map<String, List<BlockDisplay>> ropes) {
        Location location = loc.clone().add(0, 5, 0);

        for (int i = 0; i < 2; ++i) {
            for (int x = -2; x <= 2; ++x) {
                for (int z = -2; z <= 2; ++z) {
                    if (i == 1 && Math.abs(x) <= 1 && Math.abs(z) <= 1) {
                        continue;
                    }
                    if (i == 0 && (Math.abs(x) > 1 || Math.abs(z) > 1)) {
                        continue;
                    }
                    Location newLocation = location.clone().add(x, Math.negateExact(i), z);

                    if (Math.abs(x) == 2 && Math.abs(z) == 2) {
                        drawLine(loc, newLocation, ropes);
                    }
                    String key = i + ":" + x + ":" + z;
                    if (!parachuteBlocks.containsKey(key)) {
                        BlockDisplay display = (BlockDisplay) newLocation.getWorld().spawn(newLocation,
                                BlockDisplay.class);
                        display.setBlock(Bukkit.createBlockData(Material.WHITE_WOOL));

                        parachuteBlocks.put(key, display);
                        continue;
                    }

                    Block newBlock = newLocation.getBlock();

                    if (newBlock.getType() == Material.AIR) {
                        parachuteBlocks.get(key).teleport(newLocation);
                    }
                }
            }
        }

    }

    private void createDrops() {
        regions.forEach(region -> {
            World world = Bukkit.getWorld((String) region.get("world"));
            RegionManager regionManager = WorldGuard.getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .get(BukkitAdapter.adapt(world));

            ProtectedRegion selectedRegion = regionManager.getRegion((String) region.get("region"));

            Vector3 min = selectedRegion.getMinimumPoint().toVector3();
            Vector3 max = selectedRegion.getMaximumPoint().toVector3();

            int xCoord = generator.nextInt(Math.abs(min.blockX() - max.blockX())) + min.blockX();
            int zCoord = generator.nextInt(Math.abs(min.blockZ() - max.blockZ())) + min.blockZ();

            Location loc = new Location(world, xCoord, 175, zCoord);
            Block block = world.getBlockAt(loc);
            BlockDisplay newDisplay = (BlockDisplay) loc.getWorld().spawn(loc, BlockDisplay.class);
            newDisplay.setBlock(Bukkit.createBlockData(Material.CHEST));
            Map<String, BlockDisplay> parachuteBlocks = new HashMap<>();
            Map<String, List<BlockDisplay>> ropes = new HashMap<>();

            fallingChests.addFirst(
                    new BukkitRunnable() {
                        Location currentLoc = block.getLocation();
                        double fallingDistance = 0.10;

                        @Override
                        public void run() {

                            Block oldBlock = currentLoc.getBlock();
                            currentLoc.setY(currentLoc.getY() - fallingDistance);

                            Block breakPoint = currentLoc.clone().add(0, -15, 0).getBlock();
                            if (!breakPoint.getType().isAir()) {

                                parachuteBlocks.forEach((key, value) -> value.remove());
                                ropes.forEach((key, value) -> value.forEach(display -> display.remove()));
                                fallingDistance = 0.50;

                                currentLoc.getWorld().createExplosion(
                                        null, // source entity (can be a Player, Arrow, etc.)
                                        currentLoc, // location of explosion
                                        10.0F, // explosion power (4.0F is TNT, 10.0F is huge)
                                        false, // do not set fire
                                        false // do not break blocks
                                );
                            }

                            Block newBlock = currentLoc.getBlock();
                            if (!newBlock.getType().isAir()) {
                                oldBlock.setType(Material.CHEST);
                                newDisplay.remove();
                                this.cancel();
                                currentLoc.getWorld().createExplosion(
                                        null, // source entity (can be a Player, Arrow, etc.)
                                        currentLoc, // location of explosion
                                        10.0F, // explosion power (4.0F is TNT, 10.0F is huge)
                                        false, // do not set fire
                                        false // do not break blocks
                                );

                                return;
                            }

                            newDisplay.teleport(currentLoc);
                            createParachute(currentLoc, parachuteBlocks, ropes);
                        }
                    }.runTaskTimer(plugin, 0L, fallSpeed));
        });
    }
}
