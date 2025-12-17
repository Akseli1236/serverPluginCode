package org.server.gtamc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
    
public class CarTest {

    Plugin mockPlugin = mock(Plugin.class);
    Minecart mockCart = mock(Minecart.class);
    Player mockPlayer = mock(Player.class);
    ItemStack mockItem = mock(ItemStack.class);
    PlayerInventory mockInventory = mock(PlayerInventory.class);
    BukkitTask mockScheduler = mock(BukkitTask.class);
    Location mockLocation = mock(Location.class);
    World mockWorld = mock(World.class);

    @Test
    public void testCar() {
	
	when(mockPlayer.isInsideVehicle()).thenReturn(true);
	when(mockPlayer.getVehicle()).thenReturn(mockCart);
	when(mockLocation.getYaw()).thenReturn(90.0f);
	when(mockCart.getLocation()).thenReturn(mockLocation);
	when(mockCart.getWorld()).thenReturn(mockWorld);

	Car car = new Car(mockPlugin);
        car.setMove(mockCart, 0, 0, 0, 0, 0);

	Vector expectedVelocity = new Vector(0.0, -0.5, 0.0);
	verify(mockCart).setVelocity(expectedVelocity);

	car.setMove(mockCart, 0, 0, 1, 0, 1);
	Vector expectedVelocity2 = new Vector(-1.2246467991473532E-16, -0.5, -1.0);
	verify(mockCart).setVelocity(expectedVelocity2);

	car.setMove(mockCart, 0, 0, 0, 1, 1);
	Vector expectedVelocity3 = new Vector(1.2246467991473532E-16, -0.5, 1.0);
	verify(mockCart).setVelocity(expectedVelocity3);
	
    }

    @Test
    public void testCarBoost() {
	
	ItemStack[] contents = new ItemStack[36]; // typical player inventory size
	contents[0] = mockItem; // put the glow ink sac in slot 0

	when(mockPlayer.getInventory()).thenReturn(mockInventory);
	when(mockInventory.getContents()).thenReturn(contents);
	when(mockItem.getType()).thenReturn(Material.GLOW_INK_SAC);
	when(mockItem.getAmount()).thenReturn(64);

	
	Car car = new Car(mockPlugin);
	car.boostCounter(mockPlayer);

	verify(mockItem).setAmount(63);
    }

}
