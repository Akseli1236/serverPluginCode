package org.server.gtamc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AmmoManager {
    private final Map<UUID, AmmoData> bulletsLeft = new HashMap<>();

    // Add ammo data for a specific item
    public void addAmmoData(UUID itemUUID, int initialAmmo) {
        if (!bulletsLeft.containsKey(itemUUID)) {
            bulletsLeft.put(itemUUID, new AmmoData(initialAmmo));
        }
    }

    // Get ammo data for a specific item
    public AmmoData getAmmoData(UUID itemUUID) {
        return bulletsLeft.get(itemUUID);
    }

    // Decrease ammo count when shot
    public void useAmmo(UUID itemUUID) {
        AmmoData ammoData = bulletsLeft.get(itemUUID);
        if (ammoData != null && ammoData.getAmmoCount() > 0) {
            ammoData.setAmmoCount(ammoData.getAmmoCount() - 1);
        }
    }

    // Reload ammo for a specific item
    public void reloadAmmo(UUID itemUUID, int newAmmoCount) {
        AmmoData ammoData = bulletsLeft.get(itemUUID);
        if (ammoData != null) {
            ammoData.setAmmoCount(newAmmoCount);
            ammoData.setOutOfAmmo(false);
        }
    }

    // Check if an item is out of ammo
    public boolean isOutOfAmmo(UUID itemUUID) {
        AmmoData ammoData = bulletsLeft.get(itemUUID);
        return ammoData != null && ammoData.isOutOfAmmo();
    }
}
