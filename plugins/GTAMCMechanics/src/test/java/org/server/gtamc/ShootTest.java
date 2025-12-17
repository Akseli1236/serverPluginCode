package org.server.gtamc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockito.ArgumentCaptor;

public class ShootTest {

    private Plugin mockPlugin = mock(Plugin.class);
    private Player mockPlayer = mock(Player.class);
    private Shoot mockShoot;
    private Weapon mockWeapon = mock(Weapon.class);
    private ServerMock server;

    @BeforeEach
    void setUp() {
        mockShoot = new Shoot(mockPlugin, mockWeapon, null);
        server = MockBukkit.mock();
    }

    @AfterEach
    public void tearDown(){
        MockBukkit.unmock();
    }

    @Test
    public void testUpdate() {
        mockShoot.update();
        verify(mockWeapon).clearAll();
        verify(mockWeapon).readFile();
        verify(mockWeapon).weaponUpdate(mockPlugin);

        verifyNoMoreInteractions(mockWeapon);
    }

    @Test
    public void testSetInventoryOpen(){
        PlayerInventory mockInventory = mock(PlayerInventory.class);
        ItemStack mockItemStack = mock(ItemStack.class);

        when(mockPlayer.getInventory()).thenReturn(mockInventory);
        when(mockInventory.getItemInMainHand()).thenReturn(mockItemStack);

        when(mockItemStack.getType()).thenReturn(Material.ACACIA_LOG);

    }

    @Test
    public void testReturnUUID(){
        ItemMeta mockItemMeta = mock(ItemMeta.class);
        ItemStack mockItemStack = mock(ItemStack.class);
        PersistentDataContainer mockContainer = mock(PersistentDataContainer.class);

        when(mockItemStack.getItemMeta()).thenReturn(mockItemMeta);
        when(mockItemMeta.getPersistentDataContainer()).thenReturn(mockContainer);
        when(mockPlugin.getName()).thenReturn("TestPlugin");

        NamespacedKey key = new NamespacedKey(mockPlugin, "unique_id");
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";

        when(mockContainer.has(eq(key), eq(PersistentDataType.STRING))).thenReturn(true);
        when(mockContainer.get(eq(key), eq(PersistentDataType.STRING))).thenReturn(uuidString);

        UUID result = mockShoot.returnUUID(mockItemStack);

        assertEquals(UUID.fromString(uuidString), result);

    }

    @Test
    public void testSetInventoryAction(){
        InventoryAction action = InventoryAction.PICKUP_ALL; // or any enum value

        mockShoot.setInventoryAction(action);
        assertEquals(action, mockShoot.getInventoryAction());
    }

    @Test
    public void testSetItemSlot(){
        int itemSlot = 1;

        mockShoot.setItemSlot(itemSlot);
        assertEquals(itemSlot, mockShoot.getItemSlot());
    }

    @Test
    public void testSetOrGetUUID(){
        ItemMeta mockItemMeta = mock(ItemMeta.class);
        when(mockPlugin.getName()).thenReturn("Sammakko");
        PersistentDataContainer mockContainer = mock(PersistentDataContainer.class);
        NamespacedKey unique_id = new NamespacedKey(mockPlugin, "unique_id");
        ItemStack mockItemStack = mock(ItemStack.class);

        when(mockItemMeta.getPersistentDataContainer()).thenReturn(mockContainer);
        when(mockContainer.has(unique_id, PersistentDataType.STRING)).thenReturn(false);

        // To capture the string set in PersistentDataContainer
        final ArgumentCaptor<String> uuidCaptor = ArgumentCaptor.forClass(String.class);

        UUID resultUuid = mockShoot.setOrGetUUID(mockItemMeta, mockItemStack);

        verify(mockContainer).set(eq(unique_id), eq(PersistentDataType.STRING), uuidCaptor.capture());
        verify(mockItemStack).setItemMeta(mockItemMeta);

        assertEquals(UUID.fromString(uuidCaptor.getValue()), resultUuid);
    }

    @Test
    public void testRemoveEffects(){
        Player player = server.addPlayer();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1000, 1000));
        assertFalse(player.getActivePotionEffects().isEmpty());
        
        mockShoot.removeEffects(player);

        assertTrue(mockPlayer.getActivePotionEffects().isEmpty());

        MockBukkit.unmock();
    }

    @Test
    public void testLoadWeapon(){
        ItemStack mockStack = mock(ItemStack.class);
        Player player = server.addPlayer();
        ItemMeta mockMeta = mock(ItemMeta.class);
        //ItemStack item = new ItemStack(Material.DIAMOND_AXE);

        when(mockStack.getItemMeta()).thenReturn(mockMeta);
        when(mockStack.getType()).thenReturn(Material.DIAMOND_AXE);

        mockShoot.loadWeapons(mockStack, player);
    }
}
