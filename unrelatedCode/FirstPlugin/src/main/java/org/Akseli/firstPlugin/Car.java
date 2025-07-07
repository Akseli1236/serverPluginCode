package org.Akseli.firstPlugin;

import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Map;

public class Car implements Listener {

    private final Plugin plugin;
    private BukkitTask currentTask = null;

    public Car(Plugin plugin) {
        this.plugin = plugin;
    }

    public void moveCar(PacketEvent event, Map<String, Integer> movementMap) {
        Player player = event.getPlayer();

        double forward = movementMap.get("forward");
        double backward = movementMap.get("backward");
        double right = movementMap.get("right");
        double left = movementMap.get("left");
        double speedMultiplier;
        if (movementMap.get("sprint") == 1) {
            speedMultiplier = 2.5;
        } else {
            speedMultiplier = 0.7;
        }

        if (currentTask != null && !currentTask.isCancelled()) {
            currentTask.cancel();
            currentTask = null;
        }

        // Check if the player is in a minecart
        if (player.isInsideVehicle() && player.getVehicle() instanceof Minecart minecart) {
            if (currentTask == null || currentTask.isCancelled()) {
                currentTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    float yaw = getYaw(minecart, right, left, forward, backward);

                    // Calculate the x and z components of the velocity to move the minecart straight
                    double x = 0; // Moves in the direction the player is facing
                    double z = 0; // Moves in the direction the player is facing

                    // Check movement input and adjust velocity accordingly
                    if (forward == 1) {
                        x = -Math.sin(Math.toRadians(yaw)); // Move in the direction the minecart is facing (x-axis)
                        z = Math.cos(Math.toRadians(yaw)); // Move in the direction the minecart is facing (z-axis)
                    } else if (backward == 1) {
                        x = Math.sin(Math.toRadians(yaw)); // Move in the direction the minecart is facing (x-axis)
                        z = -Math.cos(Math.toRadians(yaw)); // Move in the direction the minecart is facing (z-axis)
                    }

                    Vector velocity = new Vector(x, 0, z).multiply(speedMultiplier); // Adjust the speed multiplier if needed
                    minecart.setMaxSpeed(100);
                    minecart.setVelocity(velocity);

                }, 0, 1);
            }

        }

    }

    private float getYaw(Minecart minecart, double right, double left, double forward, double backward) {
        float yaw = minecart.getLocation().getYaw();  // Get player's yaw (direction they're facing)
        yaw += 90;

        if (yaw > 180) {
            yaw -= 360;
        } else if (yaw < -180) {
            yaw += 360;
        }

        float steeringSpeed = 8.0f;
        if (right == 1 && forward == 1) {
            yaw += steeringSpeed; // Rotate right (clockwise)
        } else if (left == 1 && forward == 1) {
            yaw -= steeringSpeed; // Rotate left (counterclockwise)
        } else if (backward == 1 && right == 1) {
            yaw -= steeringSpeed;
        } else if (backward == 1 && left == 1) {
            yaw += steeringSpeed;
        }
        if (yaw > 180) yaw -= 360;
        if (yaw < -180) yaw += 360;

        return yaw;
    }
}
