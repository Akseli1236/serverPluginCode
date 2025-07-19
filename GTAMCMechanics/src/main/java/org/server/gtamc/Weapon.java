package org.server.gtamc;

import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Weapon {

    private Plugin plugin;

    public void weaponUpdate(Plugin plugin) {
        this.plugin = plugin;
        readFile();
    }

    private final List<String> FOLDER_NAMES = Arrays.asList(
            "Weapons", // The folder name for weapons
            "Ammos", // The folder name for ammos
            "Projectiles",
            "Tools"// The folder name for projectiles
    );
    private final Map<String, WeaponClass> weapons = new HashMap<>();
    private final Map<String, AmmoClass> ammos = new HashMap<>();
    private final Map<String, ProjectileClass> projectiles = new HashMap<>();
    private final Map<String, WeaponClass> tools = new HashMap<>();

    private Map<String, Object> toLowercaseKeysRecursive(Map<String, Object> map, String key, String folderName) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String lowerKey = entry.getKey().toLowerCase();
            Object value = entry.getValue();
            if (value instanceof Map) {
                // Recursively process nested maps
                if (key.equalsIgnoreCase(lowerKey)
                        && (folderName.equalsIgnoreCase("Weapons") || folderName.equalsIgnoreCase("Tools"))) {
                    result.put("root", toLowercaseKeysRecursive((Map<String, Object>) value, key, folderName));
                } else {
                    result.put(lowerKey, toLowercaseKeysRecursive((Map<String, Object>) value, key, folderName));
                }

            } else if (value instanceof List) {
                // Process lists
                result.put(lowerKey, processListKeys((List<Object>) value, key, folderName));
            } else {
                // Leave values unchanged
                result.put(lowerKey, value);
            }
        }
        return result;
    }

    // Recursively process lists without modifying the values
    private List<Object> processListKeys(List<Object> list, String key, String folderName) {
        List<Object> result = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map) {
                result.add(toLowercaseKeysRecursive((Map<String, Object>) item, key, folderName));
            } else {
                result.add(item); // Leave non-map values unchanged
            }
        }
        return result;
    }

    public Map<String, WeaponClass> getWeapons() {
        return weapons;
    }

    public Map<String, AmmoClass> getAmmos() {
        return ammos;
    }

    public Map<String, ProjectileClass> getProjectiles() {
        return projectiles;
    }

    public Map<String, WeaponClass> getTools() {
        return tools;
    }

    public void clearAll() {
        weapons.clear();
        ammos.clear();
        projectiles.clear();
        tools.clear();
    }

    public void readFile() {
        File dataFolder = plugin.getDataFolder();

        for (String folderName : FOLDER_NAMES) {
            Path filePath = Path.of(dataFolder.toString(), folderName);
            File dir = new File(filePath.toString());

            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    System.out.println("Directory created: " + dir.getAbsolutePath());
                } else {
                    System.out.println("Failed to create directory.");
                }
            }

            File[] files = dir.listFiles((d, name) -> name.endsWith(".yml")); // Filter for .yml files
            if (files != null) {
                for (File file : files) {
                    try (FileInputStream fileInputStream = new FileInputStream(file)) {
                        // Parse the YAML file into a Weapon object
                        Yaml yaml = new Yaml();
                        Map<String, Object> yamlData = yaml.load(fileInputStream);
                        String key = yamlData.entrySet().iterator().next().getKey();
                        Map<String, Object> lowerCaseKeysYaml = toLowercaseKeysRecursive(yamlData, key, folderName);
                        String lowerCaseYamlString = yaml.dump(lowerCaseKeysYaml);

                        Constructor constructor = new Constructor(new LoaderOptions());
                        switch (folderName) {
                            case "Weapons", "Tools":
                                constructor = new Constructor(WeaponClass.class, new LoaderOptions());
                                break;
                            case "Ammos":
                                constructor = new Constructor(AmmoClass.class, new LoaderOptions());
                                break;
                            case "Projectiles":
                                constructor = new Constructor(ProjectileClass.class, new LoaderOptions());
                                break;
                            default:
                                break;
                            // code block
                        }

                        Yaml yaml2 = new Yaml(constructor);
                        Object loadedData = yaml2.load(lowerCaseYamlString);

                        // Check the loaded data type dynamically, and cast it
                        if (loadedData instanceof WeaponClass weaponData) {
                            if (folderName.equalsIgnoreCase("Weapons")) {
                                weapons.put(key, weaponData);
                            } else if (folderName.equalsIgnoreCase("Tools")) {
                                tools.put(key, weaponData);
                            }
                            // Process the WeaponClass data
                        } else if (loadedData instanceof AmmoClass ammoData) {
                            ammos.put(key, ammoData);
                            // Process the AmmoClass data
                        } else if (loadedData instanceof ProjectileClass projectileData) {
                            projectiles.put(key, projectileData);
                            // Process the ProjectileClass data
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
