package org.Akseli.firstPlugin;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;


import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.protection.flags.Flags;


public class Shoot implements Listener {

    private final Plugin plugin;
    private final Weapon weapon;

    private BukkitTask currentTask = null;
    private BukkitTask reloadTaskWeapon = null;
    private BukkitTask reloadTaskTool = null;
    private BukkitTask shootTask = null;
    private BukkitTask controlFire = null;
    private BukkitTask zoomScheduler = null;


    private Double spread;
    private double damage = 5;
    private Integer magSize;
    private Integer reloadTime;
    private Integer Projectile_Speed;
    private Integer ammoPerReload;
    private Integer fireRate;
    private Integer projectilesPerShot = 1;
    private Double zoomAmount;
    private Double bonusDamage;
    private double armorDamage;
    private Integer itemSlot;

    private String Projectile_Type;
    private String weapon_Type;
    private String shootingType;

    private boolean InventoryOpen = false;
    private boolean dontShoot = false;
    private boolean canShoot = false;
    private boolean damageEvent = false;
    private boolean zoomOn = false;
    private boolean wasDrop = false;
    private boolean outOfAmmoCooldown = false;
    private boolean fromGround = false;
    private boolean resetNextShotCooldown = false;
    private boolean flaming = false;

    private InventoryAction invetoryActions;

    // List contains these in order: Bullets, isReloading, shotCooldown, secondaryFire, secondaryAmmo
    private final Map<UUID, List<Object>> bulletsLeft = ShootManager.getBulletsLeft();
    private final Map<Double, Double> damageDrop = new HashMap<>();
    private final Map<String, List<String>> weaponSounds = new HashMap<>();


    public Shoot(Plugin plugin)
    {
        weapon = PlayerData.getWeapon();
        this.plugin = plugin;
    }

    public void update(){

        weapon.weaponUpdate(plugin);
    }

    public void setInventoryOpen(boolean state){
        InventoryOpen = state;
    }

    public void setInventoryAction(InventoryAction state){
        invetoryActions = state;
    }

    public void setItemSlot(int slot){
        itemSlot = slot;
    }

    private UUID setOrGetUUID(ItemMeta itemMeta, ItemStack ItemInHand){
        UUID uuid = UUID.randomUUID();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey unique_id = new NamespacedKey(plugin, "unique_id");

        // Check if the UUID already exists in the PersistentDataContainer
        if (!container.has(unique_id, PersistentDataType.STRING)) {
            // UUID does not exist, so generate and set it
            container.set(unique_id, PersistentDataType.STRING, uuid.toString());
            ItemInHand.setItemMeta(itemMeta);
        } else {
            // UUID already exists, retrieve and print it for debugging
            uuid = UUID.fromString(container.get(unique_id, PersistentDataType.STRING));
        }


        return uuid;
    }

    private void cancelZoom(Player player){
        if (zoomScheduler != null && !zoomScheduler.isCancelled()){
            zoomScheduler.cancel();
            zoomScheduler = null;
            zoomOn = false;

            if (!player.getActivePotionEffects().isEmpty()) {
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
            }

        }
    }

    public void removeEffects(HumanEntity entity){
        Player player = (Player) entity;
        cancelZoom(player);
    }

    private List<Object> processMeta(WeaponClass value, ItemMeta itemMeta){
        List<String> lore = new ArrayList<>();
        List<Object> loreItems = new ArrayList<>();
        int iterator = 0;
        for (String loreItem :  value.getRoot().getInfo().getWeapon_item().getLore()){

            int index = loreItem.indexOf(">");
            String itemName = loreItem.substring(index + 1);
            ChatColor color = ChatColor.valueOf(loreItem.substring(1, index).toUpperCase());
            lore.add(color + itemName);
            if (iterator == 0){
                shootingType = itemName.split(" ")[1];
            }
            iterator++;
        }
        itemMeta.setLore(lore);


        String name = value.getRoot().getInfo().getWeapon_item().getName();
        int index = name.indexOf(">");
        loreItems.add(name.substring(index + 1));
        loreItems.add(ChatColor.valueOf(name.substring(1, index).toUpperCase()));
        return loreItems;

    }


    private void fillBullets(UUID finalUuid){
        if (bulletsLeft.get(finalUuid) == null) {
            bulletsLeft.put(finalUuid, new ArrayList<>());
            bulletsLeft.get(finalUuid).add(magSize);
            bulletsLeft.get(finalUuid).add(false);
            bulletsLeft.get(finalUuid).add(false);
            bulletsLeft.get(finalUuid).add(false);
            bulletsLeft.get(finalUuid).add(1);
        } else if (resetNextShotCooldown)
            bulletsLeft.get(finalUuid).set(2, false);
    }

