package org.server.mapcrates;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
            if (args.length < 1){
                System.out.println("Input stop or start");
                return true;
            }
            if (args[0].equals("start")){
                try{
                    airdrop.startDrops(args[1], args[2]);
                    return true;
                }catch (Exception e){
                    System.out.println("Wrong amount of arguments");
                }

            }
            if (args[0].equals("stop")){
                airdrop.stopDrops();
                if (args.length > 2){
                    if (args[1].equals("clear")){
                        airdrop.clear();
                    }
                }
                return true;
            }

        }

        return true;
    }


}
