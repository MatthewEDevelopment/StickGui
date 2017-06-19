package me.matt11matthew.gui;

import me.matt11matthew.gui.item.GuiItem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/*
The stick gui main class
 */
public final class StickGui extends JavaPlugin implements Listener {
    private static final Logger logger = Logger.getLogger("Minecraft"); // logger
    private Economy economy; // economy field
    private Map<Integer, GuiItem> guiItemMap; // gui map to save the gui items
    private Map<GuiItem, Map<UUID, Integer>> usesGuiItemMap;
    private Map<GuiItem, Map<UUID, Long>> timeGuiItemMap;

    private static StickGui instance; /// instance of StickGui

    /**
     @return gets the instance of {@link StickGui}
     */
    public static StickGui getInstance() {
        return instance;
    }

    /**
     * On enable method
     */
    @Override
    public void onEnable() {
        instance = this;
        this.guiItemMap = new HashMap<>();
        this.timeGuiItemMap = new HashMap<>();
        this.usesGuiItemMap = new HashMap<>();
        if (!this.setupEconomy() ) {
            logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        for (String slot : getConfig().getConfigurationSection("sticks").getKeys(false)) {
            GuiItem guiItem = new GuiItem(Integer.parseInt(slot));
            this.guiItemMap.put(Integer.parseInt(slot), guiItem);
            timeGuiItemMap.put(guiItem, new HashMap<>());
            usesGuiItemMap.put(guiItem, new HashMap<>());
        }
    }

    private int getUses(Player player, GuiItem guiItem) {
        if (this.usesGuiItemMap.containsKey(guiItem)) {
           if (usesGuiItemMap.get(guiItem).containsKey(player.getUniqueId())) {
              return usesGuiItemMap.get(guiItem).get(player.getUniqueId());
           } else {
               setUses(player, guiItem, guiItem.getUses());
               return guiItem.getUses();
           }
        }
        return 0;
    }

    private long getTime(Player player, GuiItem guiItem) {
        if (this.timeGuiItemMap.containsKey(guiItem)) {
           if (timeGuiItemMap.get(guiItem).containsKey(player.getUniqueId())) {
              return timeGuiItemMap.get(guiItem).get(player.getUniqueId());
           }
        }
        return 0L;
    }

    private void setTime(Player player, GuiItem guiItem, long time) {
        if (this.timeGuiItemMap.containsKey(guiItem)) {
            Map<UUID, Long> uuidLongMap = this.timeGuiItemMap.get(guiItem);
            this.timeGuiItemMap.remove(guiItem);
            if (!uuidLongMap.containsKey(player.getUniqueId())) {
                if (uuidLongMap.containsKey(player.getUniqueId())) {
                    uuidLongMap.remove(player.getUniqueId());
                }
                uuidLongMap.put(player.getUniqueId(), System.currentTimeMillis() + time);
            }
            this.timeGuiItemMap.put(guiItem, uuidLongMap);
        }
    }

    private void setUses(Player player, GuiItem guiItem, int uses) {
        if (this.usesGuiItemMap.containsKey(guiItem)) {
            Map<UUID, Integer> uuidIntegerMap = this.usesGuiItemMap.get(guiItem);
            this.usesGuiItemMap.remove(guiItem);
            if (!uuidIntegerMap.containsKey(player.getUniqueId())) {
                if (uuidIntegerMap.containsKey(player.getUniqueId())) {
                    uuidIntegerMap.remove(player.getUniqueId());
                }
                uuidIntegerMap.put(player.getUniqueId(), uses);
            }
            this.usesGuiItemMap.put(guiItem, uuidIntegerMap);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if (inventory.getType() != InventoryType.PLAYER && inventory.getTitle().startsWith("Stick Gui")) {
            event.setCancelled(true);
            if (guiItemMap.containsKey(event.getSlot())) {
                GuiItem guiItem = guiItemMap.get(event.getSlot());
                if (getUses(player, guiItem) < 1) {
                    player.sendMessage(ChatColor.RED + "You're out of uses! wait for the server to reboot");
                    return;
                }
                if (!isDelayExpired(player, guiItem)) {
                    player.sendMessage(ChatColor.RED + "Please wait for the delay to expire");
                    return;
                }
                double cost = guiItem.getCost();
                if (economy.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())) >= cost) {
                    setUses(player,guiItem,getUses(player, guiItem)-1);
                    setTime(player, guiItem, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(guiItem.getDelay()));
                    player.performCommand(guiItem.getCommand());
                    player.closeInventory();
                } else {
                    player.sendMessage("You don't have enough money!");
                    return;
                }
            }

        }
    }

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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (GuiItem guiItem : guiItemMap.values()) {
            if (usesGuiItemMap.containsKey(guiItem)) {
                if (!usesGuiItemMap.get(guiItem).containsKey(player.getUniqueId())) {
                    setUses(event.getPlayer(), guiItem, guiItem.getUses());
                }
            }
            if (timeGuiItemMap.containsKey(guiItem)) {
                if (!timeGuiItemMap.get(guiItem).containsKey(player.getUniqueId())) {
                    setTime(event.getPlayer(), guiItem, System.currentTimeMillis());
                }
            }
        }
    }

    public boolean isDelayExpired(Player player, GuiItem guiItem) {
        return System.currentTimeMillis() > getTime(player, guiItem);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

    }

    public Inventory getStickGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Stick Gui (" + player.getName() + ")");
        for (Integer integer : this.guiItemMap.keySet()) {
            GuiItem guiItem = this.guiItemMap.get(integer);
            inventory.setItem(integer, guiItem.toItemStack());
        }
        return inventory;
    }
    /**
     * this method setups up the vault eco hook
     * @return if the hook worked or not
     */
    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> economyRegisteredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyRegisteredServiceProvider == null) {
            return false;
        }
        this.economy = economyRegisteredServiceProvider.getProvider();
        return this.economy != null;
    }

    @Override
    public void onDisable() {
        logger.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    public Economy getEconomy() {
        return economy;
    }

    public Map<Integer, GuiItem> getGuiItemMap() {
        return guiItemMap;
    }
}
