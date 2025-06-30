package org.server.gtamc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;

import org.bukkit.plugin.java.JavaPlugin;
import com.comphenix.protocol.ProtocolLibrary;

import java.util.Objects;

public final class GTAMC extends JavaPlugin {

    @Override
    public void onEnable() {
        WASD wasd = new WASD(this, PacketType.Play.Client.STEER_VEHICLE);
        PlayerData playerData = new PlayerData(this, wasd);
        UsefulCommands usefulCommands = new UsefulCommands(this, playerData, wasd);

        getServer().getPluginManager().registerEvents(playerData, this);
        getServer().getPluginManager().registerEvents(new ShootManager(this, playerData, wasd), this);
        getServer().getPluginManager().registerEvents(new Car(this), this);
        //getServer().getPluginManager().registerEvents(new HoldingDetection(this), this);
        Objects.requireNonNull(this.getCommand("itempickup")).setExecutor(usefulCommands);
        Objects.requireNonNull(this.getCommand("rweapons")).setExecutor(usefulCommands);

	

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(wasd);
        // Register the holding detection listener
        new HoldingDetection(this, protocolManager, playerData); // Pass 'this' to the constructor of HoldingDetection

    }

    @Override
    public void onDisable() {
//        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        System.out.println("Something went wrong...");
        // Plugin shutdown logic
    }
}
