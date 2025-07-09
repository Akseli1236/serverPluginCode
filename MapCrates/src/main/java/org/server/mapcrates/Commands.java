package org.server.mapcrates;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class Commands implements CommandExecutor{

    private Plugin plugin;
    private Airdrop airdrop;

    public Commands(Plugin plugin){
        this.plugin = plugin;
        this.airdrop = new Airdrop(plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equals("airdrops")){
            if (args[0].equals("start")){
                airdrop.startDrops();
                return true;
            }
            if (args[0].equals("stop")){
                airdrop.stopDrops();
                return true;
            }

        }

        return true;
    }


}
