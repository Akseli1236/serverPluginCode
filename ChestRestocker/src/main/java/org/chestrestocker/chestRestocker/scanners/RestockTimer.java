package org.chestrestocker.chestRestocker.scanners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.chestrestocker.chestRestocker.LoadItemDistribution;


public class RestockTimer {

    private Chest chest;
    private long isRestocking = 0;
    private final LoadItemDistribution loadItemDistribution;

    private Vector identifier = new Vector(0,0,0);
    private Map<Vector, ItemStack[]> chestIdentifier = new HashMap<>();

    public RestockTimer(LoadItemDistribution loadItemDistribution) {
        this.loadItemDistribution = loadItemDistribution;
    }

    public void setChest(Chest chest, Vector identifier) {
        this.chest = chest;
        this.identifier = identifier;
    }

    public void startTimer() {
        long currentTime = System.currentTimeMillis();
        if (currentTime < isRestocking) {
            chest.getInventory().clear();
            chest.getInventory().setContents(chestIdentifier.get(identifier));
            return; // prevent if already restocking
        }

        restockChest(chest.getInventory());
    }

    private void restockChest(Inventory chestInventory) {
        // Custom loot table example
        isRestocking = System.currentTimeMillis()+30*1000;
        Random rand = new Random();
        Map<String, List<Double>> restockChest = loadItemDistribution.getItemChance();

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

        chestIdentifier.put(identifier, chestInventory.getContents());
    }
}
