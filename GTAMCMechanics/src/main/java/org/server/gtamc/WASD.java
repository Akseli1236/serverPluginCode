package org.server.gtamc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class WASD extends PacketAdapter {

    public WASD(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    private final Map<String, Car> playerCarMap = new HashMap<>();
    private final Map<String, Integer> movementMap = new HashMap<>();


    public Map<String, Integer> getMovements(){
        return movementMap;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if(event.getPacketType().equals(PacketType.Play.Client.STEER_VEHICLE)){
            Object ppisv = event.getPacket().getHandle();


            //Usage ppisv
            String packetString = ppisv.toString();
            String inputString = packetString.substring(packetString.indexOf("Input[") + 6, packetString.indexOf("]"));

            String[] inputs = inputString.split(", ");

            // Loop through the input values and parse them
            for (String input : inputs) {
                // Split each value by "="
                String[] parts = input.split("=");

                // Extract the movement type (e.g., "forward", "backward") and its boolean value
                if (parts.length == 2) {
                    String movementType = parts[0].trim();  // "forward", "backward", etc.
                    int isActive = parts[1].trim().equals("true") ? 1 : 0;  // "false" or "true"

                    // Put the result into the map
                    movementMap.put(movementType, isActive);
                }
            }

            if (playerCarMap.containsKey(event.getPlayer().getName())) {
                Car instance = playerCarMap.get(event.getPlayer().getName());
                instance.moveCar(event, movementMap);
            }else {
                Car instance = new Car(plugin);
                playerCarMap.put(event.getPlayer().getName(), instance);
                instance.moveCar(event, movementMap);
            }



        }
    }

}
