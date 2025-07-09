package org.server.mapcrates;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class Airdrop{

    private Plugin plugin;
    private List<Map<?, ?>> regions;
    private BukkitTask dropTask = null;

    public Airdrop(Plugin plugin){
        this.plugin = plugin;
        this.regions = plugin.getConfig().getMapList("regions");
    }

    public void startDrops(){
        dropTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                createDrops();
            }, 0, 20*5);
    }

    public void stopDrops(){
        if (dropTask != null){
           dropTask.cancel();
           dropTask = null;
        }

    }

    private void createDrops(){

    }


}
