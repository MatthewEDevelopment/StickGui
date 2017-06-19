package me.matt11matthew.gui.commands;

import me.matt11matthew.gui.StickGui;
import me.matt11matthew.gui.item.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Created by Matthew E on 6/19/2017.
 */
public class StickGuiCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("stickgui")) {
                player.openInventory(getStickGui(player));
                return true;
            }
        }
        return true;
    }

    public Inventory getStickGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Stick Gui (" + player.getName() + ")");
        for (Integer integer : StickGui.getInstance().getGuiItemMap().keySet()) {
            GuiItem guiItem = StickGui.getInstance().getGuiItemMap().get(integer);
            inventory.setItem(integer, guiItem.toItemStack());
        }
        return inventory;
    }
}
