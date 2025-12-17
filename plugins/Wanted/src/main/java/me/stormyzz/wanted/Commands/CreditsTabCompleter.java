package me.stormyzz.wanted.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CreditsTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        String lowerAlias = alias.toLowerCase();

        if (lowerAlias.equals("credits")) {
            // /credits <player>
            if (args.length == 1 && sender.hasPermission("wanted.credits.others")) {
                String partial = args[0].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String name = player.getName();
                    if (name.toLowerCase().startsWith(partial)) {
                        completions.add(name);
                    }
                }
            }
        } else if (lowerAlias.equals("addcredits")) {
            // /addcredits <player> <amount>
            if (args.length == 1 && sender.hasPermission("wanted.addcredits")) {
                String partial = args[0].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String name = player.getName();
                    if (name.toLowerCase().startsWith(partial)) {
                        completions.add(name);
                    }
                }
            } else if (args.length == 2 && sender.hasPermission("wanted.addcredits")) {
                // Suggest some default amounts
                completions.add("10");
                completions.add("50");
                completions.add("100");
                completions.add("500");
            }
        }

        return completions;
    }
}