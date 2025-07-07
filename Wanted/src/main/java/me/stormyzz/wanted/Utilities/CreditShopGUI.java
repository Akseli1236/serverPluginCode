package me.stormyzz.wanted.Utilities;

import me.stormyzz.wanted.statistics.playerStatsManager;
import me.stormyzz.wanted.statistics.playerStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

public class CreditShopGUI {

    public static YamlConfiguration config;
    private static String cachedTitle;

    public static void loadConfig(Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "creditshop.yml");
        if (!file.exists()) {
            plugin.saveResource("creditshop.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
        cachedTitle = ChatColor.translateAlternateColorCodes('&', config.getString("gui.title", "&6Credit Shop"));
    }

    public static void openShop(Player player) {
        int rows = config.getInt("gui.rows", 3);
        Inventory inv = Bukkit.createInventory(null, rows * 9, cachedTitle);

        ConfigurationSection section = config.getConfigurationSection("items");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection itemSec = section.getConfigurationSection(key);
            if (itemSec == null) continue;

            String slotString = itemSec.getString("slot", "");
            if (slotString.isEmpty()) continue;

            String[] slotStrings = slotString.split(",");
            List<Integer> slots = new ArrayList<>();
            for (String s : slotStrings) {
                try {
                    slots.add(Integer.parseInt(s.trim()));
                } catch (NumberFormatException e) {
                    // Ignore invalid numbers
                }
            }

            Material material = Material.matchMaterial(itemSec.getString("material", "BARRIER"));
            if (material == null) material = Material.BARRIER;

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            boolean isFiller = itemSec.getBoolean("filler", false);
            if (isFiller) {
                if (meta != null) {
                    meta.setDisplayName(ChatColor.RESET.toString());
                    meta.setLore(Collections.emptyList());
                    item.setItemMeta(meta);
                }
                for (int slot : slots) {
                    inv.setItem(slot, item);
                }
                continue;
            }

            String nameRaw = itemSec.getString("name", "Unnamed Item");
            String name = ChatColor.translateAlternateColorCodes('&', nameRaw);

            List<String> loreRaw = itemSec.getStringList("lore");
            List<String> lore = new ArrayList<>();
            for (String line : loreRaw) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }

            if (meta != null) {
                meta.setDisplayName(name);
                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            for (int slot : slots) {
                inv.setItem(slot, item);
            }
        }

        player.openInventory(inv);
    }

    public static void handleClick(Player player, ItemStack clickedItem) {
        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        String clickedName = clickedItem.getItemMeta().getDisplayName();
        if (clickedName == null) return;

        String strippedClickedName = ChatColor.stripColor(clickedName);

        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection == null) {
            player.sendMessage("§cShop configuration error: no items found.");
            return;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSec = itemsSection.getConfigurationSection(key);
            if (itemSec == null) continue;

            // Skip fillers or items with no name
            if (itemSec.getBoolean("filler", false)) continue;

            String configNameRaw = itemSec.getString("name");
            if (configNameRaw == null) continue;

            String configName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', configNameRaw));

            if (!configName.equals(strippedClickedName)) continue;

            int cost = itemSec.getInt("cost", -1);
            if (cost < 0) {
                player.sendMessage("§cShop configuration error: invalid cost for item " + configName);
                return;
            }

            String uuid = player.getUniqueId().toString();
            playerStats stats = playerStatsManager.getStats(uuid);

            if (stats.getCredits() < cost) {
                player.sendMessage("§cYou don't have enough credits.");
                return;
            }

            // Deduct credits and save stats
            stats.setCredits(stats.getCredits() - cost);
            playerStatsManager.setStats(uuid, stats);
            playerStatsManager.saveStats();

            // Run commands, replacing %player%
            for (String cmd : itemSec.getStringList("commands")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
            }

            player.sendMessage("§aPurchased: §e" + clickedName + " §afor §6" + cost + " §acredits.");
            player.closeInventory();

            return; // Done
        }

        // If none matched
        player.sendMessage("§cThis item is not purchasable.");
    }

    public static void reloadConfig(Plugin plugin) {
        loadConfig(plugin);
    }

}
