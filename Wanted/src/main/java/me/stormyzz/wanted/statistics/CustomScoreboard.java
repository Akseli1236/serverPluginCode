package me.stormyzz.wanted.statistics;
import me.clip.placeholderapi.PlaceholderAPI;
import me.stormyzz.wanted.Wanted;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CustomScoreboard implements Listener {

    private final Wanted plugin;
    private final Map<UUID, Scoreboard> playerScoreboards = new HashMap<>();
    private final Map<UUID, Objective> playerObjectives = new HashMap<>();

    public CustomScoreboard(Wanted plugin) {
        this.plugin = plugin;
    }


    public void updateScoreboard(Player player) {
        if (!plugin.getConfig().getBoolean("scoreboard.enabled", true)) return;

        UUID uuid = player.getUniqueId();
        String world = player.getWorld().getName();
        boolean isHub = world.equalsIgnoreCase("HUB");

        List<String> lines = plugin.getConfig().getStringList(isHub ? "scoreboard.hub.lines" : "scoreboard.default.lines");
        String titleRaw = plugin.getConfig().getString(isHub ? "scoreboard.hub.title" : "scoreboard.default.title", "&b&lServer");
        String title = ChatColor.translateAlternateColorCodes('&', replacePlaceholders(player, titleRaw));

        Scoreboard board = playerScoreboards.get(uuid);
        Objective obj = playerObjectives.get(uuid);

        if (board == null || obj == null) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            if (manager == null) return;

            board = manager.getNewScoreboard();
            obj = board.registerNewObjective("stats", "dummy", title);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);

            playerScoreboards.put(uuid, board);
            playerObjectives.put(uuid, obj);
            player.setScoreboard(board);

            new BukkitRunnable() {
                @Override
                public void run() {
                    updateNametagsFor(player);
                }
            }.runTaskLater(plugin, 1L);

        } else {
            obj.setDisplayName(title);
        }
        for (String entry : board.getEntries()) {
            board.resetScores(entry);
        }

        int score = lines.size();

        Set<String> existingTeams = new HashSet<>();
        for (Team t : board.getTeams()) {
            existingTeams.add(t.getName());
        }

        for (String rawLine : lines) {
            // Replace placeholders and colorize the line
            String line = colorize(replacePlaceholders(player, rawLine));
            if (line.trim().isEmpty()) {
                // Skip empty or whitespace-only lines, decrease score as we won't add it
                score--;
                continue;
            }

            String entry = ChatColor.COLOR_CHAR + "" + (char) ('a' + score % 26);
            String teamName = "line" + score;

            Team team = board.getTeam(teamName);
            if (team == null) {
                team = board.registerNewTeam(teamName);
            }

            // Remove from existing teams set so we know which to unregister later
            existingTeams.remove(teamName);

            if (!team.hasEntry(entry)) {
                team.addEntry(entry);
            }

            String[] parts = splitIntoPrefixSuffix(line);
            String prefix = parts[0] != null ? parts[0] : "";
            String suffix = parts[1] != null ? parts[1] : "";

            team.setPrefix(prefix);
            team.setSuffix(suffix);

            obj.getScore(entry).setScore(score--);
        }

        // Unregister teams no longer needed
        for (String teamName : existingTeams) {
            Team t = board.getTeam(teamName);
            if (t != null) {
                t.unregister();
            }
        }
    }


    private String replacePlaceholders(Player player, String line) {
        // Your custom replacements
        String uuid = player.getUniqueId().toString();
        String worldName = player.getWorld().getName();
        int playersInWorld = (int) Bukkit.getOnlinePlayers().stream().filter(p -> p.getWorld().equals(player.getWorld())).count();

        line = line.replace("%world%", worldName);
        line = line.replace("%players%", String.valueOf(playersInWorld));

        if (!worldName.equalsIgnoreCase("HUB")) {
            playerStats stats = playerStatsManager.getStats(uuid);
            if (stats != null) {
                line = line.replace("%kills%", String.valueOf(stats.getKills()));
                line = line.replace("%deaths%", String.valueOf(stats.getDeaths()));
                line = line.replace("%kdr%", String.format("%.2f", stats.getKDR()));
                line = line.replace("%killstreak%", String.valueOf(stats.getKillstreak()));
                line = line.replace("%wanted%", String.valueOf(stats.getWantedLevel()));
            }

            if (VaultEconomyManager.getEconomy() != null) {
                double balance = VaultEconomyManager.getEconomy().getBalance(player);
                line = line.replace("%balance%", String.valueOf((int) Math.floor(balance)));
            }
        }

        // 🔁 Use PAPI to parse any external placeholders (e.g., LuckPerms)
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            line = PlaceholderAPI.setPlaceholders(player, line);
        }

        return line;
    }

    private String colorize(String message) {
        // Translate legacy color codes
        message = ChatColor.translateAlternateColorCodes('&', message);

        // Translate hex color codes (e.g. &#00FFFF)
        Pattern pattern = Pattern.compile("(?i)&#([0-9A-F]{6})");
        Matcher matcher = pattern.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            String replacement = net.md_5.bungee.api.ChatColor.of("#" + hexColor).toString();
            matcher.appendReplacement(buffer, replacement);
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private void updateNametagsFor(Player viewer) {
        Scoreboard board = viewer.getScoreboard();
        if (board == null) return;

        for (Player target : Bukkit.getOnlinePlayers()) {
            String teamName = "prefix_" + target.getName();

            Team team = board.getTeam(teamName);
            if (team == null) {
                team = board.registerNewTeam(teamName);
            }

            // Get raw prefix from PAPI
            String rawPrefix = PlaceholderAPI.setPlaceholders(target, "%luckperms_prefix%");

            // Remove color codes (&4, §7, etc.)
            String strippedPrefix = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', rawPrefix));

            // Set team prefix with no color
            team.setPrefix(strippedPrefix);

            // Ensure name stays uncolored
            team.setColor(ChatColor.RESET);

            // Clear previous entries
            team.getEntries().forEach(team::removeEntry);
            team.addEntry(target.getName());
        }
    }

    public void updateScoreboardForWorld(String worldName) {
        // Now update scoreboards for all players in the world
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().getName().equals(worldName)) {
                updateScoreboard(player);  // Update scoreboard for each player in this world
            }
        }
    }

    // Listener for player killing another player (updates kills, killstreak)
    @EventHandler
    public void onPlayerKill(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getDamager() instanceof Player) {
                Player attacker = (Player) event.getDamager();
                String uuid = player.getUniqueId().toString();
                playerStats stats = playerStatsManager.getStats(uuid);
                playerStatsManager.setStats(uuid, stats);

                // Update the attacker's stats as well for killstreak and kills
                String attackerUuid = attacker.getUniqueId().toString();
                playerStats attackerStats = playerStatsManager.getStats(attackerUuid);
                playerStatsManager.setStats(attackerUuid, attackerStats);

                // Update scoreboards for both players
                updateScoreboard(player);
                updateScoreboard(attacker);
            }
        }
    }

    // Listener for player death (updates deaths, killstreak, and possibly wanted level)
    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            String uuid = player.getUniqueId().toString();
            playerStats stats = playerStatsManager.getStats(uuid);
            playerStatsManager.setStats(uuid, stats);

            // Reset the player's killstreak
            playerStatsManager.setStats(uuid, stats);

            // Update scoreboard
            updateScoreboard(player);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        String fromWorld = event.getFrom().getWorld().getName();  // World player is leaving
        String toWorld = event.getTo().getWorld().getName();  // World player is entering

        new BukkitRunnable() {
            @Override
            public void run() {
                // Debugging statement for updating scoreboards
                updateScoreboardForWorld(fromWorld);
                updateScoreboardForWorld(toWorld);
                updateScoreboard(player);
                updateNametagsFor(player);
            }
        }.runTaskLater(Bukkit.getPluginManager().getPlugin("Wanted"), 1L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        updateScoreboard(player);
        updateScoreboardForWorld(player.getWorld().getName());
        updateNametagsFor(player);

        // Regular updates for the joining player
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    updateScoreboard(player);
                    updateNametagsFor(player);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Wanted"), 0L, 100L);  // Update every 5 seconds
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        playerScoreboards.remove(uuid);
        playerObjectives.remove(uuid);
        Scoreboard mainBoard = Bukkit.getScoreboardManager().getMainScoreboard();

        Team team = mainBoard.getTeam("prefix_" + player.getName());
        if (team != null) {
            team.removeEntry(player.getName());
            team.unregister(); // remove to avoid scoreboard bloat
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                updateScoreboardForWorld(player.getWorld().getName());
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String oldWorld = event.getFrom().getName();
        String newWorld = player.getWorld().getName();

        new BukkitRunnable() {
            @Override
            public void run() {
                // Update the scoreboard for both the old and new world
                updateScoreboardForWorld(oldWorld);
                updateScoreboardForWorld(newWorld);
                updateNametagsFor(player);
            }
        }.runTaskLater(Bukkit.getPluginManager().getPlugin("Wanted"), 1L);
    }

    private static final int MAX_ENTRY_LENGTH = 16;

    private String[] splitIntoPrefixSuffix(String text) {
        if (text == null) return new String[] {"", ""};

        if (text.length() <= MAX_ENTRY_LENGTH) {
            return new String[] {text, ""};
        }

        // We'll split based on visible characters, preserving color codes
        StringBuilder prefix = new StringBuilder();
        StringBuilder suffix = new StringBuilder();

        int visibleChars = 0;
        boolean isInColorCode = false;

        List<String> codes = new ArrayList<>();
        String lastColorCode = "";

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == ChatColor.COLOR_CHAR) {
                // Start of color code sequence
                // Hex color: §x§R§R§G§G§B§B
                if (i + 12 < text.length() && text.charAt(i+1) == 'x') {
                    String hexCode = text.substring(i, i + 14); // §x§R§R§G§G§B§B = 14 chars total
                    if (visibleChars < MAX_ENTRY_LENGTH) {
                        prefix.append(hexCode);
                    } else {
                        suffix.append(hexCode);
                    }
                    lastColorCode = hexCode;
                    i += 13; // skip the whole hex code
                    continue;
                } else if (i + 1 < text.length()) {
                    // legacy color code § + char
                    String code = text.substring(i, i + 2);
                    if (visibleChars < MAX_ENTRY_LENGTH) {
                        prefix.append(code);
                    } else {
                        suffix.append(code);
                    }
                    lastColorCode = code;
                    i += 1;
                    continue;
                }
            }

            if (visibleChars < MAX_ENTRY_LENGTH) {
                prefix.append(c);
                visibleChars++;
            } else {
                suffix.append(c);
            }
        }

        // Prepend last color code to suffix if not empty and suffix doesn't start with color code
        if (suffix.length() > 0 && !suffix.toString().startsWith("" + ChatColor.COLOR_CHAR)) {
            suffix.insert(0, lastColorCode);
        }

        // Just in case suffix is too long (should not be, but to be safe)
        if (suffix.length() > MAX_ENTRY_LENGTH) {
            suffix = new StringBuilder(suffix.substring(0, MAX_ENTRY_LENGTH));
        }

        return new String[] {prefix.toString(), suffix.toString()};
    }

}

