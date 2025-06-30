package org.Akseli.firstPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.PacketType;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class HoldingDetection extends PacketAdapter {

    public HoldingDetection(Plugin plugin, ProtocolManager protocolManager) {
        super(plugin, PacketType.Play.Client.ARM_ANIMATION);
        protocolManager.addPacketListener(this);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {

        Player player = event.getPlayer(); // Get the player who sent the packet
        if (event.getPacketType() == PacketType.Play.Client.ARM_ANIMATION) {
            Shoot instance = PlayerData.getPlayerShoots().get(player.getName());
            instance.shootSemiAuto(player);
        }

    }
}