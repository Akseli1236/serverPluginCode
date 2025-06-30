package me.stormyzz.wanted.Commands;

import me.stormyzz.wanted.Wanted;
import me.stormyzz.wanted.statistics.CustomScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WantedCommand implements CommandExecutor {

    private final Wanted plugin;

    public WantedCommand(Wanted plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.getMobSpawnerManager().loadConfig();


            sender.sendMessage(ChatColor.GREEN + "Wanted reloaded");

            CustomScoreboard scoreboard = plugin.getCustomScoreboard();
            for (Player player : Bukkit.getOnlinePlayers()) {
                scoreboard.updateScoreboard(player);
            }

            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Usage: /wanted reload");
        return true;
    }
}
