package me.stormyzz.wanted.listeners;

import me.stormyzz.wanted.Wanted;
import me.stormyzz.wanted.statistics.VaultEconomyManager;
import me.stormyzz.wanted.statistics.playerStats;
import me.stormyzz.wanted.statistics.playerStatsManager;
import me.stormyzz.wanted.dataManagers.piglinManager; // Import PiglinManager
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKillListener implements Listener {

    private final piglinManager piglinManager;

    public PlayerKillListener(Wanted plugin) {
        this.piglinManager = new piglinManager(plugin); // Initialize PiglinManager with plugin instance
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent e) {
        Player victim = e.getEntity(); // The player who died
        Player killer = victim.getKiller();

        // Add a death to the victim's stats
        String victimUUID = victim.getUniqueId().toString();
        playerStats victimStats = playerStatsManager.getStats(victimUUID);
        victimStats.addDeath();
        playerStatsManager.setStats(victimUUID, victimStats);

        if (killer != null && killer != victim) {
            // Get the economy instance from VaultEconomyManager
            Economy economy = VaultEconomyManager.getEconomy();
            if (economy == null) {
                return; // If economy is not available, we cannot proceed with the reward
            }

            // Add a kill and +1 wanted level to the killer's stats
            String killerUUID = killer.getUniqueId().toString();
            playerStats killerStats = playerStatsManager.getStats(killerUUID);
            killerStats.addKill();
            killerStats.addWantedLevel();
            playerStatsManager.setStats(killerUUID, killerStats);

            killer.sendMessage(ChatColor.RED + "Start running!");
            killer.sendMessage(
                    ChatColor.RED + "Your wanted level has been increased! It is now: " + killerStats.getWantedLevel());

            // Deposit money to the killer based on the victim's wanted level
            double reward = victimStats.getWantedLevel() * 5.0;
            if (reward > 0.0) {
                economy.depositPlayer(killer, reward);
                killer.sendMessage(ChatColor.GOLD + "You received " + reward + "$ for eliminating " + victim.getName()
                        + " with the wanted level " + victimStats.getWantedLevel());
            }

            // Delegate piglin spawning to PiglinManager
            piglinManager.spawnPiglins(victim, killer);
        }

        // Remove the victim's wanted level after processing the killer
        victimStats.removeWantedLevel();
    }

    @EventHandler
    public void onKillerDeath(PlayerDeathEvent e) {
        Player killer = e.getEntity();

        if (killer != null) {
            // Delegate piglin despawning to PiglinManager
            piglinManager.onKillerDeath(killer);
        }
    }
}
