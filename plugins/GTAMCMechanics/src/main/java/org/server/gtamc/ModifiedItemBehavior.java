package org.server.gtamc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ModifiedItemBehavior {

    private boolean bandageCooldown = false;
    private final Plugin plugin;

    public ModifiedItemBehavior(Plugin plugin) {
        this.plugin = plugin;
    }

    public void useBandage(Player player, ItemStack item) {

        if (player.getHealth() >= 20 || bandageCooldown) {
            return;
        }

        player.setHealth(Math.min(20, player.getHealth() + 4));
        item.setAmount(item.getAmount() - 1);
        bandageCooldown = true;

        Bukkit.getScheduler().runTaskLater(plugin, () -> bandageCooldown = false, 40);

    }
}
