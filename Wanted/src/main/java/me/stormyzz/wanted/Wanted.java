package me.stormyzz.wanted;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import me.stormyzz.wanted.Commands.WantedCommand;
import me.stormyzz.wanted.dataManagers.MobSpawnerManager;
import me.stormyzz.wanted.listeners.*;
import me.stormyzz.wanted.statistics.CustomScoreboard;
import me.stormyzz.wanted.statistics.playerStatsManager;
import me.stormyzz.wanted.statistics.VaultEconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Wanted extends JavaPlugin {

    private static Wanted instance;
    private MobSpawnerManager mobSpawnerManager;
    private CustomScoreboard customScoreboard;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
	System.out.println("Server Start");
        mobSpawnerManager = new MobSpawnerManager(this);
        customScoreboard = new CustomScoreboard(this);
        mobSpawnerManager.loadConfig();

        getServer().getPluginManager().registerEvents(new MobSpawnListener(mobSpawnerManager, this), this);
        getServer().getPluginManager().registerEvents(customScoreboard, this);
        getCommand("wantedreload").setExecutor(new WantedCommand(this));
        getServer().getPluginManager().registerEvents(new MobBurnPreventionListener(), this);


        // Schedule the stats saving task every 15 minutes
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                this, // Your plugin instance
                () -> playerStatsManager.saveStats(), // Task to save stats
                0L, // Initial delay (0 means start immediately)
                20L * 60 * 15 // Repeat interval (2 minutes in ticks)
        );

        // Register shutdown hook to save stats on server shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                playerStatsManager.saveStats();
                Bukkit.getLogger().info("Player stats saved successfully during shutdown.");
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("Failed to save player stats during shutdown.");
            }
        }));

        // Register the player kill and join listeners
        getServer().getPluginManager().registerEvents(new PlayerKillListener(this), this);
        getServer().getPluginManager().registerEvents(new CustomScoreboard(this), this);
        Bukkit.getPluginManager().registerEvents(new DeathBalanceListener(this), this);


        // Make sure Vault is set up before loading stats
        Bukkit.getScheduler().runTask(this, () -> {
            if (!VaultEconomyManager.setupEconomy()) {
                getLogger().warning("Vault not found! Economy features may not work.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            getLogger().info("Economy setup successfully.");

            // Now load player stats, since Vault is confirmed to be available
            playerStatsManager.loadStats();
        });

        WorldGuardPlugin wg = getWorldGuard();
        if (wg == null){
            getLogger().severe("WorldGuard plugin not found! mob spawning will not work");
        }

    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin instanceof  WorldGuardPlugin) return (WorldGuardPlugin) plugin;
        return null;
    }

    public MobSpawnerManager getMobSpawnerManager(){
        return mobSpawnerManager;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        playerStatsManager.saveStats();
        Bukkit.getLogger().info("Player stats saved successfully during shutdown.");
    }

    public static Wanted getInstance() {
        return instance;
    }

    public CustomScoreboard getCustomScoreboard() {
        return customScoreboard;
    }
}
