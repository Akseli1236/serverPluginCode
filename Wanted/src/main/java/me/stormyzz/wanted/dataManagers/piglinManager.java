package me.stormyzz.wanted.dataManagers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class piglinManager {
    private final JavaPlugin plugin;
    private final Map<Player, List<PigZombie>> playerPiglinsMap = new HashMap<>();
    private final Map<Player, Integer> playerBackupTaskMap = new HashMap<>();

    public piglinManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void spawnPiglins(Player victim, Player killer) {
        Location victimLocation = victim.getLocation();
        World world = victimLocation.getWorld();

        if (world != null) {
            int rndx = (int) (Math.random() * 5);
            int rndz = (int) (Math.random() * 5);
            Location spawnLocation = victimLocation.clone().add(rndx, 0, rndz);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                List<PigZombie> piglins = playerPiglinsMap.computeIfAbsent(killer, k -> new ArrayList<>());

                // Spawn the initial Zombified Piglins
                for (int i = 0; i < 3; i++) {
                    PigZombie piglin = (PigZombie) world.spawnEntity(spawnLocation, EntityType.ZOMBIFIED_PIGLIN);
<<<<<<< HEAD
                    piglin.setBaby(false);
=======
                    piglin.setAdult();
>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79
                    piglins.add(piglin);
                    piglin.setTarget(killer);
                    equipPiglin(piglin);
                }

                // Schedule backup spawn
                int backupTaskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Location killerLocation = killer.getLocation();
                    Location backupSpawnLocation = killerLocation.clone().add(rndx, 0, rndz);

                    killer.sendMessage(ChatColor.RED + "Looks like they called for backup");

                    for (int i = 0; i < 3; i++) {
                        PigZombie piglin = (PigZombie) world.spawnEntity(backupSpawnLocation, EntityType.ZOMBIFIED_PIGLIN);
<<<<<<< HEAD
                        piglin.setBaby(false);
=======
                        piglin.setAdult();
>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79
                        piglins.add(piglin);
                        piglin.setTarget(killer);
                        equipPiglin(piglin);
                    }
                }, 200L).getTaskId(); // 10 seconds delay (200 ticks)

                playerBackupTaskMap.put(killer, backupTaskId);

                // Schedule despawn after 30 seconds
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (killer.isOnline() && !killer.isDead()) {
                        killer.sendMessage(ChatColor.GREEN + "Looks like you got away this time...");
                    }
                    despawnPiglins(killer);
                }, 600L); // 30 seconds delay (600 ticks)// 30 seconds delay (600 ticks)

            }, 20L); // Initial spawn delay (1 second)
        }
    }

    private void equipPiglin(PigZombie piglin) {
        piglin.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
        piglin.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        piglin.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        piglin.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        piglin.setCustomName(ChatColor.AQUA + "Cop");
        piglin.setCustomNameVisible(true);
    }

    public void despawnPiglins(Player killer) {
        List<PigZombie> piglins = playerPiglinsMap.remove(killer);
        if (piglins != null) {
            for (PigZombie piglin : piglins) {
                if (piglin != null && !piglin.isDead()) {
                    piglin.remove();
                }
            }
        }

        // Cancel backup task if running
        Integer backupTaskId = playerBackupTaskMap.remove(killer);
        if (backupTaskId != null) {
            Bukkit.getScheduler().cancelTask(backupTaskId);
        }
    }

    public void onKillerDeath(Player killer) {
        despawnPiglins(killer);
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79
