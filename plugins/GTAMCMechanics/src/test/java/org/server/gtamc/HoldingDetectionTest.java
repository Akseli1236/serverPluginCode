package org.server.gtamc;


import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;



public class HoldingDetectionTest {

    Plugin mockPlugin = mock(Plugin.class);
    Player mockPlayer = mock(Player.class);
    ProtocolManager mockManager = mock(ProtocolManager.class);

    @BeforeEach
    public void setUp() {
        // Start the mock server
        MockBukkit.mock();
        // Load your plugin
    }
    @AfterEach
    public void tearDown() {
        // Start the mock server
        MockBukkit.unmock();;
        // Load your plugin
    }

    @Test
    public void testPacketReveiving() {

	PacketEvent mockEvent = mock(PacketEvent.class);
	Map<String, Shoot> mockShootMap = new HashMap<>();
	PacketType mockPacketType = mock(PacketType.class);
	PlayerData mockData = mock(PlayerData.class);  //new PlayerData(mockPlugin, new WASD(mockPlugin, mock(PacketType.class)));
					     
	when(mockEvent.getPlayer()).thenReturn(mockPlayer);
	 
	//when(mockPacketType.equals(PacketType.Play.Client.ARM_ANIMATION)).thenReturn(true);
	when(mockEvent.getPacketType()).thenReturn(mockPacketType);
	when(mockPlayer.getName()).thenReturn("Hessu");
	when(mockData.getPlayerShoots()).thenReturn(mockShootMap);
	
	
	//	var holdingDetection = new HoldingDetection(mockPlugin, mockManager, mockData);
	//	holdingDetection.onPacketReceiving(mockEvent);
	//	verify(mockShootInstance).shootSemiAuto(mockPlayer);
	
    }
    
}
