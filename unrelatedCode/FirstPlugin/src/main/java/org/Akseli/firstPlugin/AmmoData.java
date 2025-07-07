package org.Akseli.firstPlugin;

import java.util.UUID;

public class AmmoData {
    private int ammoCount;
    private boolean isReloading;
    private boolean isOutOfAmmo;

    public AmmoData(int ammoCount) {
        this.ammoCount = ammoCount;
        this.isReloading = false;
        this.isOutOfAmmo = false;
    }

    public int getAmmoCount() {
        return ammoCount;
    }

    public void setAmmoCount(int ammoCount) {
        this.ammoCount = ammoCount;
    }

    public boolean isReloading() {
        return isReloading;
    }

    public void setReloading(boolean reloading) {
        isReloading = reloading;
    }

    public boolean isOutOfAmmo() {
        return isOutOfAmmo;
    }

    public void setOutOfAmmo(boolean outOfAmmo) {
        isOutOfAmmo = outOfAmmo;
    }
}
