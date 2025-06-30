package org.server.gtamc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

public class ModifiedItemBehaviourTest {

    Plugin mockPlugin = mock(Plugin.class);
    Player mockPlayer = mock(Player.class);

    @BeforeEach
    public void setUp(){
	MockBukkit.mock();
    }

    @AfterEach
    public void tearDown(){
	MockBukkit.unmock();
    }
    
    
    @Test
    public void testExample() {
	ModifiedItemBehavior modifiedItemBehavior = new ModifiedItemBehavior(mockPlugin);
	ItemStack mockItem = mock(ItemStack.class);
	
	when(mockPlayer.getHealth()).thenReturn(10.0);
	modifiedItemBehavior.useBandage(mockPlayer, mockItem);
	verify(mockPlayer).setHealth(14.0);

    }
}
