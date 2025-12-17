package me.stormyzz.wanted.Configurators;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Map;

public class MobSpawnConfig {

    private final String worldName;
    private final String regionName;
    private final boolean requireSky;
    private final List<Material> disallowedBlocks;
    private final int minY;
    private final int maxY;
    private final int spawnSpeed;
    private final double spawnRadius;
    private final Map<EntityType, MobSettings> allowedMobsWithSettings;

    public MobSpawnConfig(String worldName, String regionName, boolean requireSky,
            List<Material> disallowedBlocks, int minY, int maxY,
            Map<EntityType, MobSettings> allowedMobsWithSettings, int spawnSpeed, double spawnRadius) {
        this.worldName = worldName;
        this.regionName = regionName;
        this.requireSky = requireSky;
        this.disallowedBlocks = disallowedBlocks;
        this.minY = minY;
        this.maxY = maxY;
        this.spawnRadius = spawnRadius;
        this.spawnSpeed = spawnSpeed;
        this.allowedMobsWithSettings = allowedMobsWithSettings;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getRegionName() {
        return regionName;
    }

    public boolean isRequireSky() {
        return requireSky;
    }

    public List<Material> getDisallowedBlocks() {
        return disallowedBlocks;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getSpawnSpeed() {
        return spawnSpeed;
    }

    public double getSpawnRadius() {
        return spawnRadius;
    }

    public Map<EntityType, MobSettings> getAllowedMobsWithSettings() {
        return allowedMobsWithSettings;
    }

    public static class MobSettings {
        private final String customName;
        private final boolean allowArmor;
        private final boolean allowItems;

        public MobSettings(String customName, boolean allowArmor, boolean allowItems) {
            this.customName = customName;
            this.allowArmor = allowArmor;
            this.allowItems = allowItems;
        }

        public String getCustomName() {
            return customName;
        }

        public boolean isAllowArmor() {
            return allowArmor;
        }

        public boolean isAllowItems() {
            return allowItems;
        }
    }
}
