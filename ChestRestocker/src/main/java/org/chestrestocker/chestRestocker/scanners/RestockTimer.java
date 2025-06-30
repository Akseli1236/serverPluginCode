package org.chestrestocker.chestRestocker.scanners;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.chestrestocker.chestRestocker.LoadItemDistribution;

import java.util.*;

public class RestockTimer {

    private Chest chest;
    private long isRestocking = 0;
    private final LoadItemDistribution loadItemDistribution;

    public RestockTimer(LoadItemDistribution loadItemDistribution) {
        this.loadItemDistribution = loadItemDistribution;
    }
    public void setChest(Chest chest) {
        this.chest = chest;
    }

    public void startTimer() {
        long currentTime = System.currentTimeMillis();
        if (currentTime < isRestocking) {
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
    }
}