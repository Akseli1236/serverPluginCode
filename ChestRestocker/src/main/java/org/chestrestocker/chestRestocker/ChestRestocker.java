package org.chestrestocker.chestRestocker;

import org.bukkit.plugin.java.JavaPlugin;
import org.chestrestocker.chestRestocker.listeners.ChestOpenListener;

public class ChestRestocker extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("ChestRestocker plugin enabled!");

        LoadItemDistribution loadItemDistribution = new LoadItemDistribution(this);
        Commands commands = new Commands(this, loadItemDistribution);
        // Register the ChestOpenListener event
        getServer().getPluginManager().registerEvents(loadItemDistribution, this);
        getServer().getPluginManager().registerEvents(new ChestOpenListener(loadItemDistribution), this);

        this.getCommand("rcheststocker").setExecutor(commands);
    }

    @Override
    public void onDisable() {
        getLogger().info("ChestRestocker plugin disabled!");
    }
}