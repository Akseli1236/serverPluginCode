package org.chestrestocker.chestRestocker.scanners;
<<<<<<< HEAD
=======
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
<<<<<<< HEAD
import org.chestrestocker.chestRestocker.LoadItemDistribution;

import java.util.*;

=======
import org.bukkit.util.Vector;
import org.chestrestocker.chestRestocker.LoadItemDistribution;

>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79
public class RestockTimer {

    private Chest chest;
    private long isRestocking = 0;
    private final LoadItemDistribution loadItemDistribution;
<<<<<<< HEAD
=======
    private Vector identifier = new Vector(0,0,0);
    private Map<Vector, ItemStack[]> chestIdentifier = new HashMap<>();
>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79

    public RestockTimer(LoadItemDistribution loadItemDistribution) {
        this.loadItemDistribution = loadItemDistribution;
    }
<<<<<<< HEAD
    public void setChest(Chest chest) {
        this.chest = chest;
=======
    public void setChest(Chest chest, Vector identifier) {
        this.chest = chest;
        this.identifier = identifier;
>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79
    }

    public void startTimer() {
        long currentTime = System.currentTimeMillis();
        if (currentTime < isRestocking) {
<<<<<<< HEAD
=======
            chest.getInventory().clear();
            chest.getInventory().setContents(chestIdentifier.get(identifier));
>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79
            return; // prevent if already restocking
        }

        restockChest(chest.getInventory());
    }

    private void restockChest(Inventory chestInventory) {
        // Custom loot table example
        isRestocking = System.currentTimeMillis()+30*1000;
        Random rand = new Random();
        Map<String, List<Double>> restockChest = loadItemDistribution.getItemChance();

<<<<<<< HEAD
=======

>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79
        ItemStack[] loot = restockChest.entrySet().stream()
                .filter(entry -> {
                    double odds = entry.getValue().get(2);
                    return rand.nextDouble() <= odds;
                })
                .map(entry -> new ItemStack(Material.valueOf(entry.getKey().toUpperCase()),
                        rand.nextInt(entry.getValue().getFirst().intValue(), entry.getValue().get(1).intValue()+1)))
                .toArray(ItemStack[]::new);

        // Restock the chest with the loot
        chestInventory.clear(); // clear existing items

        List<Integer> usedValues = new ArrayList<>();
        for (ItemStack item : loot) {
            while (true){
                int rand_int = rand.nextInt(chestInventory.getSize());
                if (!usedValues.contains(rand_int)) {
                    usedValues.add(rand_int);
                    break;
                }
            }
            chestInventory.setItem(usedValues.getLast(), item);
        }
<<<<<<< HEAD
    }
}
=======
        chestIdentifier.put(identifier, chestInventory.getContents());
    }
}
>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79