    public void loadWeapons(ItemStack ItemInHand, Player player){


        ItemMeta itemMeta = ItemInHand.getItemMeta();

        if (itemMeta != null && ItemInHand.getType() != Material.AIR) {
            // Get the existing lore or create a new one if it's null

            weapon.getWeapons().forEach((key, value) -> {
                if (value.getRoot().getInfo().getWeapon_item().getType().equalsIgnoreCase(ItemInHand.getType().toString())) {
                    UUID finalUuid = setOrGetUUID(itemMeta, ItemInHand);
                    if (value.getRoot().getInfo().getWeapon_item().isUnbreakable()){
                        itemMeta.setUnbreakable(true);
                    }
                    if (value.getRoot().getInfo().getDual_wield() != null){
                        if (value.getRoot().getInfo().getDual_wield().isUnbreakable()){
                            itemMeta.setUnbreakable(true);
                        }
                    }

                    List<Object> loreItems = processMeta(value, itemMeta);
                    String itemName = (String) loreItems.getFirst();
                    ChatColor color = (ChatColor) loreItems.get(1);


                    itemMeta.setDisplayName(color + itemName);
                    damage = value.getRoot().getDamage().getBase_damage();
                    reloadTime = value.getRoot().getReload().getReload_duration();
                    magSize = value.getRoot().getReload().getMagazine_size();

                    Projectile_Type = null;
                    if (value.getRoot().getReload().getAmmo().getAmmos() != null){
                        Projectile_Type = value.getRoot().getReload().getAmmo().getAmmos().getFirst();
                    }

                    weapon_Type = value.getRoot().getProjectile().split("\\.")[0];
                    projectilesPerShot = value.getRoot().getShoot().getProjectiles_per_shot();
                    fireRate = value.getRoot().getShoot().getDelay_between_shots();
                    ammoPerReload = value.getRoot().getReload().getAmmo_per_reload();
                    armorDamage = value.getRoot().getDamage().getArmor_damage();

                    zoomAmount = 0.0;
                    bonusDamage = 0.0;
                    if (value.getRoot().getDamage().getHead() != null){
                        bonusDamage = value.getRoot().getDamage().getHead().getBonus_damage();
                    }

                    if (value.getRoot().getScope() != null){
                        zoomAmount = value.getRoot().getScope().getZoom_amount();
                    }

                    if (value.getRoot().getShoot().getFully_automatic_shots_per_second() > 0) {
                        fireRate = Math.floorDiv(20, value.getRoot().getShoot().getFully_automatic_shots_per_second());
                    }

                    flaming = value.getRoot().getDamage().getFlaming();

                    resetNextShotCooldown = true;
                    fillBullets(finalUuid);

                    if (value.getRoot().getDamage().getDropoff() != null) {
                        List<String> dropoff = value.getRoot().getDamage().getDropoff();
                        for (String dropoffValue : dropoff) {
                            String[] DistAndVal = dropoffValue.split(" ");
                            damageDrop.put(Double.parseDouble(DistAndVal[0]), Double.parseDouble(DistAndVal[1]));
                        }
                    } else {
                        damageDrop.clear();
                    }

                    Projectile_Speed = value.getRoot().getShoot().getProjectile_speed();
                    if (value.getRoot().getShoot().getSpread() != null){
                        spread = value.getRoot().getShoot().getSpread().getBase_spread();
                    }else {
                        spread = 0.0;
                    }

                    weaponSounds.put("Shoot_Mechanics", value.getRoot().getShoot().getMechanics());
                    weaponSounds.put("Weapon_Get_Mechanics", new ArrayList<>());
                    weaponSounds.get("Weapon_Get_Mechanics").add(value.getRoot().getInfo().getWeapon_get_mechanics());
                    weaponSounds.put("Out_Of_Ammo_Mechanics", value.getRoot().getReload().getAmmo().getOutOf_ammo_mechanics());
                    weaponSounds.put("Start_Mechanics", value.getRoot().getReload().getStart_mechanics());
                    weaponSounds.put("Finish_Mechanics", value.getRoot().getReload().getFinish_mechanics());
                    weaponSounds.put("Open_Mechanics", value.getRoot().getFirearm_action().getOpen().getMechanics());
                    weaponSounds.put("Close_Mechanics", value.getRoot().getFirearm_action().getClose().getMechanics());

                    if (!InventoryOpen && !fromGround){
                        playWeaponSound(player, "Weapon_Get_Mechanics");
                        if ((boolean) bulletsLeft.get(finalUuid).get(3) && ItemInHand.getType().toString().equalsIgnoreCase("Diamond_pickaxe")) {
                            int secondaryAmmo = (int) bulletsLeft.get(finalUuid).get(4);
                            renameItem(value.getRoot().getInfo().getWeapon_item().getName(), secondaryAmmo, player);
                        }else {
                            renameItem(value.getRoot().getInfo().getWeapon_item().getName(), Integer.parseInt(bulletsLeft.get(finalUuid).getFirst().toString()), player);
                        }
                    }
                    fromGround = false;




                }
            });

            weapon.getTools().forEach((toolName, tool) -> {
                if (tool.getRoot().getInfo().getWeapon_item().getType().equalsIgnoreCase(ItemInHand.getType().toString())) {
                    UUID finalUuid = setOrGetUUID(itemMeta, ItemInHand);
                    if (tool.getRoot().getInfo().getWeapon_item().isUnbreakable()){
                        itemMeta.setUnbreakable(true);
                    }
                    List<Object> loreItems = processMeta(tool, itemMeta);
                    String itemName = (String) loreItems.getFirst();
                    ChatColor color = (ChatColor) loreItems.get(1);

                    reloadTime = tool.getRoot().getReload().getReload_duration();
                    magSize = tool.getRoot().getReload().getMagazine_size();
                    fireRate = tool.getRoot().getShoot().getDelay_between_shots();
                    itemMeta.setDisplayName(color + itemName);

                    resetNextShotCooldown = false;
                    fillBullets(finalUuid);

                    weaponSounds.put("Shoot_Mechanics", tool.getRoot().getShoot().getMechanics());
                    weaponSounds.put("Weapon_Get_Mechanics", new ArrayList<>());
                    weaponSounds.get("Weapon_Get_Mechanics").add(tool.getRoot().getInfo().getWeapon_get_mechanics());
                    weaponSounds.put("Start_Mechanics", tool.getRoot().getReload().getStart_mechanics());
                    weaponSounds.put("Finish_Mechanics", tool.getRoot().getReload().getFinish_mechanics());
                    weapon_Type = tool.getRoot().getProjectile();

                    if (!InventoryOpen && !fromGround){
                        playWeaponSound(player, "Weapon_Get_Mechanics");
                        renameItem(tool.getRoot().getInfo().getWeapon_item().getName(), Integer.parseInt(bulletsLeft.get(finalUuid).getFirst().toString()), player);
                    }
                    fromGround = false;
                }

            });

            ItemInHand.setItemMeta(itemMeta);
        }

    }

    public void onPlayerItemPickup(EntityPickupItemEvent event) {
        Player player = (Player) event.getEntity();
        Item item = event.getItem();
        item.getItemStack();
        fromGround = true;
        loadWeapons(item.getItemStack(), player);
        loadWeapons(player.getInventory().getItemInMainHand(), player);

    }

    public void onItemSwitch(PlayerItemHeldEvent event) {


        if (controlFire != null && !controlFire.isCancelled()){
            controlFire.cancel();
            controlFire = null;
        }
        Player player = event.getPlayer();
        //player = event.getPlayer();
        int newSlot = event.getNewSlot(); // Slot the player is switching to
        int lastSlot = event.getPreviousSlot();
        ItemStack PrevItemInHand = player.getInventory().getItem(lastSlot);
        ItemStack ItemInHand = player.getInventory().getItem(newSlot);

        if (ItemInHand != null && ItemInHand.getType() != Material.AIR) {
            loadWeapons(ItemInHand, player);
        }

        if (PrevItemInHand != null && PrevItemInHand.getType() != Material.AIR) {


            UUID finalUuid = returnUUID(PrevItemInHand);
            weapon.getWeapons().forEach((key, value) -> {
                stopReload(value, finalUuid, PrevItemInHand, player);
            });
            weapon.getTools().forEach((key, value) -> {
                stopReload(value, finalUuid, PrevItemInHand, player);
            });

            cancelZoom(player);
        }

        // Additional logic for handling the switch can be added here
    }

