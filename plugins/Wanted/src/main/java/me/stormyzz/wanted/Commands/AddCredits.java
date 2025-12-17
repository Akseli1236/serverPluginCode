package me.stormyzz.wanted.Commands;

import me.stormyzz.wanted.statistics.playerStats;
import me.stormyzz.wanted.statistics.playerStatsManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class AddCredits implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("wanted.addcredits")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§cUsage: /addcredits <player> <amount>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number.");
            return true;
        }

        UUID uuid = target.getUniqueId();
        playerStats stats = playerStatsManager.getStats(uuid.toString());

        int newCredits = stats.getCredits() + amount;
        stats.setCredits(newCredits);

        playerStatsManager.setStats(uuid.toString(), stats);
        playerStatsManager.saveStats();
        sender.sendMessage("§aAdded §e" + amount + " §acredits to §b" + target.getName() + "§a.");
        return true;
    }
}