package com.zetaplugins.timberz.service;

import com.zetaplugins.timberz.TimberZ;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

        if (plugin.getConfig().getBoolean("restrictAxeModelData")) {
            if (!item.getItemMeta().hasCustomModelData()) return false;
            int itemCustomModelData = item.getItemMeta().getCustomModelData();
            List<Integer> allowedModelData = plugin.getConfig().getIntegerList("allowedModelData");
            if (!allowedModelData.contains(itemCustomModelData)) return false;
        }

        if (plugin.getConfig().getBoolean("requireCustomEnchant")) {
            Map<Enchantment, Integer> enchants = item.getEnchantments();
            Set<String> enchantKeys = enchants.keySet().stream()
                .map(Enchantment::getKey)
                .map(Object::toString)
                .collect(Collectors.toSet());

            if (!enchantKeys.contains("lifestealz:timber")) return false;
        }

        return true;
    }
}