    public void stopReload(WeaponClass value, UUID finalUuid, ItemStack PrevItemInHand, Player player){
        if (value.getRoot().getInfo().getWeapon_item().getType().equalsIgnoreCase(PrevItemInHand.getType().toString())){
            if (bulletsLeft.get(finalUuid) == null) {
                loadWeapons(PrevItemInHand, player);
            }
            boolean isReloading = (boolean) bulletsLeft.get(finalUuid).get(1);
            if (reloadTaskWeapon != null && !reloadTaskWeapon.isCancelled() && isReloading){
                reloadTaskWeapon.cancel();
                reloadTaskWeapon = null;
                player.sendMessage(ChatColor.DARK_AQUA + "Reload canceled!");
                bulletsLeft.get(finalUuid).set(1, false);
            }
            else if (reloadTaskTool != null && !reloadTaskTool.isCancelled() && isReloading){
                reloadTaskTool.cancel();
                reloadTaskTool = null;
                player.sendMessage(ChatColor.DARK_AQUA + "Reload canceled!");
                bulletsLeft.get(finalUuid).set(1, false);
            }
        }
    }

    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        dontShoot = true;
        wasDrop = true;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            dontShoot = false;
            wasDrop = false;
        },2);
        UUID finalUuid = returnUUID(item);


        weapon.getWeapons().forEach((key, value) -> {
            reloadCheck(value, event, player, finalUuid, item);
        });
        weapon.getTools().forEach((key, value) -> {
            reloadCheck(value, event, player, finalUuid, item);
        });




    }

    public void reloadCheck(WeaponClass value, PlayerDropItemEvent event, Player player, UUID finalUuid, ItemStack item) {
        if (value.getRoot().getInfo().getWeapon_item().getType().equalsIgnoreCase(item.getType().toString())) {
            if (!InventoryOpen) {
                event.setCancelled(true);

                if (!(boolean) bulletsLeft.get(finalUuid).get(3)){
                    renameItem(value.getRoot().getInfo().getWeapon_item().getName(), Integer.parseInt(bulletsLeft.get(finalUuid).getFirst().toString()), player);
                }else if (item.getType().toString().equalsIgnoreCase("Diamond_Pickaxe")) {
                    int secondaryAmmo = (int) bulletsLeft.get(finalUuid).get(4);
                    renameItem(value.getRoot().getInfo().getWeapon_item().getName(), secondaryAmmo, player);
                }

                boolean isReloading = (Boolean) bulletsLeft.get(finalUuid).get(1);
                if (!isReloading && ((Integer) bulletsLeft.get(finalUuid).getFirst() < magSize) || (int) bulletsLeft.get(finalUuid).get(4) < 1) {
                    try {
                        if ((boolean) bulletsLeft.get(finalUuid).get(3)){
                            reloadGrenadeLauncher(player, finalUuid, value.getRoot().getInfo().getWeapon_item().getName());
                        }
                        else if (weapon_Type != null && Projectile_Type != null){
                            reloadWeapon(value, player, finalUuid);
                        }else{
                            reloadTool(value, player, finalUuid);
                        }


                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (invetoryActions == InventoryAction.DROP_ONE_SLOT || invetoryActions == InventoryAction.DROP_ALL_SLOT) {
                ItemStack droppedItem = event.getItemDrop().getItemStack();
                event.getItemDrop().remove();
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.getInventory().setItem(itemSlot, droppedItem);
                }, 1L);

            } else{
                if (reloadTaskTool != null && !reloadTaskTool.isCancelled()){
                    reloadTaskTool.cancel();
                    reloadTaskTool = null;
                    bulletsLeft.get(finalUuid).set(1, false);
                }else if (reloadTaskWeapon != null && !reloadTaskWeapon.isCancelled()){
                    reloadTaskWeapon.cancel();
                    reloadTaskWeapon = null;
                    bulletsLeft.get(finalUuid).set(1, false);
                }
            }

        }
    }

    private boolean isHoe(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type == Material.WOODEN_HOE || type == Material.STONE_HOE ||
                type == Material.IRON_HOE || type == Material.GOLDEN_HOE ||
                type == Material.DIAMOND_HOE || type == Material.NETHERITE_HOE;
    }

    private boolean isShovel(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type == Material.WOODEN_SHOVEL || type == Material.STONE_SHOVEL ||
                type == Material.IRON_SHOVEL || type == Material.GOLDEN_SHOVEL ||
                type == Material.DIAMOND_SHOVEL || type == Material.NETHERITE_SHOVEL;
    }

    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Prevent all items from dropping
        event.setKeepInventory(true);

        // Get the list of items that are going to drop
        List<ItemStack> drops = event.getDrops();

        // Loop through the drops and remove the ones you want to keep
        Iterator<ItemStack> iterator = drops.iterator();
        while (iterator.hasNext()) {
            ItemStack item = iterator.next();

            // Check if the item should be kept (based on item type, name, etc.)
            weapon.getWeapons().forEach((key, value) -> {
                if (item.getType().toString().equalsIgnoreCase(value.getRoot().getInfo().getWeapon_item().getType()) &&
                        value.getRoot().getInfo().getWeapon_item().getLore().stream().anyMatch(line -> line.contains("Tier 5") || line.contains("Special"))) {
                    // Keep this item, so remove it from the drops list
                    iterator.remove();
                }
            });
            weapon.getTools().forEach((key, value) -> {
                if (item.getType().toString().equalsIgnoreCase(value.getRoot().getInfo().getWeapon_item().getType())) {
                    // Keep this item, so remove it from the drops list
                    iterator.remove();
                }
            });


        }

    }

    public void checkFallDamage(EntityDamageEvent event){
        Player player = (Player) event.getEntity();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        weapon.getTools().forEach((key, value) -> {
            if (itemInHand.getType().toString().equalsIgnoreCase(value.getRoot().getInfo().getWeapon_item().getType())) {
                event.setCancelled(true);
            }
        });
        if (itemInHand.getType().toString().equalsIgnoreCase("diamond_shovel")){
            event.setCancelled(true);
        }
    }

    public void onPlayerRespawn(PlayerRespawnEvent event) {
        AtomicBoolean found = new AtomicBoolean(false);
        List<ItemStack> itemsToRemove = new ArrayList<>();
        Player player = event.getPlayer();
        player.getInventory().forEach(key -> {
            if (key != null && key.getType() != Material.AIR){
                weapon.getWeapons().forEach((k, v) ->{
                    if (key.getType().toString().equalsIgnoreCase(v.getRoot().getInfo().getWeapon_item().getType()) &&
                            v.getRoot().getInfo().getWeapon_item().getLore().stream().anyMatch(line -> line.contains("Tier 5") || line.contains("Special"))){
                        found.set(true);
                    }
                });
                weapon.getTools().forEach((k, v) ->{
                    if (key.getType().toString().equalsIgnoreCase(v.getRoot().getInfo().getWeapon_item().getType())){
                        found.set(true);
                    }
                });
                if (!found.get()) {
                    itemsToRemove.add(key);
                }
                found.set(false);
            }
        });

        player.getInventory().setArmorContents(new ItemStack[4]);

        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem.getType() != Material.AIR) {
            weapon.getWeapons().forEach((key, value) -> {
                if (offHandItem.getType().toString().equalsIgnoreCase(value.getRoot().getInfo().getWeapon_item().getType()) &&
                        value.getRoot().getInfo().getWeapon_item().getLore().stream().anyMatch(line -> line.contains("Tier 5"))){
                    found.set(true);
                }
            });
            if (!found.get()) {
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            }
            found.set(false);

        }

        itemsToRemove.forEach(item -> event.getPlayer().getInventory().remove(item));
    }


    public void handleShooting(Player player){
        if (!isFlagAllowed(player, Flags.PVP)){
            return;
        }
        ItemStack ItemInHand = player.getInventory().getItemInMainHand();
        UUID finalUuid = returnUUID(ItemInHand);


        weapon.getWeapons().forEach((key, value) -> {
            if (ItemInHand.getType().toString().equalsIgnoreCase(value.getRoot().getInfo().getWeapon_item().getType())) {

                int val = Integer.parseInt(bulletsLeft.get(finalUuid).getFirst().toString());

                boolean isReloading = (Boolean) bulletsLeft.get(finalUuid).get(1);
                boolean shot = (Boolean) bulletsLeft.get(finalUuid).get(2);

                if (val > 0 && !shot && !isReloading) {
                    bulletsLeft.get(finalUuid).set(2, true);
                    bulletsLeft.get(finalUuid).set(0, val - 1);
                    canShoot = true;
                    fireRifle(value, player);
                    shootTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        bulletsLeft.get(finalUuid).set(2, false);
                    }, fireRate);

                }
                startReload(finalUuid, player, isReloading, value);
            }
        });

        weapon.getTools().forEach((key, value) -> {
            if (ItemInHand.getType().toString().equalsIgnoreCase(value.getRoot().getInfo().getWeapon_item().getType())) {
                int val = Integer.parseInt(bulletsLeft.get(finalUuid).getFirst().toString());

                boolean isReloading = (Boolean) bulletsLeft.get(finalUuid).get(1);
                boolean shot = (Boolean) bulletsLeft.get(finalUuid).get(2);

                if (val > 0 && !shot && !isReloading) {
                    bulletsLeft.get(finalUuid).set(2, true);
                    bulletsLeft.get(finalUuid).set(0, val - 1);
                    canShoot = true;
                    useTool(value, player);
                    shootTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        bulletsLeft.get(finalUuid).set(2, false);
                    }, fireRate);

                }
                startReload(finalUuid, player, isReloading, value);
            }
        });
    }

    public void startReload(UUID finalUuid, Player player, boolean isReloading, WeaponClass value){
        if (Integer.parseInt(bulletsLeft.get(finalUuid).getFirst().toString()) <= 0 && !isReloading) {
            // Begin reloading
            try {
                if (weapon_Type != null && Projectile_Type != null) {
                    reloadWeapon(value, player, finalUuid);
                }else{
                    reloadTool(value, player, finalUuid);
                }

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void reloadGrenadeLauncher(Player player, UUID finalUuid, String weapon_name){
        boolean isReloading = (Boolean) bulletsLeft.get(finalUuid).get(1);
        AtomicInteger secondaryAmmo = new AtomicInteger((int) bulletsLeft.get(finalUuid).get(4));
        if (secondaryAmmo.get() <= 0 && !isReloading) {
            bulletsLeft.get(finalUuid).set(1, true);
            player.sendMessage(ChatColor.YELLOW + "Reloading...");
            playWeaponSound(player, "Start_Mechanics");
            reloadTaskWeapon = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                secondaryAmmo.set(1);
                bulletsLeft.get(finalUuid).set(4, 1);
                renameItem(weapon_name, secondaryAmmo.get(), player);
                playWeaponSound(player, "Finish_Mechanics");
                player.sendMessage(ChatColor.GREEN + "Reload complete! Magazine refilled.");
                bulletsLeft.get(finalUuid).set(1, false);
            }, reloadTime);
        }
    }

    private void launchGrenade(Player player){
        ItemStack ItemInHand = player.getInventory().getItemInMainHand();
        UUID finalUuid = returnUUID(ItemInHand);

        boolean isReloading = (Boolean) bulletsLeft.get(finalUuid).get(1);
        int secondaryAmmo = (int) bulletsLeft.get(finalUuid).get(4);
        if (secondaryAmmo > 0 && !isReloading) {
            bulletsLeft.get(finalUuid).set(4, 0);
            canShoot = true;
            TNTPrimed tnt = player.getWorld().spawn(player.getLocation().add(0, 1, 0), TNTPrimed.class);

            // Set properties of the TNT
            tnt.setFuseTicks(40); // Time until explosion (e.g., 4 seconds)
            tnt.setVelocity(player.getLocation().getDirection().multiply(1.5)); // Launch it forward
            tnt.setSource(player); // Sets the player as the source (if needed for damage tracking)

        }
        AtomicReference<String> weapon_name = new AtomicReference<>();
        weapon.getWeapons().forEach((key, value) -> {
            if (value.getRoot().getInfo().getWeapon_item().getType().equalsIgnoreCase(ItemInHand.getType().toString())) {
                weapon_name.set(value.getRoot().getInfo().getWeapon_item().getName());
            }
        });
        secondaryAmmo = (int) bulletsLeft.get(finalUuid).get(4);
        renameItem(weapon_name.get(), secondaryAmmo, player);
        reloadGrenadeLauncher(player, finalUuid, weapon_name.get());

    }


    public void shootSemiAuto(Player player){
        // Check if the player is holding a diamond pickaxe
        ItemStack ItemInHand = player.getInventory().getItemInMainHand();
        UUID finalUuid = returnUUID(ItemInHand);
        if (bulletsLeft.get(finalUuid) == null){
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (shootingType != null && shootingType.equalsIgnoreCase("semi-auto") && !dontShoot && ItemInHand.getType() != Material.AIR){
                if (!(boolean) bulletsLeft.get(finalUuid).get(3)){
                    handleShooting(player);
                }else {
                    launchGrenade(player);
                }

            }
        },1);

    }

    public UUID returnUUID(ItemStack ItemInHand) {

        ItemMeta meta = ItemInHand.getItemMeta();
        UUID uuid = UUID.randomUUID();
        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.has(new NamespacedKey(plugin, "unique_id"), PersistentDataType.STRING)) {
                String uuidString = container.get(new NamespacedKey(plugin, "unique_id"), PersistentDataType.STRING);
                uuid = UUID.fromString(uuidString);
            }
        }
        return uuid;
    }

    public boolean isFlagAllowed(Player player, StateFlag flag) {
        // Convert the player's location to WorldGuard's location type
        com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(player.getLocation());

        // Get the WorldGuard region container and query
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        // Get all applicable regions for the player's location
        ApplicableRegionSet regions = query.getApplicableRegions(wgLocation);

        // Iterate through all regions and check if minecarts are allowed
        for (com.sk89q.worldguard.protection.regions.ProtectedRegion region : regions) {
            // Check if the PLACE_VEHICLE flag is set
            StateFlag.State flagState = region.getFlag(flag);

            // If the flag is not ALLOW, return true to indicate that minecarts are not allowed
            if (flagState != StateFlag.State.ALLOW) {
                return false; // Minecarts are not allowed in this region
            }
        }

        // If no region explicitly denies minecarts, return false (minecarts are allowed)
        return true;
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Check if the player is holding a diamond pickaxe
        ItemStack ItemInHand = player.getInventory().getItemInMainHand();
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null && clickedBlock.getType() == Material.CRAFTING_TABLE) {
            event.setCancelled(true);
            dontShoot = true;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                dontShoot = false;
            },2);
        }


        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();

            // Check if the item is a minecart
            if (item != null && item.getType() == Material.MINECART) {
                // Get the clicked block location
                if (clickedBlock != null && isFlagAllowed(player, Flags.PLACE_VEHICLE)) {
                    Location location = clickedBlock.getLocation().add(0.5, 1, 0.5);

                    // Summon a minecart at the clicked location
                    Minecart minecart = (Minecart) location.getWorld().spawnEntity(location, EntityType.MINECART);
                    minecart.setVelocity(new Vector(0, 0, 0)); // Prevent immediate movement

                    // Cancel the original block placement
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.GREEN + "Minecart placed!");
                    player.getInventory().remove(item);
                }
            }
        }

        weapon.getTools().forEach((key, value) -> {
            if (value.getRoot().getInfo().getWeapon_item().getType().equalsIgnoreCase(ItemInHand.getType().toString())){
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (clickedBlock != null && clickedBlock.getType() == Material.CHEST) {
                        event.setCancelled(true);
                    }
                    if ((isHoe(ItemInHand) || isShovel(ItemInHand)) && clickedBlock.getType() == Material.GRASS_BLOCK){
                        event.setCancelled(true);
                    }
                }
                if (event.getAction().toString().contains("LEFT_CLICK_BLOCK")){
                    event.setCancelled(true);
                }
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    // Launch an egg from the player's location
                    handleShooting(player);

                }
            }


        });

        weapon.getWeapons().forEach((key, value) -> {
            if (value.getRoot().getInfo().getWeapon_item().getType().equalsIgnoreCase(ItemInHand.getType().toString())) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (clickedBlock != null && clickedBlock.getType() == Material.CHEST) {
                        event.setCancelled(true);
                        dontShoot = true;
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            dontShoot = false;
                        },2);
                    }
                    if ((isHoe(ItemInHand) || isShovel(ItemInHand)) && clickedBlock.getType() == Material.GRASS_BLOCK){
                        event.setCancelled(true);
                    }
                }
                if (event.getAction().toString().contains("LEFT_CLICK_BLOCK")){
                    event.setCancelled(true);
                }
                if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (zoomAmount > 0 && !zoomOn && !wasDrop){
                        double temp = zoomAmount*10 - 10;

                        zoomOn = true;

                        zoomScheduler = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1, (int) temp, false, false));
                        },0,1);


                    } else if (zoomAmount > 0 && zoomScheduler != null && !zoomScheduler.isCancelled()) {
                        zoomScheduler.cancel();
                        zoomScheduler = null;
                        zoomOn = false;
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    }
                }
                // Check for left-click (ACTION_LEFT_CLICK_AIR or ACTION_LEFT_CLICK_BLOCK)
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    // Launch an egg from the player's location

                    if (ItemInHand.getType() == Material.FLINT_AND_STEEL && isFlagAllowed(player, Flags.PVP)) {
                        flameThrower(player, ItemInHand, value);
                    }else{
                        if (!shootingType.equalsIgnoreCase("semi-auto")){
                            if (controlFire == null || controlFire.isCancelled()){
                                UUID finalUuid = returnUUID(ItemInHand);
                                boolean isReloading = (Boolean) bulletsLeft.get(finalUuid).get(1);
                                if (isReloading && reloadTaskWeapon != null && !reloadTaskWeapon.isCancelled() && weapon_Type.equalsIgnoreCase("shotgun") && (Integer) bulletsLeft.get(finalUuid).get(0) > 0) {
                                    reloadTaskWeapon.cancel();
                                    reloadTaskWeapon = null;
                                    bulletsLeft.get(finalUuid).set(1, false);
                                    bulletsLeft.get(finalUuid).set(2, false);
                                }
                                controlFire = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                                    int tickCount = 0; // Track the number of ticks

                                    @Override
                                    public void run() {
                                        handleShooting(player);
                                        tickCount++;

                                        if (tickCount >= 4) {
                                            controlFire.cancel();
                                        }
                                    }
                                }, 0, 1); // Start immediately and repeat every tick
                            }
                        }else if (ItemInHand.getType().toString().equalsIgnoreCase("Diamond_pickaxe")){

                            UUID finalUuid = returnUUID(ItemInHand);

                            boolean state = (boolean) bulletsLeft.get(finalUuid).get(3);
                            bulletsLeft.get(finalUuid).set(3, !state);

                            boolean isReloading = (Boolean) bulletsLeft.get(finalUuid).get(1);
                            if ((boolean) bulletsLeft.get(finalUuid).get(3)) {
                                int secondaryAmmo = (int) bulletsLeft.get(finalUuid).get(4);
                                renameItem(value.getRoot().getInfo().getWeapon_item().getName(), secondaryAmmo, player);
                            }else {
                                renameItem(value.getRoot().getInfo().getWeapon_item().getName(), (Integer) bulletsLeft.get(finalUuid).getFirst(),player);
                            }
                            if (reloadTaskWeapon != null && !reloadTaskWeapon.isCancelled() && isReloading){
                                reloadTaskWeapon.cancel();
                                reloadTaskWeapon = null;
                                bulletsLeft.get(finalUuid).set(1, false);
                                player.sendMessage(ChatColor.DARK_AQUA + "Reload canceled!");

                            }

                        }
                    }
                }
            }
        });

    }

    public void flameThrower(Player player, ItemStack ItemInHand, WeaponClass value) {
        UUID finalUuid = returnUUID(ItemInHand);
        int val = Integer.parseInt(bulletsLeft.get(finalUuid).getFirst().toString());

        boolean isReloading = (Boolean) bulletsLeft.get(finalUuid).get(1);
        boolean shot = (Boolean) bulletsLeft.get(finalUuid).get(2);

        if (val > 0 && !shot && !isReloading) {
            bulletsLeft.get(finalUuid).set(2, true);
            bulletsLeft.get(finalUuid).set(0, val - 1);
            canShoot = true;
            for(int i = 0; i <24; i++){
                Egg egg = player.launchProjectile(Egg.class);
                //egg.setShooter(player);
                //eggSpawnedByPlayer.put(egg.getShooter(), player);
                Vector originalVelocity = egg.getVelocity();

                originalVelocity.setX(originalVelocity.getX() * 0.5);
                originalVelocity.setY(originalVelocity.getY() * 0.5);
                originalVelocity.setZ(originalVelocity.getZ() * 0.5);


                Vector newVelocity = originalVelocity.add(randomOffset(new Random()));
                egg.setVelocity(newVelocity);
            }
            shootTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                bulletsLeft.get(finalUuid).set(2, false);
            }, fireRate);

        }
        int bullets = Integer.parseInt(bulletsLeft.get(finalUuid).getFirst().toString());
        renameItem(value.getRoot().getInfo().getWeapon_item().getName(), bullets, player);
        if (Integer.parseInt(bulletsLeft.get(finalUuid).getFirst().toString()) <= 0 && !isReloading) {
            reloadTool(value, player, finalUuid);
        }
    }

    public void reloadTool(WeaponClass value, Player player, UUID finalUuid) {
        bulletsLeft.get(finalUuid).set(1, true);
        renameItem(value.getRoot().getInfo().getWeapon_item().getName(), Integer.parseInt(bulletsLeft.get(finalUuid).getFirst().toString()), player);
        player.sendMessage(ChatColor.YELLOW + "Reloading...");
        playWeaponSound(player, "Start_Mechanics");
        reloadTaskTool = Bukkit.getScheduler().runTaskLater(plugin, () -> {

            magSize = value.getRoot().getReload().getMagazine_size();
            bulletsLeft.get(finalUuid).set(0, magSize);
            bulletsLeft.get(finalUuid).set(1, false);

            renameItem(value.getRoot().getInfo().getWeapon_item().getName(), magSize, player);
            playWeaponSound(player, "Finish_Mechanics");
            player.sendMessage(ChatColor.GREEN + "Reload complete! Magazine refilled.");
        }, reloadTime);
    }

    private void playWeaponSound(Player player, String mechanics) {
        for (String sound : weaponSounds.get(mechanics)){
            if (sound.contains("sound")){
                String play = sound.substring(sound.indexOf("sound=")+6, sound.indexOf("}"));
                String volume = "1";
                String pitch = "1";
                String noise = "1";
                if (sound.contains("volume")) {
                    play = sound.substring(sound.indexOf("sound=")+6, sound.indexOf(", v"));
                    if (sound.contains("pitch")) {
                        volume = sound.substring(sound.indexOf("volume=")+7, sound.indexOf(", p"));
                        pitch = sound.substring(sound.indexOf("pitch=")+6, sound.indexOf(", n"));
                    }else{
                        volume = sound.substring(sound.indexOf("volume=")+7, sound.indexOf(", n"));
                    }
                } else if (sound.contains("pitch")) {
                    play = sound.substring(sound.indexOf("sound=")+6, sound.indexOf(", p"));
                    if (sound.contains("listener")) {
                        pitch = sound.substring(sound.indexOf("pitch=")+6, sound.indexOf(", l"));
                    }else {
                        pitch = sound.substring(sound.indexOf("pitch=")+6, sound.indexOf("}"));
                    }
                }

                if (sound.contains("noise") && !sound.contains("listener")) {
                    noise = sound.substring(sound.indexOf("noise=")+6, sound.indexOf("}"));
                }else if (sound.contains("noise")) {
                    noise = sound.substring(sound.indexOf("noise=")+6, sound.indexOf(", l"));
                }

                player.getWorld().playSound(player.getLocation(), Sound.valueOf(play), Float.parseFloat(volume), Float.parseFloat(pitch));
            }
        }
    }

    public void reloadWeapon(WeaponClass value, Player player, UUID finalUuid) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        renameItem(value.getRoot().getInfo().getWeapon_item().getName(), Integer.parseInt(bulletsLeft.get(finalUuid).getFirst().toString()), player);
        bulletsLeft.get(finalUuid).set(1, true);
        String ammo = switch (Projectile_Type.toLowerCase()) {
            case "assaultrifle_ammo" ->
                    weapon.getAmmos().get("Assaultrifle_Ammo").getAssaultrifle_ammo().getItem_ammo().bullet_item.getType();
            case "pistol_ammo" ->
                    weapon.getAmmos().get("Assaultrifle_Ammo").getPistol_ammo().getItem_ammo().bullet_item.getType();
            case "shotgun_ammo" ->
                    weapon.getAmmos().get("Assaultrifle_Ammo").getShotgun_ammo().getItem_ammo().bullet_item.getType();
            case "sniper_ammo" ->
                    weapon.getAmmos().get("Assaultrifle_Ammo").getSniper_ammo().getItem_ammo().bullet_item.getType();
            default -> "";
        };
        Material ammoMaterial = Material.matchMaterial(ammo);
        if (ammoMaterial == null){
            return;
        }
        boolean hasAmmo = player.getInventory().contains(ammoMaterial);
        if (!hasAmmo) {
            bulletsLeft.get(finalUuid).set(1, false);
            if (!outOfAmmoCooldown){
                playWeaponSound(player, "Out_Of_Ammo_Mechanics");
                player.sendMessage(ChatColor.GREEN + "No ammo");
                outOfAmmoCooldown = true;
                Bukkit.getScheduler().runTaskLater(plugin, () -> outOfAmmoCooldown = false,fireRate);
            }

            return;
        }

        AtomicInteger totalCount = new AtomicInteger(Arrays.stream(player.getInventory().getContents()).filter(item -> item != null && item.getType() == ammoMaterial).mapToInt(ItemStack::getAmount).sum());

        // Check the material
        // Add the stack size
        player.sendMessage(ChatColor.YELLOW + "Reloading...");
        playWeaponSound(player, "Start_Mechanics");
        reloadTaskWeapon = Bukkit.getScheduler().runTaskLater(plugin, () -> {

            int remaining = Integer.parseInt(bulletsLeft.get(finalUuid).getFirst().toString());

            magSize = value.getRoot().getReload().getMagazine_size();

            int remove = magSize - remaining;
            if (totalCount.get() + remaining < magSize){
                magSize = totalCount.get() + remaining;
                remove = totalCount.get();
            }
            if (ammoPerReload > 0){
                remove = Math.min(ammoPerReload, totalCount.get());
                magSize = remaining + ammoPerReload;
            }

            removeItems(ammoMaterial, remove, player);
            bulletsLeft.get(finalUuid).set(0, magSize);
            bulletsLeft.get(finalUuid).set(1, false);

            renameItem(value.getRoot().getInfo().getWeapon_item().getName(), magSize, player);
            if (ammoPerReload > 0 && magSize < value.getRoot().getReload().getMagazine_size()){
                try {
                    reloadWeapon(value, player, finalUuid);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            playWeaponSound(player, "Finish_Mechanics");
            player.sendMessage(ChatColor.GREEN + "Reload complete! Magazine refilled.");
        }, reloadTime);
        return; // Prevent shooting during reload
    }

    public void removeItems(Material itemType, int amountToRemove, Player player) {
        int remainingAmount = amountToRemove;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == itemType) {
                int stackSize = item.getAmount();

                if (stackSize > remainingAmount) {
                    // Reduce the stack size by the remaining amount
                    item.setAmount(stackSize - remainingAmount);
                    remainingAmount = 0;
                    break; // Done removing items
                } else {
                    // Remove the entire stack
                    remainingAmount -= stackSize;
                    player.getInventory().remove(item); // Remove this stack
                }
            }

            // Stop if we've removed all required items
            if (remainingAmount <= 0) {
                break;
            }
        }
    }


    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG)
        {
            event.setCancelled(true);
        }
    }

    public void onEntitySpawnEvent(EntitySpawnEvent event) {

        Entity entity = event.getEntity();


        // Double check: One here and one in shoot manager.
        if (entity instanceof Projectile projectile && projectile.getShooter() instanceof Player player) {

            if (projectile.getType().equals(EntityType.EGG)) {
                projectile.setInvulnerable(true);
                AtomicReference<BukkitTask> onFireTask = new AtomicReference<>();
                if (flaming){
                    projectile.setFireTicks(20*60);
                }

//                onFireTask.set(Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
//                    @Override
//                    public void run() {
//                        // Stop if the entity is no longer valid (e.g., it hit something)
//                        if (!entity.isValid()) {
//                            onFireTask.get().cancel();
//                            return;
//                        }
//
//                        // Spawn flame particles at the entity's location
//                        if (flaming){
//                            projectile.setFireTicks(5);
//                        }
//
//
//                        //Location loc = entity.getLocation();
//                        //loc.getWorld().spawnParticle(Particle.FLAME, loc, 5, 0.1, 0.1, 0.1, 0.01);
//                    }
//
//                }, 0L, 1L)); // Repeat every tick (20 ticks per second)
            }
        }
    }

    private double calculateDamageWithArmor(double damage, double armorPoints, double armorToughness) {
        // Standard armor formula used in Minecraft
        double reduction = armorPoints -  (4*damage/ (armorToughness + 8));
        return damage * (1 - Math.max(reduction, armorPoints / 5) / 25.0);
    }

    private double applyProtectionEnchantmentReduction(double damage, int protectionLevel) {
        // Each level of protection reduces damage by 4%
        double protectionFactor = protectionLevel * 0.03;
        return damage * (1 - Math.min(protectionFactor, 0.8)); // Cap at 80% reduction
    }


    public void onEggHit(ProjectileHitEvent event) {
        // Check if the projectile is an Egg
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();
        Player player = (Player) shooter;

        // Check if the egg hits an entity (such as a player or mob)

        if (player != null && event.getHitEntity() instanceof LivingEntity hitEntity && hitEntity != player) {
            hitEntity.setNoDamageTicks(0);
            // Apply custom damage to the entity
            Location entityLocation = hitEntity.getLocation();
            Location playerLocation = player.getLocation();
            Location hitLocation = projectile.getLocation();
            double distance = entityLocation.distance(playerLocation);
            AtomicReference<Double> damageChange = new AtomicReference<>((double) 0);
            if (!damageDrop.isEmpty()){
                damageDrop.forEach((key, value) ->{
                    if (distance > key){
                        damageChange.set(value);
                    }
                });
            }


            // Calculate the head's region (typically ~1 block above the entity's eye level)
            double headHeight = entityLocation.getY() + hitEntity.getHeight() * 0.8; // Adjust for different entities
            double extraDamage = 0;
            // Check if the hit was in the "head" region
            if (hitLocation.getY() >= headHeight && bonusDamage > 0) {
                // It's a headshot!
                player.getWorld().playSound(hitLocation, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
                hitEntity.getWorld().spawnParticle(Particle.EXPLOSION, hitLocation, 5);
                hitEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, (int) 2, false, false));

                // Spawn a firework with custom properties
                Firework firework = hitEntity.getWorld().spawn(hitLocation, Firework.class);

                FireworkMeta meta = firework.getFireworkMeta();


                // Set the firework effect
                FireworkEffect effect = FireworkEffect.builder()
                        .with(FireworkEffect.Type.BALL) // Shape: BALL
                        .withColor(Color.AQUA, Color.fromRGB(85, 255, 255)) // Color: AQUA and Light Blue
                        .withTrail() // Enable trail
                        .build();

                meta.addEffect(effect);
                meta.setPower(1); // Flight time (scaled down for immediate detonation)

                firework.setFireworkMeta(meta);
                Bukkit.getScheduler().runTaskLater(plugin, firework::detonate,7);
                // Detonate the firework immediately

                extraDamage = bonusDamage;
                // Log or handle headshot event
                System.out.println("Headshot detected on " + hitEntity.getName());
            }

            double baseDamage = damage + extraDamage + damageChange.get();

            // Get the armor points of the entity

            double armorPoints = Objects.requireNonNull(hitEntity.getAttribute(Attribute.ARMOR)).getValue();
            double armorToughness = Objects.requireNonNull(hitEntity.getAttribute(Attribute.ARMOR_TOUGHNESS)).getValue();
            // Factor in armor reduction
            double damageAfterArmor = calculateDamageWithArmor(baseDamage, armorPoints, armorToughness);

            // Check for protection enchantments

            ItemStack[] armorContents = Objects.requireNonNull(hitEntity.getEquipment()).getArmorContents();


            AtomicInteger protectionLevel = new AtomicInteger(0);
            // Loop through each piece of armor
            for (ItemStack armorPiece : armorContents) {
                if (armorPiece != null) { // Ensure the armor piece is not null
                    ItemMeta armorMeta = armorPiece.getItemMeta();
                    if (armorMeta instanceof Damageable) {
                        ((Damageable) armorMeta).setDamage(((Damageable) armorMeta).getDamage()+ (int) armorDamage);
                        armorPiece.setItemMeta(armorMeta);
                    }
                    //armorPiece.setDurability((short) (armorPiece.getDurability() + armorDamage));

                    // Get the enchantments on this armor piece
                    if (armorMeta != null && Objects.requireNonNull(armorPiece.getItemMeta()).hasEnchants()) {
                        armorMeta.getEnchants().forEach((enchantment, level) -> {
                            // Print out the enchantment and its level
                            if (enchantment.getKey().getKey().equalsIgnoreCase("protection")) {
                                protectionLevel.addAndGet(level);
                            }
                        });
                    }
                }
            }
//            hitEntity.getEquipment().setArmorContents(armorContents);
            if (protectionLevel.get() > 0) {
                damageAfterArmor = applyProtectionEnchantmentReduction(damageAfterArmor, protectionLevel.get());
            }
            double absorption = hitEntity.getAbsorptionAmount();
            // Apply the calculated damage

            if (damageEvent){
                return;
            }
            System.out.println(damageAfterArmor);

            if (absorption > 0) {
                if (damageAfterArmor <= absorption) {
                    // Damage is fully absorbed
                    hitEntity.setAbsorptionAmount(absorption - damageAfterArmor);
                } else {
                    // Part of the damage is absorbed, subtract the remaining from regular health
                    double remainingDamage = damageAfterArmor - absorption;
                    hitEntity.setAbsorptionAmount(0); // Remove all absorption
                    hitEntity.setHealth(Math.max(0, hitEntity.getHealth() - remainingDamage)); // Subtract remaining damage from health
                }
            } else {
                // No absorption, subtract damage directly from health
                double remainingHealth = Math.max(0, hitEntity.getHealth() - damageAfterArmor);

                if (remainingHealth > 0){
                    hitEntity.setHealth(remainingHealth);
                }else{
                    hitEntity.damage(damageAfterArmor*40, player);
                }
            }
            if (hitEntity.getHealth() > 0){
                hitEntity.damage(0.5);
            }



            if (projectile.getType().equals(EntityType.EGG)) {
                if (player.getInventory().getItemInMainHand().getType() == Material.FLINT_AND_STEEL){
                    hitEntity.setFireTicks(20 * 5);
                }else if (flaming){
                    hitEntity.setFireTicks(20*3);
                }

            }
            damageEvent = true;
            if (weapon_Type.equalsIgnoreCase("shotgun")){
                damageEvent = false;
            }else if (weapon_Type.equalsIgnoreCase("flame")){
                Bukkit.getScheduler().runTaskLater(plugin, () ->{
                    damageEvent = false;
                },10);
            }else {
                Bukkit.getScheduler().runTaskLater(plugin, () ->{
                    damageEvent = false;
                },1);
            }

        }
    }
    private Vector randomOffset(Random random) {
        return new Vector(
                (random.nextDouble() - 0.5) * spread,
                (random.nextDouble() - 0.5) * spread,
                (random.nextDouble() - 0.5) * spread
        );
    }

    public void applyLeap(String command, Player player) {
        // Parse the speed
        double speed = 0.00; // Default value
        if (command.contains("speed=")) {
            String speedString = command.split("speed=")[1].split("}")[0];
            speed = Double.parseDouble(speedString);
        }

        // Parse the offset
        Vector offset = new Vector(0, 0, 0); // Default offset
        if (command.contains("offset=")) {
            String offsetString = command.split("offset=~")[1].split("}")[0];
            String[] components = offsetString.split(" ");
            offset = new Vector(
                    components[0].equals("~") ? 0 : Double.parseDouble(components[0]),
                    components[1].equals("~") ? 0 : Double.parseDouble(components[1]),
                    components[2].equals("~") ? -1 : Double.parseDouble(components[2])
            );
        }

        Vector playerDirection = player.getLocation().getDirection().normalize();
        Vector movement = playerDirection.clone().multiply(speed*offset.getZ());
        movement.setY(movement.getY() + speed * offset.getY());
        movement.setX(movement.getX() + speed * offset.getX());
        //Vector movement = offset.clone().normalize().multiply(offset.getZ());


        // Apply the velocity to the player
        if (!offset.isZero()){
            player.setVelocity(movement);
        }


    }

    private void useTool(WeaponClass data, Player player) {
        ArrayList<String> leap = (ArrayList<String>) data.getRoot().getShoot().getMechanics();
        applyLeap(leap.getFirst(), player);


        for (String sound : weaponSounds.get("Shoot_Mechanics")){
            if (sound.contains("sound")){
                String play = sound.substring(sound.indexOf("sound=")+6, sound.indexOf(", v")).strip();
                String volume = sound.substring(sound.indexOf("volume=")+7, sound.indexOf(", p"));
                String pitch = sound.substring(sound.indexOf("pitch=")+6, sound.indexOf(", n"));
                String noise = sound.substring(sound.indexOf("noise=")+6, sound.indexOf("}"));

                player.getWorld().playSound(player.getLocation(), Sound.valueOf(play), Float.parseFloat(volume), Float.parseFloat(pitch)); // Gunshot-like sound
            }

        }

        UUID finalUuid = returnUUID(player.getInventory().getItemInMainHand());


        int bullets = Integer.parseInt(bulletsLeft.get(finalUuid).getFirst().toString());
        renameItem(data.getRoot().getInfo().getWeapon_item().getName(), bullets, player);
    }

    private void fireRifle(WeaponClass data, Player player) {

        ArrayList<String> leap = (ArrayList<String>) data.getRoot().getShoot().getMechanics();
        applyLeap(leap.getFirst(), player);

        // Get the player's eye location and direction
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection(); // The direction the player is facing
        direction.normalize();

        class ProjectileDetails {
            private String type;
            private double gravity; // Add any other values you need
            private double drag;
            private int speed;

            public ProjectileDetails(String type, double gravity, double drag, int speed) {
                this.type = type;
                this.gravity = gravity;
                this.drag = drag;
                this.speed = speed;
            }

            public String getType() {
                return type;
            }

            public double getGravity() {
                return gravity;
            }
            public double getSpeed() {
                return speed;
            }

            public double getDrag() {
                return drag;
            }
        }

        ProjectileDetails projectile_type = switch (weapon_Type.toLowerCase()) {
            case "assault_rifle" -> new ProjectileDetails(
                    weapon.getProjectiles().get("sniper_rifle").getAssault_rifle().getProjectile().getProjectile_settings().getType(),
                    weapon.getProjectiles().get("sniper_rifle").getAssault_rifle().getProjectile().getProjectile_settings().getGravity(),
                    weapon.getProjectiles().get("sniper_rifle").getAssault_rifle().getProjectile().getProjectile_settings().getDrag().getBase(),
                    weapon.getProjectiles().get("sniper_rifle").getAssault_rifle().getProjectile().getProjectile_settings().getMinimum().getSpeed()
                    );
            case "pistol" ->new ProjectileDetails(
                    weapon.getProjectiles().get("sniper_rifle").getPistol().getProjectile().getProjectile_settings().getType(),
                    weapon.getProjectiles().get("sniper_rifle").getPistol().getProjectile().getProjectile_settings().getGravity(),
                    weapon.getProjectiles().get("sniper_rifle").getPistol().getProjectile().getProjectile_settings().getDrag().getBase(),
                    0
            );
            case "shotgun" ->new ProjectileDetails(
                    weapon.getProjectiles().get("sniper_rifle").getShotgun().getProjectile().getProjectile_settings().getType(),
                    weapon.getProjectiles().get("sniper_rifle").getShotgun().getProjectile().getProjectile_settings().getGravity(),
                    weapon.getProjectiles().get("sniper_rifle").getShotgun().getProjectile().getProjectile_settings().getDrag().getBase(),
                    weapon.getProjectiles().get("sniper_rifle").getShotgun().getProjectile().getProjectile_settings().getMinimum().getSpeed()
            );
            case "sniper_rifle" ->new ProjectileDetails(
                    weapon.getProjectiles().get("sniper_rifle").getSniper_rifle().getProjectile().getProjectile_settings().getType(),
                    weapon.getProjectiles().get("sniper_rifle").getSniper_rifle().getProjectile().getProjectile_settings().getGravity(),
                    weapon.getProjectiles().get("sniper_rifle").getSniper_rifle().getProjectile().getProjectile_settings().getDrag().getBase(),
                    weapon.getProjectiles().get("sniper_rifle").getSniper_rifle().getProjectile().getProjectile_settings().getMinimum().getSpeed()
            );
            default -> new ProjectileDetails("", 0, 0, 0);
        };
        if (Objects.equals(projectile_type.type, "INVISIBLE")){
            projectile_type.type = "SNOWBALL";
        }
        EntityType entityType = EntityType.valueOf(projectile_type.type.toUpperCase());
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (int i = 0; i <= projectilesPerShot; i++) {
                Vector offset = randomOffset(new Random());

                Entity entity = player.getWorld().spawnEntity(eyeLocation.add(offset), entityType);
                if (entity instanceof Projectile projectile) {
                    projectile.setShooter(player);
                    projectile.setGravity(false);

                    Vector velocity = player.getEyeLocation().getDirection().normalize().multiply((double) (Projectile_Speed + projectile_type.speed/10) / 100 + 0.5); // Custom initial velocity
                    projectile.setVelocity(velocity);

                    Vector gravity = new Vector(0, projectile_type.getGravity() / 200 * -1, 0); // Custom gravity
                    double dragStrength = projectile_type.getDrag();
                    Vector drag = new Vector(dragStrength, 1, dragStrength); // Custom drag

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // Stop updating velocity if the projectile is no longer valid or is dead
                            if (!projectile.isValid() || projectile.isDead()) {
                                this.cancel();  // Cancel this task if the projectile is no longer valid
                                return;
                            }
                            // Apply gravity to the projectile's velocity
                            projectile.setVelocity(projectile.getVelocity().add(gravity).multiply(drag));
                        }
                    }.runTaskTimer(plugin, 0, 1);
                }
            }
        });

        playWeaponSound(player, "Shoot_Mechanics");

        UUID finalUuid = returnUUID(player.getInventory().getItemInMainHand());


        int bullets = Integer.parseInt(bulletsLeft.get(finalUuid).getFirst().toString());
        renameItem(data.getRoot().getInfo().getWeapon_item().getName(), bullets, player);
    }

    private void renameItem(String newName, Integer bullets, Player player) {
        int index = newName.indexOf(">");
        String itemName = newName.substring(index + 1);
        ChatColor color = ChatColor.valueOf(newName.substring(1, index).toUpperCase());
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }

        // Schedule a new task to repeatedly send the message
        currentTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color+itemName+" «"+bullets+"»"));
        }, 0L, 40L); // Repeats every 2 seconds (40 ticks)
        // Stop sending after the specified duration
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (currentTask != null) {
                currentTask.cancel();
                currentTask = null;
            }
        }, 100);
    }
}
