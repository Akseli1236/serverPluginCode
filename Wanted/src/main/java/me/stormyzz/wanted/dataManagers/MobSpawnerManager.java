package me.stormyzz.wanted.dataManagers;

import me.stormyzz.wanted.Configurators.MobSpawnConfig;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class MobSpawnerManager {

    private final JavaPlugin plugin;
    private static final List<MobSpawnConfig> spawnConfigs = new ArrayList<>();

    public MobSpawnerManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        spawnConfigs.clear();

        List<Map<?, ?>> rawRegions = plugin.getConfig().getMapList("regions");

        for (Map<?, ?> rawData : rawRegions) {
            try {
                Map<String, Object> regionData = (Map<String, Object>) rawData;

                String worldName = (String) regionData.get("world");
                String regionName = (String) regionData.get("region");
                boolean requireSky = regionData.containsKey("requireSky") ? (boolean) regionData.get("requireSky") : true;

		

                List<String> disallowedBlockStrings = regionData.containsKey("disallowedBlocks")
                        ? (List<String>) regionData.get("disallowedBlocks")
                        : List.of();

                List<Material> disallowedBlocks = disallowedBlockStrings.stream()
                        .map(String::toUpperCase)
                        .map(name -> {
                            try {
                                return Material.valueOf(name);
                            } catch (IllegalArgumentException e) {
                                plugin.getLogger().warning("Invalid block type in disallowedBlocks: " + name);
                                return null;
                            }
                        })
                        .filter(java.util.Objects::nonNull)
                        .toList();

                // Parse allowedMobs as map from mob type -> settings map
                Map<String, Map<String, Object>> rawAllowedMobs =
                        (Map<String, Map<String, Object>>) regionData.get("allowedMobs");
                Map<EntityType, MobSpawnConfig.MobSettings> allowedMobsWithSettings = new java.util.HashMap<>();

                if (rawAllowedMobs != null) {
                    for (Map.Entry<String, Map<String, Object>> entry : rawAllowedMobs.entrySet()) {
                        try {
                            EntityType type = EntityType.valueOf(entry.getKey().toUpperCase());
                            Map<String, Object> settings = entry.getValue();

                            String name = (String) settings.getOrDefault("name", null);
                            boolean allowArmor = (boolean) settings.getOrDefault("allowArmor", true);
                            boolean allowItems = (boolean) settings.getOrDefault("allowItems", true);

                            allowedMobsWithSettings.put(type, new MobSpawnConfig.MobSettings(name, allowArmor, allowItems));
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("Invalid mob type in allowedMobs: " + entry.getKey());
                        }
                    }
                }

                int minY = regionData.containsKey("minY") ? (int) regionData.get("minY") : 0;
                int maxY = regionData.containsKey("maxY") ? (int) regionData.get("maxY") : 256;
		int spawnRadius = regionData.containsKey("spawnRadius") ? (int) regionData.get("spawnRadius") : 30;
		int spawnSpeed = regionData.containsKey("spawnSpeed") ? (int) regionData.get("spawnSpeed") : 30;

                MobSpawnConfig config = new MobSpawnConfig(
                        worldName,
                        regionName,
                        requireSky,
                        disallowedBlocks,
                        minY,
                        maxY,
                        allowedMobsWithSettings,
			spawnSpeed,
			spawnRadius
                );

                spawnConfigs.add(config);
            } catch (ClassCastException | NullPointerException e) {
                plugin.getLogger().warning("Invalid region configuration format: " + e.getMessage());
            }
        }
    }
    public static List<MobSpawnConfig> getSpawnConfigs() {
        return spawnConfigs;
    }
}
