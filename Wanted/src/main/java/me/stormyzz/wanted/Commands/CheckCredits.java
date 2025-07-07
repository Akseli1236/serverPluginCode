package me.stormyzz.wanted.Commands;

import me.stormyzz.wanted.statistics.playerStats;
import me.stormyzz.wanted.statistics.playerStatsManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckCredits implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            // /credits → self view
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cOnly players can check their own credits.");
                return true;
            }

            Player player = (Player) sender;
            playerStats stats = playerStatsManager.getStats(player.getUniqueId().toString());
            player.sendMessage("§7You have §a" + stats.getCredits() + "§7 credits.");
            return true;
        }

        // /credits <player> → view other's credits
        if (!sender.hasPermission("wanted.credits.others")) {
            sender.sendMessage("§cYou do not have permission to check other players' credits.");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        playerStats stats = playerStatsManager.getStats(target.getUniqueId().toString());
        sender.sendMessage("§e" + target.getName() + "§7 has §a" + stats.getCredits() + "§7 credits.");
        return true;
    }
}