package org.server.gtamc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;


public class AmmoManagerTest {

    private final AmmoManager mockManager = new AmmoManager();
    

    @Test
    public void testaddAmmoData() {
	final UUID uuid = UUID.randomUUID();

	mockManager.addAmmoData(uuid, 32);
	AmmoData ammoData = mockManager.getAmmoData(uuid);       
	assertEquals(32, ammoData.getAmmoCount());
	
	
    }


    @Test
    public void testReloadAmmo(){
	final UUID uuid = UUID.randomUUID();
	
	mockManager.addAmmoData(uuid, 32);
	mockManager.reloadAmmo(uuid, 16);
	AmmoData ammoData = mockManager.getAmmoData(uuid);
	assertEquals(16, ammoData.getAmmoCount());

    }

    @Test
    public void testIsOutOfAmmo(){
	final UUID uuid = UUID.randomUUID();

	mockManager.addAmmoData(uuid, 32);
	mockManager.reloadAmmo(uuid, 0);
	AmmoData ammoData = mockManager.getAmmoData(uuid);
	assertEquals(false, ammoData.isOutOfAmmo());
	assertEquals(false, mockManager.isOutOfAmmo(uuid));

	ammoData.setOutOfAmmo(true);

	assertEquals(true, ammoData.isOutOfAmmo());
	assertEquals(true, mockManager.isOutOfAmmo(uuid));
	
    }
}
