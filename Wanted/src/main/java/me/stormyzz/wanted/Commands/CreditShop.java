package me.stormyzz.wanted.Commands;

import me.stormyzz.wanted.Utilities.CreditShopGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreditShop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can open the credit shop.");
            return true;
        }

        Player player = (Player) sender;
        CreditShopGUI.openShop(player);
        return true;
    }
}

