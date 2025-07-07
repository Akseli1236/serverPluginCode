package org.server.gtamc;

<<<<<<< HEAD
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
=======
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79

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
<<<<<<< HEAD
import org.mockbukkit.mockbukkit.ServerMock;
=======
>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79



public class HoldingDetectionTest {

    Plugin mockPlugin = mock(Plugin.class);
    Player mockPlayer = mock(Player.class);
    ProtocolManager mockManager = mock(ProtocolManager.class);

<<<<<<< HEAD
    private ServerMock server;

    @BeforeEach
    public void setUp() {
        // Start the mock server
        //server =  MockBukkit.mock();
=======
    @BeforeEach
    public void setUp() {
        // Start the mock server
        MockBukkit.mock();
>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79
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
<<<<<<< HEAD
	MockBukkit.mock();
	PacketEvent mockEvent = mock(PacketEvent.class);
	Map<String, Shoot> mockShootMap = new HashMap<>();
	Shoot mockShootInstance = mock(Shoot.class);
=======
	PacketEvent mockEvent = mock(PacketEvent.class);
	Map<String, Shoot> mockShootMap = new HashMap<>();
>>>>>>> ae8c77c6322c0e0a0f5d3264eb193dbde7957d79
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
