package com.zetaplugins.timberz;

import com.zetaplugins.timberz.listener.AxeEquipListener;
import com.zetaplugins.timberz.listener.TreeBreakListener;
import com.zetaplugins.timberz.service.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class TimberZ extends JavaPlugin implements Listener {
    private PlayerStateService playerStateService;
    private MessageService messageService;
    private LocalizationService localizationService;
    private TreeFellerService treeFellerService;
    private VersionChecker versionChecker;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        this.localizationService = new LocalizationService(this);
        this.messageService = new MessageService(this);

        this.versionChecker = new VersionChecker(this);

        this.playerStateService = new PlayerStateService(this);
        this.treeFellerService = new TreeFellerService(this);
        versionChecker.checkForUpdates();

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

    public MessageService getMessageService() {
        return messageService;
    }

    public LocalizationService getLocalizationService() {
        return localizationService;
    }

    public TreeFellerService getTreeFellerService() {
        return treeFellerService;
    }

    public VersionChecker getVersionChecker() {
        return versionChecker;
    }
}