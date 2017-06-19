package me.matt11matthew.gui.item;

import me.matt11matthew.gui.StickGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew E on 6/19/2017.
 */
public class GuiItem {
    private int slot;
    private String name;
    private List<String> loreStringList;
    private String command;
    private int uses;
    private int delay;
    private double cost;

    public GuiItem(int slot) {
        this.slot = slot;
        FileConfiguration config = StickGui.getInstance().getConfig();
        this.loreStringList = new ArrayList<>();
        if (config.isSet("sticks." + slot)) {
            this.name = ChatColor.translateAlternateColorCodes('&',  config.getString("sticks." + slot + ".name"));
            for (String lore : config.getStringList("sticks." + slot + ".lore")) {
                loreStringList.add(ChatColor.translateAlternateColorCodes('&', lore));
            }
            this.command = config.getString("sticks." + slot + ".command-run");
            this.uses = config.getInt("sticks." + slot + ".uses");
            this.delay = config.getInt("sticks." + slot + ".delay");
            this.cost = config.getDouble("sticks." + slot + ".cost");
        }
    }

    public GuiItem setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public GuiItem setName(String name) {
        this.name = name;
        return this;
    }

    public GuiItem setLoreStringList(List<String> loreStringList) {
        this.loreStringList = loreStringList;
        return this;
    }

    public GuiItem setCommand(String command) {
        this.command = command;
        return this;
    }

    public GuiItem setUses(int uses) {
        this.uses = uses;
        return this;
    }

    public GuiItem setDelay(int delay) {
        this.delay = delay;
        return this;
    }

    public GuiItem setCost(double cost) {
        this.cost = cost;
        return this;
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(Material.STICK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(loreStringList);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public int getSlot() {
        return slot;
    }

    public String getName() {
        return name;
    }

    public List<String> getLoreStringList() {
        return loreStringList;
    }

    public String getCommand() {
        return command;
    }

    public int getUses() {
        return uses;
    }

    public int getDelay() {
        return delay;
    }

    public double getCost() {
        return cost;
    }
}