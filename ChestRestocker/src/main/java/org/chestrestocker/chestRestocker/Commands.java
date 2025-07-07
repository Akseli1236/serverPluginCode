package org.chestrestocker.chestRestocker;

<<<<<<< HEAD
import org.bukkit.Location;
=======
>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79
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
