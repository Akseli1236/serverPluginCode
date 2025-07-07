package org.Akseli.firstPlugin;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import org.bukkit.event.EventHandler;

import java.util.Arrays;
import java.util.Random;

public class ArrowCount implements Listener, CommandExecutor {

    private final Plugin plugin;
    private int additionalArrows = 0;
    double spread = 0;
    float spread2 = 0.0f;

    public void setArrowCount(int count) {
        additionalArrows = count;
    }
    public ArrowCount(Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("arrows")) {
            setArrowCount(Integer.parseInt(args[0]));
        }

        if (command.getName().equalsIgnoreCase("repair")) {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof Damageable) {
                ((Damageable) meta).setDamage(0); // Full repair
                item.setItemMeta(meta);
            }
        }

        if (command.getName().equalsIgnoreCase("arrowspread")) {
            spread = Double.parseDouble(args[0]) / 10;
            spread2 = Float.parseFloat(args[0]);
        }
        return true;
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        // Check if the shooter is a player and if the projectile is an arrow
        Entity shooter = event.getEntity();
        if (shooter instanceof Player && event.getProjectile() instanceof Arrow originalArrow) {
            Location arrowLocation = originalArrow.getLocation();
            Vector originalDirection = originalArrow.getVelocity();

            // Number of additional arrows to shoot

            Random random = new Random();
            for (int i = 0; i < additionalArrows; i++) {
                // Spawn a new arrow
                Vector offset = randomOffset(random);
                Arrow extraArrow = shooter.getWorld().spawnArrow(
                        arrowLocation,
                        originalDirection.clone().add(offset), // Slightly modify direction
                        (float) originalArrow.getVelocity().length(),        // Match original speed
                        spread2 // Spread (higher value = wider spread)
                );
                extraArrow.setShooter((ProjectileSource) shooter); // Set the shooter of the arrow
            }
        }
    }

    /**
     * Generate a small random offset for the arrow direction.
     *
     * @param random Random instance
     * @return Random vector offset
     */
    private Vector randomOffset(Random random) {
        return new Vector(
                (random.nextDouble() - 0.5) * spread,
                (random.nextDouble() - 0.5) * spread,
                (random.nextDouble() - 0.5) * spread
        );
    }
}
