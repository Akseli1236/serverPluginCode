package org.Akseli.firstPlugin;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.wargamer2010.signshop.SignShop;
import org.wargamer2010.signshop.events.SSCreatedEvent;
import org.wargamer2010.signshop.events.SSPostTransactionEvent;
import org.wargamer2010.signshop.events.SSPreTransactionEvent;
import org.wargamer2010.signshop.player.PlayerCache;
import org.wargamer2010.signshop.player.SignShopPlayer;

import java.util.*;

public class ShootManager implements Listener {

    private final Plugin plugin;
    private final Map<String, Shoot> shoots = PlayerData.getPlayerShoots();
    private static final Map<UUID, List<Object>> bulletsLeft = new HashMap<>();

    public ShootManager(Plugin plugin)
    {
        this.plugin = plugin;
    }

    public static Map<UUID, List<Object>> getBulletsLeft() {
        return bulletsLeft;
    }


    @EventHandler
    public void onExplosionEvent(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            // Prevent block destruction
            event.blockList().clear(); // Clears the list of affected blocks
        }
    }

    @EventHandler
    public void onItemSwitch(PlayerItemHeldEvent event){
        Shoot instance = shoots.get(event.getPlayer().getName());
        instance.onItemSwitch(event);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Shoot instance = shoots.get(event.getPlayer().getName());
        instance.onPlayerDropItem(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {

        Shoot instance = shoots.get(event.getPlayer().getName());
        instance.onPlayerInteract(event);
    }

    @EventHandler
    public void onPlayerPickItemEvent(EntityPickupItemEvent event) {
        if (shoots.containsKey(event.getEntity().getName())) {
            Shoot instance = shoots.get(event.getEntity().getName());
            instance.onPlayerItemPickup(event);
        }


    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Shoot instance = shoots.get(event.getEntity().getName());
        instance.onPlayerDeath(event);
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Shoot instance = shoots.get(event.getPlayer().getName());
        instance.onPlayerRespawn(event);
    }
    @EventHandler
    public void onPlayerDropFromInventory(InventoryOpenEvent event) {
        Shoot shoot = shoots.get(event.getPlayer().getName());
        shoot.setInventoryOpen(true);


    }

    public void resetItemMeta(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return; // Avoid null or AIR items
        }

        // Create a new ItemStack with the same type and amount
        ItemStack newItem = new ItemStack(item.getType(), item.getAmount());

        // If the original item had durability or damage (for older versions or damageable items), preserve it
        if (item.getItemMeta() instanceof Damageable originalDamageable) {
            ItemMeta newMeta = newItem.getItemMeta();
            if (newMeta instanceof Damageable newDamageable) {
                newDamageable.setDamage(originalDamageable.getDamage());
                newItem.setItemMeta(newMeta);
            }
        }

        // Replace the original item with the new one
        item.setType(newItem.getType());
        item.setAmount(newItem.getAmount());
        item.setItemMeta(newItem.getItemMeta());
    }
    @EventHandler
    public void onShotCreate(SSCreatedEvent event){
        ItemStack[] items = event.getItems();
        for (ItemStack item : items) {
            resetItemMeta(item);
        }
    }

    @EventHandler
    public void onSignShopTransaction(SSPreTransactionEvent event) {
        ItemStack[] items = event.getItems();
        ItemStack[] inventory = event.getPlayer().getInventoryContents();

        SignShopPlayer ssPlayer = PlayerCache.getPlayer(event.getPlayer().getPlayer());
        String itemName = Objects.requireNonNull(items[0].getItemMeta()).getDisplayName();
        for (ItemStack shopItem : items) {
            // Reset the metadata of the shop item to only compare by type
            resetItemMeta(shopItem);

            // Iterate through the player's inventory and normalize items for comparison
            for (ItemStack playerItem : inventory) {
                if (playerItem == null || playerItem.getType() != shopItem.getType()) {
                    continue; // Skip null items or items that don't match the type
                }

                resetItemMeta(playerItem); // Reset the player's item metadata

                // Check if the normalized items match
                if (shopItem.isSimilar(playerItem) && !event.getRequirementsOK()) {
                    // Perform your logic here, such as allowing the transaction
                    ssPlayer.sendMessage(SignShop.getInstance().getSignShopConfig().getError("player_verify_transaction", null));
                    return;
                }
            }
        }
        if (!event.getRequirementsOK()) {
            ssPlayer.sendMessage(SignShop.getInstance().getSignShopConfig().getError("player_doesnt_have_items_own", event.getMessageParts()));
        }
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework) {
            event.setCancelled(true); // Cancel damage from the firework
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Shoot shoot = shoots.get(event.getWhoClicked().getName());
        shoot.setInventoryOpen(true);
        shoot.setInventoryAction(event.getAction());
        shoot.setItemSlot(event.getSlot());
        shoot.removeEffects(event.getWhoClicked());
        if (event.getCurrentItem() != null){
            shoot.loadWeapons(event.getCurrentItem(), (Player) event.getWhoClicked());
        }



    }
    @EventHandler
    public void onPlayerCloseInventory(InventoryCloseEvent event) {
        if (PlayerData.getPlayerShoots().containsKey(event.getPlayer().getName())) {
            Shoot shoot = shoots.get(event.getPlayer().getName());
            shoot.setInventoryOpen(false);

            Player player = (Player) event.getPlayer();
            shoot.loadWeapons(player.getInventory().getItemInMainHand(), player);

        }
    }

    @EventHandler
    public void onPlayerFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Shoot instance = shoots.get(player.getName());
            instance.checkFallDamage(event);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {

        Shoot instance = new Shoot(plugin);
        instance.onCreatureSpawn(event);

    }

    @EventHandler
    public void onEntitySpawnEvent(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Projectile projectile) {
            // Delay the execution for 1 tick to ensure shooter is updated
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (projectile.getShooter() instanceof Player player) {
                    Shoot instance = shoots.get(player.getName());
                    instance.onEntitySpawnEvent(event);
                }
            }, 1L); // 1L is 1 tick (50ms)
        }
    }

    @EventHandler
    public void onEggHit(ProjectileHitEvent event) {
        event.getEntity();
        if (event.getEntity().getShooter() instanceof Player player) {
            Shoot instance = shoots.get(player.getName());
            instance.onEggHit(event);
        }

    }
    public void updateWeaponData(){
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Shoot shoot = shoots.get(player.getName());
            shoot.update();
        }
    }


}
