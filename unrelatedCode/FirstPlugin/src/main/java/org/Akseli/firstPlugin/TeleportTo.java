package org.Akseli.firstPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class TeleportTo implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        Location deathCoords = PlayerData.getLastDeath(player.getName());

        if (command.getName().equalsIgnoreCase("tpback")) {

            if (deathCoords == null) {
                player.sendMessage("You have no recorded death location.");
                return true;
            }

            player.teleport(deathCoords);
            player.sendMessage("You have been teleported to your last death location.");

        }
        if (command.getName().equalsIgnoreCase("lastpos")) {
            sender.sendMessage("Your last death coordinates:" + deathCoords);
        }

        if (command.getName().equalsIgnoreCase("tp")) {
            Player playerFrom = Bukkit.getPlayer(args[0]);
            Player playerWhere = null;

            if (playerFrom == null) {
                return false;
            }
            Location coords = playerFrom.getLocation();
            if (args.length == 2) {
                playerWhere = Bukkit.getPlayer(args[1]);
            }
            else if (args.length == 4) {
                double x_coord = 0, y_coord = 0, z_coord = 0;

                // Parse input values for better reuse
                int x_input = Integer.parseInt(args[1]);
                int y_input = Integer.parseInt(args[2]);
                int z_input = Integer.parseInt(args[3]);

                // Adjust coordinates based on conditions
                x_coord = x_input - coords.getX();
                y_coord = y_input - coords.getY();
                z_coord = z_input - coords.getZ();

                System.out.println(coords.getX() + " " + coords.getY() + " " + coords.getZ());
                System.out.println(x_coord + " " + y_coord + " " + z_coord);



                Location whereTo = playerFrom.getLocation().add(x_coord, y_coord, z_coord);
                playerFrom.teleport(whereTo);
                return true;
            }else {
                sender.sendMessage("Invalid amount of arguments! Expected 2 or 4, found " + args.length);
                return false;
            }

            if (playerWhere != null) {
                playerFrom.teleport(playerWhere.getLocation());
            } else {
                sender.sendMessage("Player not found!");
            }
        }
        return true;
    }

}
