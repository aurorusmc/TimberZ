package com.zetaplugins.timberz.dev;

import com.zetaplugins.timberz.TimberZ;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class DevMode {
    private final TimberZ plugin;

    public DevMode(TimberZ plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        plugin.getLogger().warning("[DevMode] Development mode is active.");

        sendMessageAfterServerStart();
    }

    private void sendMessageAfterServerStart() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getLogger().warning("[DevMode] If you have not changed config, server will run at localhost:25561");

        }, 80L);
    }
}

