package me.matt11matthew.gui.player;

import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Matthew E on 6/19/2017.
 */
public class PlayerManager implements Listener {
    private static PlayerManager instance;
    private Map<UUID, StickPlayer> stickPlayerMap;

    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }

    public PlayerManager() {
        instance = this;
        stickPlayerMap = new HashMap<>();
    }

    public void addStickPlayer(UUID uuid) {
        stickPlayerMap.put(uuid, new StickPlayer(uuid));
    }

    public Map<UUID, StickPlayer> getStickPlayerMap() {
        return stickPlayerMap;
    }

    public StickPlayer getPlayer(UUID uniqueId) {
        return stickPlayerMap.get(uniqueId);
    }
}
