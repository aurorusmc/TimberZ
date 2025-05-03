package com.zetaplugins.timberz.service;

import com.zetaplugins.timberz.TimberZ;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class ConfigService {
    private final TimberZ plugin;

    public ConfigService(TimberZ plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration getBlocksConfig() {
        return getCustomConfig("blocks");
    }

    public FileConfiguration getCustomConfig(String fileName) {
        File configFile = new File(plugin.getDataFolder(),  fileName+ ".yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(fileName + ".yml", false);
        }

        return YamlConfiguration.loadConfiguration(configFile);
    }

    public void initConfigs() {
        getBlocksConfig();
    }


}
