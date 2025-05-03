package com.zetaplugins.timberz.service;

import com.zetaplugins.timberz.TimberZ;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PlayerStateService {
    private final TimberZ plugin;
    private final String METADATA_KEY = "TimberZ_toggle";
    private final Map<Player, BukkitTask> actionBarTasks = new HashMap<>();

    public PlayerStateService(TimberZ plugin) {
        this.plugin = plugin;
    }

    public boolean isAllowedToTimber(Player player) {
        boolean toggleTimber = plugin.getConfig().getBoolean("toggleTimber");
        if (toggleTimber) return getToggleMetadata(player);
        else return true;
    }

    private boolean getToggleMetadata(Player player) {
        if (!player.hasMetadata(METADATA_KEY)) {
            return false;
        }
        for (MetadataValue value : player.getMetadata(METADATA_KEY)) {
            if (value.getOwningPlugin().equals(plugin)) {
                return value.asBoolean();
            }
        }
        return false;
    }

    private boolean isValidAxe(ItemStack item, FileConfiguration config) {
        if (item == null) return false;
        if (!config.getBoolean("restrictAxes")) return true;
        List<Integer> allowedModelData = config.getIntegerList("allowedModelData");
        if (!item.getItemMeta().hasCustomModelData()) return false;
        int itemCustomModelData = item.getItemMeta().getCustomModelData();
        System.out.println("Item Custom Model Data: " + itemCustomModelData);

        return allowedModelData.contains(itemCustomModelData);
    }

    public void setTimberEnabled(Player player, boolean state) {
        player.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, state));

        if (state) {
            startActionBar(player);
        } else {
            stopActionBar(player);
            player.sendActionBar(plugin.getMessageService().getAndFormatMsg(
                    false,
                    "timberModeOff",
                    "&7Timber: &cOFF&7"
            ));
        }
    }

    public void toggleTimberState(Player player) {
        boolean currentState = getToggleMetadata(player);
        // can only turn off when toggleTimber is disabled
        if (!plugin.getConfig().getBoolean("toggleTimber") && !currentState) return;
        setTimberEnabled(player, !currentState);
    }

    public void startActionBar(Player player) {
        stopActionBar(player); // Prevent duplicate tasks

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) {
                stopActionBar(player);
                return;
            }
            player.sendActionBar(plugin.getMessageService().getAndFormatMsg(
                            false,
                            "timberModeOn",
                            "&7Timber: &aON&7"
            ));
        }, 0L, 40L); // Repeat every 40 ticks = 2 seconds

        actionBarTasks.put(player, task);
    }

    public void stopActionBar(Player player) {
        BukkitTask task = actionBarTasks.remove(player);
        if (task != null) {
            task.cancel();
        }
    }

    public void cleanupPlayer(Player player) {
        stopActionBar(player);
    }

    public void cleanupAll() {
        for (BukkitTask task : actionBarTasks.values()) {
            task.cancel();
        }
        actionBarTasks.clear();
    }
}