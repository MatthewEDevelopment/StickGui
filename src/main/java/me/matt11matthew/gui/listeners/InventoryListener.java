package me.matt11matthew.gui.listeners;

import me.matt11matthew.gui.StickGui;
import me.matt11matthew.gui.item.GuiItem;
import me.matt11matthew.gui.player.PlayerManager;
import me.matt11matthew.gui.player.StickPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Matthew E on 6/19/2017.
 */
public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if (inventory.getType() != InventoryType.PLAYER && inventory.getTitle().startsWith("Stick Gui")) {
            event.setCancelled(true);
            if (StickGui.getInstance().getGuiItemMap().containsKey(event.getSlot())) {
                GuiItem guiItem = StickGui.getInstance().getGuiItemMap().get(event.getSlot());
                StickPlayer stickPlayer = PlayerManager.getInstance().getPlayer(player.getUniqueId());
                if (stickPlayer.getUses(guiItem) < 1) {
                    player.sendMessage(ChatColor.RED + "You're out of uses! wait for the server to reboot");
                    return;
                }
                if (System.currentTimeMillis() > stickPlayer.getDelay(guiItem)) {
                    double cost = guiItem.getCost();
                    if (StickGui.getInstance().getEconomy().getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())) >= cost) {
                        stickPlayer.setUses(guiItem, stickPlayer.getUses(guiItem) - 1);
                        stickPlayer.setTime(guiItem, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(guiItem.getDelay()));
                        player.performCommand(guiItem.getCommand());
                        player.closeInventory();
                    } else {
                        player.sendMessage("You don't have enough money!");
                        return;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Please wait for the delay to expire");
                    return;
                }
            }
        }
    }
}
