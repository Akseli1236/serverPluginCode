package org.Akseli.restartPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;

public class RestartPlugin extends JavaPlugin {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("restartserver")) {
            // Send a starting message

            // Start the countdown task
            int countdownTime = 5;

            // If an argument is provided, try to parse it as an integer
            if (args != null && args.length == 1) {
                try {
                    countdownTime = Integer.parseInt(args[0]);

                    // Ensure the countdown is positive
                    if (countdownTime <= 0) {
                        sender.sendMessage("Please enter a positive number for the countdown.");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    // If the argument is not a valid integer, inform the user
                    sender.sendMessage("Invalid number! Please enter a valid integer for the countdown.");
                    return false;
                }
            }else if (args != null && args.length > 1){
                sender.sendMessage("Invalid amount of arguments! Expected 0 or 1, found " + args.length);
                return false;
            }
            String message = String.format("Server restart in %d seconds...", countdownTime);
            sender.sendMessage(message);

            // Start the countdown task
            final int finalCountdownTime = countdownTime;
            new BukkitRunnable() {
                int countdown = finalCountdownTime;

                @Override
                public void run() {
                    if (countdown > 0) {
                        // Notify players every second
                        Bukkit.broadcastMessage("Server restarting in " + countdown + " seconds...");
                        countdown--;
                    } else {
                        // When countdown is finished, restart the server by executing the batch file
                        try {
                            // Run the batch file
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
                            Runtime.getRuntime().exec("cmd /c start C:\\Users\\Akseli\\Desktop\\MCserver\\start.bat");
                            Bukkit.broadcastMessage("Server is restarting now...");
                        } catch (IOException e) {
                            e.printStackTrace();
                            Bukkit.broadcastMessage("Failed to restart the server.");
                        }
                        cancel(); // Stop the task once the batch file is called
                    }
                }
            }.runTaskTimer(this, 0L, 20L); // Run the task every second (20 ticks per second)

            return true;
        }
        return false;
    }
}