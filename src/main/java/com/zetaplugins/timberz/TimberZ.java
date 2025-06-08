package com.zetaplugins.timberz;

import com.zetaplugins.timberz.commands.TimberZCommand;
import com.zetaplugins.timberz.dev.DevMode;
import com.zetaplugins.timberz.service.*;
import com.zetaplugins.timberz.service.bstats.Metrics;
import com.zetaplugins.timberz.service.registrars.EventRegistrar;
import com.zetaplugins.timberz.service.worldguard.WorldGuardManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
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
    private WorldGuardManager worldGuardManager;
    private final boolean hasWorldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;

    private boolean devMode = false;

    @Override
    public void onLoad() {
        getLogger().info("Loading TimberZ...");

        if (hasWorldGuard()) {
            getLogger().info("WorldGuard found! Enabling WorldGuard support...");
            worldGuardManager = new WorldGuardManager();
            getLogger().info("WorldGuard support enabled!");
        } else {
            getLogger().warning("WorldGuard not found! Disabling WorldGuard support.");
        }
    }

    @Override
    public void onEnable() {
        // Check if dev mode is enabled via JVM property
        String mode = System.getProperty("plugin.env", "release");
        devMode = mode.equalsIgnoreCase("dev");

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
        registerCommands();

        getServer().getPluginManager().registerEvents(this, this);

        initializeBStats();

        if (devMode) {
            new DevMode(this).enable();
        }

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

    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(new TimberZCommand(this).buildRootCommand("timberz"));
            commands.registrar().register(new TimberZCommand(this).buildRootCommand("tz"));
        });
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

    public WorldGuardManager getWorldGuardManager() {
        return worldGuardManager;
    }

    public boolean hasWorldGuard() {
        return hasWorldGuard;
    }
}
