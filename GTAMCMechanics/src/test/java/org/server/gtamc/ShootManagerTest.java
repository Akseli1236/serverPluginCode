package org.server.gtamc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.MockedConstruction;

public class ShootManagerTest {

    private Plugin mockPlugin = mock(Plugin.class);
    private Player mockPlayer = mock(Player.class);
    private Map<String, Shoot> shoots = mock(Map.class);


     @BeforeEach
    public void setUp() {
        MockBukkit.mock(); // Start mocked server
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock(); // Stop mocked server
    }
    
    @Test
    public void testOnExplosionEvent() {
	ShootManager mockShootManager = new ShootManager(mockPlugin, mock(PlayerData.class), mock(WASD.class));
	EntityExplodeEvent mockExplode = mock(EntityExplodeEvent.class);
	List<Block> mockBlockList = mock(List.class);
	TNTPrimed mockTnt = mock(TNTPrimed.class);

	when(mockExplode.getEntity()).thenReturn(mockTnt);
	when(mockExplode.blockList()).thenReturn(mockBlockList);

	mockShootManager.onExplosionEvent(mockExplode);
	verify(mockExplode).getEntity();
	verify(mockBlockList).clear();
	
    }

    @Test
    public void testOnItemSwitch(){
	PlayerItemHeldEvent mockHeldEvent = mock(PlayerItemHeldEvent.class);
	PlayerInteractEvent mockInteractEvent = mock(PlayerInteractEvent.class);
	PlayerDropItemEvent mockDropItem = mock(PlayerDropItemEvent.class);
	EntityPickupItemEvent mockPickItem = mock(EntityPickupItemEvent.class);
	PlayerDeathEvent mockDeath = mock(PlayerDeathEvent.class);
	PlayerRespawnEvent mockRespawn = mock(PlayerRespawnEvent.class);
	InventoryOpenEvent mockDrop = mock(InventoryOpenEvent.class);
	
	Shoot mockShoot = mock(Shoot.class);
	PlayerData mockData = mock(PlayerData.class);
	
	when(mockData.getPlayerShoots()).thenReturn(shoots);

	ShootManager mockShootManager = new ShootManager(mockPlugin, mockData, mock(WASD.class));
	
	when(mockHeldEvent.getPlayer()).thenReturn(mockPlayer);
	when(mockInteractEvent.getPlayer()).thenReturn(mockPlayer);
	when(mockDropItem.getPlayer()).thenReturn(mockPlayer);
	when(mockPickItem.getEntity()).thenReturn(mockPlayer);
	when(mockDeath.getEntity()).thenReturn(mockPlayer);
	when(mockRespawn.getPlayer()).thenReturn(mockPlayer);
	when(mockDrop.getPlayer()).thenReturn(mockPlayer);
	
	when(mockPlayer.getName()).thenReturn("TestPlayer");
	when(shoots.get("TestPlayer")).thenReturn(mockShoot);
	when(shoots.containsKey("TestPlayer")).thenReturn(true);

	mockShootManager.onItemSwitch(mockHeldEvent);
	mockShootManager.onPlayerDropItem(mockDropItem);
	mockShootManager.onPlayerInteract(mockInteractEvent);
	mockShootManager.onPlayerPickItemEvent(mockPickItem);
	mockShootManager.onPlayerDeath(mockDeath);
	mockShootManager.onPlayerRespawn(mockRespawn);
	mockShootManager.onPlayerDropFromInventory(mockDrop);
	
	verify(mockShoot).onItemSwitch(mockHeldEvent);
	verify(mockShoot).onPlayerInteract(mockInteractEvent);
	verify(mockShoot).onPlayerItemPickup(mockPickItem);
	verify(mockShoot).onPlayerDropItem(mockDropItem);
	verify(mockShoot).onPlayerDeath(mockDeath);
	verify(mockShoot).onPlayerRespawn(mockRespawn);
	verify(mockShoot).setInventoryOpen(true, mockPlayer);
    }

    @Test
    public void testResetItemMeta(){
	
	ItemStack item = new ItemStack(Material.IRON_SWORD, 1);
	ItemMeta meta = item.getItemMeta();
	((Damageable) meta).setDamage(15);
	meta.setDisplayName("§cEpic Sword");
	item.setItemMeta(meta);

	assertEquals("§cEpic Sword", item.getItemMeta().getDisplayName());

	ShootManager shootManager = new ShootManager(mockPlugin, mock(PlayerData.class), mock(WASD.class));

	// Act
	shootManager.resetItemMeta(item);

	// Assert
	assertEquals(Material.IRON_SWORD, item.getType());
	assertEquals(1, item.getAmount());

	Damageable resultMeta = (Damageable) item.getItemMeta();
	assertEquals(15, resultMeta.getDamage());
	
	assertEquals("", item.getItemMeta().getDisplayName());

    }

    @Test
    public void testOnEntityDamageByEntity(){
	EntityDamageByEntityEvent mockEvent = mock(EntityDamageByEntityEvent.class);
	Firework mockFireWork = mock(Firework.class);
	ShootManager shootManager = new ShootManager(mockPlugin, mock(PlayerData.class), mock(WASD.class));

	when(mockEvent.getDamager()).thenReturn(mockFireWork);

	shootManager.onEntityDamageByEntity(mockEvent);

	verify(mockEvent).setCancelled(true);

    }

