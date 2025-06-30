package org.server.gtamc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.ArgumentCaptor;

public class PlayerDataTest {

    private Plugin mockPlugin = mock(Plugin.class);
    private Player mockPlayer = mock(Player.class);
    private PlayerDeathEvent mockEvent = mock(PlayerDeathEvent.class);
    private WASD mockWASD = mock(WASD.class);
    File mockDataFolder = mock(File.class);

    @BeforeEach
    public void setUp(){
	MockBukkit.mock();
	when(mockPlugin.getDataFolder()).thenReturn(mockDataFolder);
    }

    @AfterEach
    public void tearDown(){
	MockBukkit.unmock();
	File root = new File(System.getProperty("user.dir"));
	File[] allContents = root.listFiles();
	for (File file : allContents){
	    if (file.getName().toLowerCase().contains("mock")){
		deleteDirectoryRecursively(file);
	    }
	    
	}
    }

  private void deleteDirectoryRecursively(File dir) {
    File[] allContents = dir.listFiles();
    if (allContents != null) {
        for (File file : allContents) {
            if (file.isDirectory()) {
                deleteDirectoryRecursively(file);
            } else {
                file.delete();
            }
        }
    }
    dir.delete();
}
    
    @Test
    public void testExample() {
	
	World mockWorld = mock(World.class);
	Location location = new Location(mockWorld, 30.3, 20.2, 12.1, 0, 0);
	when(mockEvent.getEntity()).thenReturn(mockPlayer);
	when(mockPlayer.getPlayer()).thenReturn(mockPlayer);
	when(mockPlayer.getLocation()).thenReturn(location);
	when(mockPlayer.getName()).thenReturn("pasi");

	PlayerData playerData = new PlayerData(mockPlugin, mockWASD);
	playerData.PlayerDeathEvent(mockEvent);

	assertEquals(location, playerData.getLastDeathLocation("pasi"));
    }

    @Test
    public void testOnPlayerJoin() {
    
	PlayerJoinEvent playerJoin = mock(PlayerJoinEvent.class);
	ItemStack mockItem = mock(ItemStack.class);
	PlayerQuitEvent playerQuit = mock(PlayerQuitEvent.class);
	BukkitScheduler mockScheduler = mock(BukkitScheduler.class);
	Server server = mock(Server.class);

	when(mockPlugin.getServer()).thenReturn(server);
	when(server.getScheduler()).thenReturn(mockScheduler);

	when(playerJoin.getPlayer()).thenReturn(mockPlayer);
	when(mockPlayer.getName()).thenReturn("TestPlayer");
	when(mockPlayer.getInventory()).thenReturn(mock(PlayerInventory.class));
	when(mockPlayer.getInventory().getItemInMainHand()).thenReturn(mockItem);
	when(playerQuit.getPlayer()).thenReturn(mockPlayer);

	PlayerData playerData = new PlayerData(mockPlugin, mockWASD);

	playerData.onPlayerJoin(playerJoin);
	
	assertEquals("TestPlayer", playerData.getPlayer("TestPlayer").getName());
	assertTrue(playerData.getPlayerShoots().containsKey("TestPlayer"));
	assertTrue(playerData.getUsableItemBehaviors().containsKey("TestPlayer"));
	
	playerData.onPlayerQuit(playerQuit);

	ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

	assertEquals("TestPlayer", playerData.getPlayer("TestPlayer").getName());
	verify(mockScheduler).runTaskLater(eq(mockPlugin), runnableCaptor.capture(), eq(20L * 60 * 10));

	runnableCaptor.getValue().run();

	assertFalse(playerData.getPlayerShoots().containsKey("TestPlayer"));
	assertTrue(playerData.getUsableItemBehaviors().containsKey("TestPlayer"));

	

	
    }
}
