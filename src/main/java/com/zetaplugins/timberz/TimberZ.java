package com.zetaplugins.timberz;

import com.zetaplugins.timberz.listener.AxeEquipListener;
import com.zetaplugins.timberz.listener.TreeBreakListener;
import com.zetaplugins.timberz.service.PlayerStateService;
import com.zetaplugins.timberz.service.TimberZService;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class TimberZ extends JavaPlugin implements @NotNull Listener {
    private PlayerStateService playerStateService;

    @Override
    public void onEnable() {
        this.playerStateService = new PlayerStateService(this);
        TimberZService treeFellerService = new TimberZService(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(
                new TreeBreakListener(this, treeFellerService, playerStateService), this);
        getServer().getPluginManager().registerEvents(
                new AxeEquipListener(this, playerStateService), this);

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("TimberZ has been enabled!");
    }

    @Override
    public @NotNull Path getDataPath() {
        return super.getDataPath();
    }

    @Override
    public void onDisable() {
        if (playerStateService != null) {
            playerStateService.cleanupAll();
        }
        getLogger().info("TimberZ has been disabled!");
    }

    public PlayerStateService getPlayerStateService() {
        return playerStateService;
    }
}