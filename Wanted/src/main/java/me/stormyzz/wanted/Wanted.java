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

    private MobSpawnerManager mobSpawnerManager;
    private CustomScoreboard customScoreboard;

    @Override
    public void onEnable() {
        customScoreboard = new CustomScoreboard(this);
        mobSpawnerManager = new MobSpawnerManager(this);
        mobSpawnerManager.loadConfig();
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new MobSpawnListener(mobSpawnerManager, this), this);
        getServer().getPluginManager().registerEvents(customScoreboard, this);
        getServer().getPluginManager().registerEvents(new MobBurnPreventionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerKillListener(this), this);
        getServer().getPluginManager().registerEvents(new CustomScoreboard(this), this);
        Bukkit.getPluginManager().registerEvents(new DeathBalanceListener(this), this);
        getCommand("wantedreload").setExecutor(new WantedCommand(this));


        Bukkit.getScheduler().runTaskTimerAsynchronously(
                this,
                () -> playerStatsManager.saveStats(),
                0L,
                20L * 60 * 15
        );

        //Statistics saver
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                playerStatsManager.saveStats();
                Bukkit.getLogger().info("Player stats saved successfully during shutdown.");
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("Failed to save player stats during shutdown.");
            }
        }));

        //Vault getter
        Bukkit.getScheduler().runTask(this, () -> {
            if (!VaultEconomyManager.setupEconomy()) {
                getLogger().warning("Vault not found! Economy features may not work.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            getLogger().info("Economy setup successfully.");

            playerStatsManager.loadStats();
        });

        //Worldguard getter
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
    public CustomScoreboard getCustomScoreboard() { return customScoreboard; }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        playerStatsManager.saveStats();
        Bukkit.getLogger().info("Player stats saved successfully during shutdown.");
    }

}
