package org.server.gtamc;

import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Map;

public class Car implements Listener {

    private final Plugin plugin;
    private BukkitTask currentTask = null;
    private boolean boostItemCounter = false;

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
        if (!(player.isInsideVehicle() && player.getVehicle() instanceof Minecart minecart)) {
            return;
        }
        if (movementMap.get("sprint") == 1 && (player.getInventory().contains(Material.GLOW_INK_SAC) || boostItemCounter)) {
            speedMultiplier = 2.5;

            if (!boostItemCounter) {
		boostCounter(player);
                Bukkit.getScheduler().runTaskLater(plugin, ()-> boostItemCounter = false,20*5);
            }

        } else {
            speedMultiplier = 0.7;
        }

        if (currentTask != null && !currentTask.isCancelled()) {
            currentTask.cancel();
            currentTask = null;
        }

        // Check if the player is in a minecart

        if (currentTask == null || currentTask.isCancelled()) {
            currentTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
		    setMove(minecart, right, left, forward, backward, speedMultiplier);
            }, 0, 1);
        }

    }

    void boostCounter(Player player){
	for (ItemStack item : player.getInventory().getContents()) {
	    if (item != null && item.getType() == Material.GLOW_INK_SAC) {
		item.setAmount(item.getAmount() - 1);
	    }
	}
	boostItemCounter = true;
    }

    void setMove(Minecart minecart, double right, double left, double forward, double backward, double speedMultiplier){
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
	minecart.setVelocity(velocity.setY(-0.5));
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