    @Test
    public void testOnInventoryClick(){
	InventoryClickEvent mockEvent = mock(InventoryClickEvent.class);
	Shoot mockShoot = mock(Shoot.class);
	ItemStack mockItem = mock(ItemStack.class);
	InventoryAction mockAction = InventoryAction.PICKUP_ALL;

	PlayerData mockData = mock(PlayerData.class);
	
	when(mockData.getPlayerShoots()).thenReturn(shoots);

	// Set up mocks
	when(mockEvent.getWhoClicked()).thenReturn(mockPlayer);
	when(mockPlayer.getName()).thenReturn("TestPlayer");
	when(mockEvent.getAction()).thenReturn(mockAction);
	when(mockEvent.getSlot()).thenReturn(5);
	when(mockEvent.getCurrentItem()).thenReturn(mockItem);
	when(shoots.get("TestPlayer")).thenReturn(mockShoot);

	// Setup shoot manager with shoot map

	ShootManager shootManager = new ShootManager(mockPlugin, mockData, mock(WASD.class));

	// Act
	shootManager.onInventoryClick(mockEvent);

	// Verify
	verify(mockShoot).setInventoryOpen(true, mockPlayer);
	verify(mockShoot).setInventoryAction(mockAction);
	verify(mockShoot).setItemSlot(5);
	verify(mockShoot).removeEffects(mockPlayer);
	verify(mockShoot).loadWeapons(mockItem, mockPlayer);
    }

    @Test
    public void testOnPlayerFallDamage() {
	// Mocks
	EntityDamageEvent mockEvent = mock(EntityDamageEvent.class);
	Shoot mockShoot = mock(Shoot.class);

	PlayerData mockData = mock(PlayerData.class);
	
	when(mockData.getPlayerShoots()).thenReturn(shoots);

	when(mockEvent.getEntity()).thenReturn(mockPlayer);
	when(mockEvent.getCause()).thenReturn(EntityDamageEvent.DamageCause.FALL);
	when(mockPlayer.getName()).thenReturn("TestPlayer");
	when(shoots.get("TestPlayer")).thenReturn(mockShoot);
	
	ShootManager shootManager = new ShootManager(mockPlugin, mockData, mock(WASD.class));

	shootManager.onPlayerFallDamage(mockEvent);
	verify(mockShoot).checkFallDamage(mockEvent);
    }

    @Test
    public void testOnCreatureSpawn() {
	CreatureSpawnEvent mockEvent = mock(CreatureSpawnEvent.class);
	Weapon mockWeapon = mock(Weapon.class);
	PlayerData mockData = mock(PlayerData.class);

	when(mockData.getWeapon()).thenReturn(mockWeapon);

	try (MockedConstruction<Shoot> mocked = mockConstruction(Shoot.class)) {
	    ShootManager shootManager = new ShootManager(mockPlugin, mockData, mock(WASD.class));

	    shootManager.onCreatureSpawn(mockEvent);

	    // Verify Shoot was constructed
	    assertEquals(1, mocked.constructed().size());

	    // Get the constructed Shoot instance
	    Shoot createdShoot = mocked.constructed().get(0);

	    // Verify method was called on it
	    verify(createdShoot).onCreatureSpawn(mockEvent);
	}

	
    }

    @Test
    public void testOnEggHit(){
	ProjectileHitEvent mockHit = mock(ProjectileHitEvent.class);
	Projectile mockProjectile = mock(Projectile.class);
	Shoot mockShoot = mock(Shoot.class);

	PlayerData mockData = mock(PlayerData.class);
	
	when(mockData.getPlayerShoots()).thenReturn(shoots);

	ShootManager shootManager = new ShootManager(mockPlugin, mockData, mock(WASD.class));

	when(mockHit.getEntity()).thenReturn(mockProjectile);
	when(mockProjectile.getShooter()).thenReturn(mockPlayer);
	when(mockPlayer.getName()).thenReturn("TestPlayer");
	when(shoots.get("TestPlayer")).thenReturn(mockShoot);

	shootManager.onEggHit(mockHit);

	verify(mockShoot).onEggHit(mockHit);
	
    }

    @Test
    public void testUpdateWeaponData(){
    Server mockServer = mock(Server.class);
    Shoot mockShoot = mock(Shoot.class);
    PlayerData mockData = mock(PlayerData.class);
    WASD mockWASD = mock(WASD.class);

    Collection<? extends Player> mockPlayers = Collections.singleton(mockPlayer);
    doReturn(mockPlayers).when(mockServer).getOnlinePlayers();

    
    when(mockData.getPlayerShoots()).thenReturn(shoots);
    when(mockPlugin.getServer()).thenReturn(mockServer);
    when(mockPlayer.getName()).thenReturn("TestPlayer");
    when(shoots.get("TestPlayer")).thenReturn(mockShoot);

    ShootManager shootManager = new ShootManager(mockPlugin, mockData, mockWASD);

    shootManager.updateWeaponData();

    verify(mockShoot).update();
    }
   
}
