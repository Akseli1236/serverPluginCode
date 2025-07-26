package org.chestrestocker.chestRestocker.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.util.Vector;
import org.chestrestocker.chestRestocker.LoadItemDistribution;
import org.chestrestocker.chestRestocker.scanners.RestockTimer;

public class ChestOpenListener implements Listener {

    private final List<String> ALLOWED_REGIONS;
    // HashMap to store chests and their associated RestockTimers
    private final Map<Vector, RestockTimer> chestRestockTimers = new HashMap<>();
    private Vector chestIdentifier = new Vector();
    private final LoadItemDistribution loadItemDistribution;


    public ChestOpenListener(LoadItemDistribution loadItemDistribution) {
        this.loadItemDistribution = loadItemDistribution;
        ALLOWED_REGIONS = loadItemDistribution.getAllowedRegions();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if (event.getInventory().getType() != InventoryType.CHEST || !chestRestockTimers.containsKey(chestIdentifier)){
            return;
        }
        chestRestockTimers.get(chestIdentifier).updateChestItems(chestIdentifier, event.getInventory().getContents());

    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        // Ensure the player clicked a block and it is a chest
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.CHEST) {
            return;
        }

        // Only handle right-click action on chests
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Chest chest = (Chest) event.getClickedBlock().getState();
        List<Vector> identifier = new ArrayList<>();

        InventoryHolder holder = chest.getInventory().getHolder();

        if (holder instanceof DoubleChest doubleChest) {
            Chest left = (Chest) doubleChest.getLeftSide();
            Chest right = (Chest) doubleChest.getRightSide();
            identifier.addLast(left.getLocation().toVector());
            identifier.addLast(right.getLocation().toVector());
        }else {
            identifier.addLast(chest.getLocation().toVector());
        }

        // Check if the chest is inside a valid region
        if (isInValidRegion(chest.getLocation())) {
            // If it's a new chest, create a RestockTimer and add it to the HashMap
            for (Vector identify : identifier){
                if (identify.equals(event.getClickedBlock().getLocation().toVector())){
                    chestIdentifier = identify;
                    if (!chestRestockTimers.containsKey(identify)){
                        RestockTimer restockTimer = new RestockTimer(loadItemDistribution);
                        restockTimer.setChest(chest, identify);
                        chestRestockTimers.put(identify, restockTimer);
                    }
                    RestockTimer restockTimer = chestRestockTimers.get(identify);
                    restockTimer.startTimer();
                }
            }
        }
    }
    private boolean isInValidRegion(org.bukkit.Location location) {
        // Get the region manager for the world
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        // Convert the Bukkit World to a WorldEdit BukkitWorld
        BukkitWorld worldEditWorld = (BukkitWorld) BukkitAdapter.adapt(location.getWorld());
        RegionManager regionManager = container.get(worldEditWorld);

        if (regionManager == null) {
            return false;
        }

        // Iterate through the allowed regions
        for (String regionName : ALLOWED_REGIONS) {
            ProtectedRegion region = regionManager.getRegion(regionName);

            if (region != null) {
                // WorldGuard's 'contains' method requires a BlockVector3 instead of separate X, Y, Z
                com.sk89q.worldedit.math.BlockVector3 vector = com.sk89q.worldedit.math.BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

                // Check if the location is within the region
                if (region.contains(vector)) {
                    return true;
                }
            }
        }

        return false;
    }
}
