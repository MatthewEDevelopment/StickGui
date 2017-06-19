package me.matt11matthew.gui.player;

import me.matt11matthew.gui.item.GuiItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Matthew E on 6/19/2017.
 */
public class StickPlayer {
    private UUID uuid;
    private Map<GuiItem, Integer> usesMap;
    private Map<GuiItem, Long> timeMap;

    public StickPlayer(UUID uuid) {
        this.uuid = uuid;
        this.usesMap = new HashMap<>();
        this.timeMap = new HashMap<>();
    }

    public void addGuiItem(GuiItem guiItem) {
        usesMap.put(guiItem, guiItem.getUses());
        timeMap.put(guiItem, System.currentTimeMillis());
    }

    public long getDelay(GuiItem guiItem) {
        return timeMap.get(guiItem);
    }

    public void setUses(GuiItem guiItem, int uses) {
        if (usesMap.containsKey(guiItem)) {
            usesMap.remove(guiItem);
        }
        usesMap.put(guiItem, uses);
    }

    public void setTime(GuiItem guiItem, long time) {
        if (timeMap.containsKey(guiItem)) {
            timeMap.remove(guiItem);
        }
        timeMap.put(guiItem, time);
    }

    public int getUses(GuiItem guiItem) {
        return  usesMap.get(guiItem);
    }
}
