package me.stormyzz.wanted.statistics;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class playerStatsManager {

    private static final File statsFile = new File(Bukkit.getPluginManager().getPlugin("Wanted").getDataFolder(), "player_stats.json");
    private static Map<String, playerStats> playerStats = new HashMap<>();
    private static final Gson gson = new Gson();

    // Load player stats from the JSON file
    public static void loadStats() {
        if (statsFile.exists()) {
            try (Reader reader = new FileReader(statsFile)) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    String uuid = entry.getKey();
                    JsonObject statsObj = entry.getValue().getAsJsonObject();
                    int kills = statsObj.get("kills").getAsInt();
                    int deaths = statsObj.get("deaths").getAsInt();
                    int wantedLevel = statsObj.get("wantedLevel").getAsInt();
                    int killstreak = statsObj.get("killstreak").getAsInt();

                    playerStats.put(uuid, new playerStats(kills, deaths, wantedLevel, killstreak));
                }
                Bukkit.getLogger().info("Stats successfully loaded from file!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveStats();  // Ensure file is created on first run
            Bukkit.getLogger().warning("Stats file not found! Created new file.");
        }
    }

    // Save player stats to the JSON file
    public static void saveStats() {
        if (playerStats.isEmpty()) {
            Bukkit.getLogger().info("Player stats map is empty. Skipping save to prevent overwriting file.");
            return;
        }

        // Ensure economy is available before saving stats
        if (VaultEconomyManager.getEconomy() == null) {
            Bukkit.getLogger().warning("Vault economy is unavailable. Skipping balance saving.");
            return;  // Skip saving balance if Vault is unavailable
        }

        JsonObject json = new JsonObject();
        for (Map.Entry<String, playerStats> entry : playerStats.entrySet()) {
            String uuid = entry.getKey();
            playerStats stats = entry.getValue();

            // Update balance before saving stats
            JsonObject statsObj = new JsonObject();
            statsObj.addProperty("kills", stats.getKills());
            statsObj.addProperty("deaths", stats.getDeaths());
            statsObj.addProperty("wantedLevel", stats.getWantedLevel());
            statsObj.addProperty("killstreak", stats.getKillstreak());
            statsObj.addProperty("kdr", stats.getKDR());

            json.add(uuid, statsObj);  // Add player stats to the JSON object
        }

        try (Writer writer = new FileWriter(statsFile)) {
            gson.toJson(json, writer);
            Bukkit.getLogger().info("Saved player stats to " + statsFile);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("Failed to save player stats to " + statsFile);
        }
    }

    // Get stats for a player
    public static playerStats getStats(String uuid) {
        return playerStats.getOrDefault(uuid, new playerStats(0, 0, 0, 0));
    }

    // Set stats for a player
    public static void setStats(String uuid, playerStats stats) {
        playerStats.put(uuid, stats);
    }
}