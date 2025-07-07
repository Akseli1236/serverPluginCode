package org.Akseli.firstPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;

import org.bukkit.plugin.java.JavaPlugin;
import com.comphenix.protocol.ProtocolLibrary;


public final class FirstPlugin extends JavaPlugin {


    @Override
    public void onEnable() {
        ArrowCount arrowCount = new ArrowCount(this);
        TeleportTo teleportTo = new TeleportTo();
        UsefulCommands usefulCommands = new UsefulCommands(this);
        getServer().getPluginManager().registerEvents(new PlayerData(this), this);
        getServer().getPluginManager().registerEvents(new RespawnAnimalOnDeath(this), this);
        getServer().getPluginManager().registerEvents(arrowCount, this);
        getServer().getPluginManager().registerEvents(new ShootManager(this), this);
        getServer().getPluginManager().registerEvents(new Car(this), this);
        //getServer().getPluginManager().registerEvents(new HoldingDetection(this), this);
        this.getCommand("tpback").setExecutor(teleportTo);
        this.getCommand("lastpos").setExecutor(teleportTo);
        this.getCommand("location").setExecutor(usefulCommands);
        this.getCommand("tp").setExecutor(teleportTo);
        this.getCommand("arrows").setExecutor(arrowCount);
        this.getCommand("repair").setExecutor(arrowCount);
        this.getCommand("arrowspread").setExecutor(arrowCount);
        this.getCommand("itempickup").setExecutor(usefulCommands);
        this.getCommand("rweapons").setExecutor(usefulCommands);



        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new WASD(this, PacketType.Play.Client.STEER_VEHICLE));
        // Register the holding detection listener
        new HoldingDetection(this, protocolManager); // Pass 'this' to the constructor of HoldingDetection

    }


    @Override
    public void onDisable() {
//        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        System.out.println("Something went wrong...");
        // Plugin shutdown logic
    }
}
