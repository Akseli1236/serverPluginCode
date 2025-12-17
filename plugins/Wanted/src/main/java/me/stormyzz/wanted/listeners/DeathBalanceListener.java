package me.stormyzz.wanted.listeners;

import me.stormyzz.wanted.statistics.VaultEconomyManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeathBalanceListener implements Listener {

    private final Plugin plugin;
    private final Map<Player, Double> lostBalanceMap = new HashMap<>();

    public DeathBalanceListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Economy economy = VaultEconomyManager.getEconomy();
        if (economy == null) {
            Bukkit.getLogger().warning("Economy is not available, cannot process player death.");
            return;
        }

        double balance = economy.getBalance(player);
        double balanceLost = Math.floor(balance * 0.20);

        if (balanceLost <= 0)
            return;

        lostBalanceMap.put(player, balanceLost);
        economy.withdrawPlayer(player, balanceLost);

        ItemStack nugget = new ItemStack(Material.GOLD_NUGGET);
        Item item = player.getWorld().dropItem(player.getLocation(), nugget);
        item.setMetadata("lostBalance", new FixedMetadataValue(plugin, balanceLost));
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        EntityType type = entity.getType();

        if (type == EntityType.ZOMBIE || type == EntityType.SKELETON) {
            // Only drop nugget from naturally spawned or plugin-spawned mobs
            ItemStack nugget = new ItemStack(Material.GOLD_NUGGET);
            Item item = entity.getWorld().dropItem(entity.getLocation(), nugget);
            item.setMetadata("mobReward", new FixedMetadataValue(plugin, true));

            // Optionally remove default drops (optional)
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        Item item = event.getItem();
        ItemStack stack = item.getItemStack();

        if (stack.getType() != Material.GOLD_NUGGET)
            return;

        Economy economy = VaultEconomyManager.getEconomy();
        if (economy == null) {
            Bukkit.getLogger().warning("Economy is not available, cannot process item pickup.");
            return;
        }

        List<MetadataValue> meta = item.getMetadata("lostBalance");
        if (!meta.isEmpty()) {
            double balanceLost = 0.0;
            MetadataValue metadataValue = meta.get(0);
            if (metadataValue.value() instanceof Double) {
                balanceLost = Math.floor((double) metadataValue.value());
            }

            int balanceLostInt = (int) balanceLost;
            economy.depositPlayer(player, balanceLostInt);
            player.sendMessage(ChatColor.GOLD + "You picked up $" + balanceLostInt + " from a dead player.");
        } else if (item.hasMetadata("mobReward")) {
            int reward = (int) (Math.random() * 15) + 1;
            economy.depositPlayer(player, reward);
            player.sendMessage(ChatColor.GOLD + "You picked up $" + reward + " from a criminal.");
        } else {
            return; // not our nugget
        }

        // Cancel pickup and delete the nugget
        event.setCancelled(true);
        item.remove();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Double balanceLost = lostBalanceMap.remove(player);

        if (balanceLost != null) {
            int balanceLostInt = (int) Math.floor(balanceLost);
            player.sendMessage(ChatColor.GOLD + "You wake up, your wallet missing $" + balanceLostInt + ".");
        }
    }
}
