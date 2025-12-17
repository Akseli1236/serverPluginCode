package org.server.gtamc;

import java.util.Objects;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

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
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);

        getLogger().info("Plugin has been disabled.");

        // Plugin shutdown logic
    }
}
