package org.example;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;

import org.bukkit.Registry;
import org.bukkit.Sound;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;


public class Main {

    private static final String FOLDER_NAME = "FirstPlugin"; // The specific folder name
    private static final List<String> FOLDER_NAMES = Arrays.asList(
            "Weapons",   // The folder name for weapons
            "Ammos",     // The folder name for ammos
            "Projectiles",
            "Tools"// The folder name for projectiles
    );
    private static String getJarDirectoryPath() {
        File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        return jarFile.getParent(); // Get the directory of the JAR file
    }

    static Map<String, WeaponClass> info = new HashMap<>();
    static Map<String, AmmoClass> ammos = new HashMap<>();
    static Map<String, ProjectileClass> projectiles = new HashMap<>();
    static Map<String, WeaponClass> tools = new HashMap<>();

public static Map<String, Object> toLowercaseKeysRecursive(Map<String, Object> map, String key, String folderName) {
    Map<String, Object> result = new LinkedHashMap<>();
    for (Map.Entry<String, Object> entry : map.entrySet()) {
        String lowerKey = entry.getKey().toLowerCase();
        Object value = entry.getValue();
        if (value instanceof Map) {
            // Recursively process nested maps
            if (key.equalsIgnoreCase(lowerKey) && (folderName.equalsIgnoreCase("Weapons") || folderName.equalsIgnoreCase("Tools"))) {
                result.put("root", toLowercaseKeysRecursive((Map<String, Object>) value, key,folderName));
            }else {
                result.put(lowerKey, toLowercaseKeysRecursive((Map<String, Object>) value, key,folderName));
            }


        } else if (value instanceof List) {
            // Process lists
            result.put(lowerKey, processListKeys((List<Object>) value, key,folderName));
        } else {
            // Leave values unchanged
            result.put(lowerKey, value);
        }
    }
    return result;
}

    // Recursively process lists without modifying the values
    public static List<Object> processListKeys(List<Object> list, String key, String folderName) {
        List<Object> result = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map) {
                result.add(toLowercaseKeysRecursive((Map<String, Object>) item, key, folderName));
            } else {
                result.add(item); // Leave non-map values unchanged
            }
        }
        return result;
     // Removes '(' and ')'
    }



    public static void main (String[] args) {
        String FILE_PATH = getJarDirectoryPath();
        // Replace with your output directory path
        for (String folderName : FOLDER_NAMES){
            Path filePath = Path.of(FILE_PATH, FOLDER_NAME, folderName);
            File dir = new File(filePath.toString());
            File[] files = dir.listFiles((d, name) -> name.endsWith(".yml")); // Filter for .yml files
            if (files != null) {
                for (File file : files) {
                    try(FileInputStream fileInputStream = new FileInputStream(file)) {
                        String outputFilePath = filePath + File.separator + "parse" +file.getName();

                        // Assuming the YAML files are in a directory "weapons"

                        // Create a SnakeYAML instance

                        // Parse the YAML file into a Weapon object
                        Yaml yaml = new Yaml();
                        Map<String, Object> yamlData = yaml.load(fileInputStream);
                        String key = yamlData.entrySet().iterator().next().getKey();
                        Map<String, Object> lowerCaseKeysYaml = toLowercaseKeysRecursive(yamlData, key, folderName);

                        // Write the transformed YAML back to a file
                        DumperOptions options = new DumperOptions();
                        options.setPrettyFlow(true); // Human-readable format
                        Yaml yamlWriter = new Yaml(options);
                        FileWriter writer = new FileWriter(outputFilePath);
                        yamlWriter.dump(lowerCaseKeysYaml, writer);
                        writer.close();


                        FileInputStream fileInputStream2 = new FileInputStream(outputFilePath);
                        //System.out.println("Transformed YAML saved to " + outputFilePath);
                        Constructor constructor = new Constructor(new LoaderOptions());
                        switch(folderName) {
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
                        Object loadedData = yaml2.load(fileInputStream2);

                        // Check the loaded data type dynamically, and cast it
                        if (loadedData instanceof WeaponClass weaponData) {
                            if (folderName.equalsIgnoreCase("Weapons")) {
                                info.put(key, weaponData);
                            }
                            else if (folderName.equalsIgnoreCase("Tools")) {
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

                        fileInputStream2.close();
                        fileInputStream.close();

                        File createdFile = new File(outputFilePath);
                        createdFile.delete();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Random random = new Random();
        System.out.println(random.nextInt(1,2));


    }

}