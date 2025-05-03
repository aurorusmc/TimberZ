package com.zetaplugins.timberz.service;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public final class MaterialTypeChecks {
    private MaterialTypeChecks() {}

    public static boolean isLeafBlock(Material material, FileConfiguration blocksConfig) {
        List<String> leafBlocks = blocksConfig.getStringList("leafBlocks");

        for (String leafBlock : leafBlocks) {
            if (material.toString().equalsIgnoreCase(leafBlock)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAxe(Material material, FileConfiguration blocksConfig) {
        List<String> axeBlocks = blocksConfig.getStringList("axes");

        for (String axeBlock : axeBlocks) {
            if (material.toString().equalsIgnoreCase(axeBlock)) {
                return true;
            }
        }

        return false;
    }
}
