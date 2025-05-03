package com.zetaplugins.timberz.service;

import com.zetaplugins.timberz.TimberZ;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

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

    public static boolean isValidAxe(ItemStack item, TimberZ plugin) {
        if (item == null || !isAxe(item.getType(), plugin.getConfigService().getBlocksConfig())) return false;
        if (!plugin.getConfig().getBoolean("restrictAxes")) return true;
        List<Integer> allowedModelData = plugin.getConfig().getIntegerList("allowedModelData");
        if (!item.getItemMeta().hasCustomModelData()) return false;
        int itemCustomModelData = item.getItemMeta().getCustomModelData();
        System.out.println("Item Custom Model Data: " + itemCustomModelData);

        return allowedModelData.contains(itemCustomModelData);
    }
}
