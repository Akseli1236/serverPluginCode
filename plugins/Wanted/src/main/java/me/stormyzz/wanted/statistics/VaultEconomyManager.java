package me.stormyzz.wanted.statistics;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomyManager {
    private static Economy economy = null;

    /**
     * Setup Vault economy.
     *
     * @return true if the economy is successfully set up, false otherwise.
     */
    public static boolean setupEconomy() {
        // Check if Vault is present and enabled
        if (Bukkit.getPluginManager().getPlugin("Vault") == null
                || !Bukkit.getPluginManager().getPlugin("Vault").isEnabled()) {
            Bukkit.getLogger().warning("Vault plugin not found or not enabled.");
            return false;
        }

        // Try to get the economy service from Vault
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Bukkit.getLogger().warning("No economy provider found through Vault.");
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    /**
     * Get the economy instance.
     *
     * @return the economy provider if available, otherwise null.
     */
    public static Economy getEconomy() {
        if (economy == null) {
            Bukkit.getLogger().warning("Vault economy provider is null!");
        }
        return economy;
    }
}
