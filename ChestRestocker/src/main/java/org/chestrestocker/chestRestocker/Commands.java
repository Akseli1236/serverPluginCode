package org.chestrestocker.chestRestocker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Commands implements CommandExecutor {

    private final Plugin plugin;
    private final LoadItemDistribution loadItemDistribution;

    public Commands(Plugin plugin, LoadItemDistribution loadItemDistribution) {
        this.plugin = plugin;
        this.loadItemDistribution = loadItemDistribution;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("rcheststocker") && player.isOp()){
            plugin.reloadConfig();
            loadItemDistribution.loader();
        }

        return true;
    }
}
