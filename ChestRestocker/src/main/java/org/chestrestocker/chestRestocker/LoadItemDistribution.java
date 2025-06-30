package org.chestrestocker.chestRestocker;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadItemDistribution implements Listener {

    private final Map<String, List<Double>> ItemChance = new HashMap<>();
    private final Plugin plugin;
    private List<String> ALLOWED_REGIONS;



    public LoadItemDistribution(Plugin plugin) {
        this.plugin = plugin;

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            if (dataFolder.mkdirs()) {
                System.out.println("Directory created: " + dataFolder.getAbsolutePath());
            } else {
                System.out.println("Failed to create directory.");
            }
        }
        loader();
    }

    public List<String> getAllowedRegions() {
        return ALLOWED_REGIONS;
    }

    public Map<String, List<Double>> getItemChance() {
        return ItemChance;
    }

    public void loader(){
        ItemChance.clear();
        loadItemChance();
    }

    private void loadItemChance() {
        ConfigurationSection itemChanceSection = plugin.getConfig().getConfigurationSection("Item_chance");
        ALLOWED_REGIONS = plugin.getConfig().getStringList("Allowed_regions");

        itemChanceSection.getKeys(false).forEach(key -> {
            ItemChance.put(key, itemChanceSection.getDoubleList(key));
        });
        System.out.println(ItemChance);

    }

}
