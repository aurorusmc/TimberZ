package com.zetaplugins.timberz;

import com.zetaplugins.timberz.service.*;
import com.zetaplugins.timberz.service.papi.Metrics;
import com.zetaplugins.timberz.service.registrars.CommandRegistrar;
import com.zetaplugins.timberz.service.registrars.EventRegistrar;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class TimberZ extends JavaPlugin implements Listener {
    private PlayerStateService playerStateService;
    private MessageService messageService;
    private LocalizationService localizationService;
    private TreeFellerService treeFellerService;
    private TreeDetectionService treeDetectionService;
    private VersionChecker versionChecker;
    private ConfigService configService;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        this.localizationService = new LocalizationService(this);
        this.messageService = new MessageService(this);

        this.versionChecker = new VersionChecker(this);
        this.configService = new ConfigService(this);

        this.playerStateService = new PlayerStateService(this);
        this.treeFellerService = new TreeFellerService(this);
        this.treeDetectionService = new TreeDetectionService(this);

        configService.initConfigs();
        versionChecker.checkForUpdates();

        new EventRegistrar(this).registerListeners();
        new CommandRegistrar(this).registerCommands();

        getServer().getPluginManager().registerEvents(this, this);

        initializeBStats();

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

    private void initializeBStats() {
        int pluginId = 25743;
        Metrics metrics = new Metrics(this, pluginId);

        metrics.addCustomChart(new Metrics.SimplePie("language", () -> getConfig().getString("lang")));
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

    public TreeDetectionService getTreeDetectionService() {
        return treeDetectionService;
    }

    public ConfigService getConfigService() {
        return configService;
    }
}