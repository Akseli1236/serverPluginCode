package org.server.gtamc;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class UsefulCommands implements CommandExecutor {

    private final Plugin plugin;
    private final PlayerData playerData;
    private final WASD wasd;

    public UsefulCommands(Plugin plugin, PlayerData playerData, WASD wasd) {
        this.plugin = plugin;
        this.playerData = playerData;
        this.wasd = wasd;
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

        if (command.getName().equalsIgnoreCase("rweapons") && player.isOp()) {
            ShootManager shootManager = new ShootManager(plugin, playerData, wasd);
            shootManager.updateWeaponData();
        } else if (command.getName().equalsIgnoreCase("rweapons")) {
            player.sendMessage("You do not have permission to use this command!");
        }

        return true;
    }
}
