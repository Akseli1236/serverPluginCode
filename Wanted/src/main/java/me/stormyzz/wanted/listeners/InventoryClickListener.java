package me.stormyzz.wanted.listeners;

import me.stormyzz.wanted.Utilities.CreditShopGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static me.stormyzz.wanted.Utilities.CreditShopGUI.config;
import static me.stormyzz.wanted.Utilities.CreditShopGUI.handleClick;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        // Get the title of the opened inventory window (the GUI title)
        String guiTitle = ChatColor.translateAlternateColorCodes('&', config.getString("gui.title"));
        String openTitle = event.getView().getTitle();

        if (openTitle.equals(guiTitle)) {
            event.setCancelled(true); // prevent item movement

            Inventory clickedInventory = event.getClickedInventory();
            if (clickedInventory != null && clickedInventory.equals(event.getView().getTopInventory())) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem == null || clickedItem.getType() == Material.AIR)
                    return;

                // Run your purchase logic
                handleClick(player, clickedItem);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String guiTitle = ChatColor.translateAlternateColorCodes('&', config.getString("gui.title"));
        if (event.getView().getTitle().equals(guiTitle)) {
            event.setCancelled(true); // prevent drag
        }
    }
}
