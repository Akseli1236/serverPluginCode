package org.server.gtamc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;


public class HoldingDetection extends PacketAdapter {

    private final PlayerData playerData;
    private BukkitTask task = null;

    public HoldingDetection(Plugin plugin, ProtocolManager protocolManager, PlayerData playerData) {
        super(plugin, PacketType.Play.Client.ARM_ANIMATION);
        protocolManager.addPacketListener(this);
        this.playerData = playerData;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event == null) {
            return;
        }
        Player player = event.getPlayer(); // Get the player who sent the packet
	
	
        if (event.getPacketType().equals(PacketType.Play.Client.ARM_ANIMATION)) {
	    Shoot instance = playerData.getPlayerShoots().get(player.getName());
            instance.shootSemiAuto(player);

	    if (task != null){
		task.cancel();
		task = null;
	    }

	    task = Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            instance.setBreakingBlock(false); // Reset after short delay
        }, 2L); // 2 ticks = 0.1 seconds
        }
    }
}
