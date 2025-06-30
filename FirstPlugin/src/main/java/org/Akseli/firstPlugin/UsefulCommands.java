package org.Akseli.firstPlugin;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class UsefulCommands implements CommandExecutor {

    private final Plugin plugin;

    public UsefulCommands(Plugin plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Location Coords = player.getLocation();

        if (command.getName().equalsIgnoreCase("location")){
            player.sendMessage("You are now here: X=" + Math.round(Coords.getX()*100)/100D
                    +", Y="+Math.round(Coords.getY()*100)/100D
                    +", Z="+Math.round(Coords.getZ()*100)/100D);
        }

        if (command.getName().equalsIgnoreCase("itempickup")) {

            ((Player) sender).setCanPickupItems(!player.getCanPickupItems());

        }

        if (command.getName().equalsIgnoreCase("rweapons")) {
            Weapon.info.clear();
            Weapon.readFile();
            ShootManager shootManager = new ShootManager(plugin);
            shootManager.updateWeaponData();
        }

        return true;
    }
}
