package me.matt11matthew.gui;

import me.matt11matthew.gui.commands.StickGuiCommand;
import me.matt11matthew.gui.item.GuiItem;
import me.matt11matthew.gui.listeners.InventoryListener;
import me.matt11matthew.gui.player.PlayerManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/*
The stick gui main class
 */
public final class StickGui extends JavaPlugin  {
    private static final Logger logger = Logger.getLogger("Minecraft"); // logger
    private Economy economy; // economy field
    private Map<Integer, GuiItem> guiItemMap; // gui map to save the gui items

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
        if (!this.setupEconomy() ) {
            logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(PlayerManager.getInstance(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        for (String slot : getConfig().getConfigurationSection("sticks").getKeys(false)) {
            GuiItem guiItem = new GuiItem(Integer.parseInt(slot));
            this.guiItemMap.put(Integer.parseInt(slot), guiItem);
        }
        this.getCommand("stickgui").setExecutor(new StickGuiCommand());

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
