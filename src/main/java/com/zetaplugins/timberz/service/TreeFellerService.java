package com.zetaplugins.timberz.service;

import com.zetaplugins.timberz.TimberZ;
import com.zetaplugins.timberz.handler.SaplingReplanter;
import com.zetaplugins.timberz.handler.TreeAnimationHandler;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.zetaplugins.timberz.service.MaterialTypeChecks.isLeafBlock;

public final class TreeFellerService {
    private final TimberZ plugin;
    private final TreeAnimationHandler animationHandler;
    private final SaplingReplanter saplingReplanter;
    private final Random random = new Random();

    public TreeFellerService(TimberZ plugin) {
        this.plugin = plugin;
        this.animationHandler = new TreeAnimationHandler(plugin);
        this.saplingReplanter = new SaplingReplanter(plugin);
    }

    /**
     * Handles the tree felling process
     */
    public void fellTree(Player player, Block sourceBlock, Set<Block> treeBlocks, ItemStack tool, int durabilityCost) {
        Material logType = sourceBlock.getType();

        // Get corresponding leaf type
        Material leafType = plugin.getTreeDetectionService().getLeafType(logType);

        // Store information about tree type for replanting
        SaplingReplanter.TreeInfo treeInfo = saplingReplanter.analyzeTreeType(sourceBlock, treeBlocks);


        // Break logs with animation (one by one with delay)
        breakTreeWithAnimation(player, treeBlocks, tool, durabilityCost);



        boolean shouldReplant = plugin.getConfig().getBoolean("replant");

        // Schedule sapling replanting
        if (treeInfo != null && shouldReplant) {
            saplingReplanter.scheduleSaplingReplant(treeInfo);
        }
    }


    /**
     * Calculate the Euclidean distance between two blocks
     */
    private double distanceBetween(Block block1, Block block2) {
        double dx = block1.getX() - block2.getX();
        double dy = block1.getY() - block2.getY();
        double dz = block1.getZ() - block2.getZ();
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }


    /**
     * Breaks the tree logs with animation and applies durability to tool
     */
    private void breakTreeWithAnimation(Player player, Set<Block> treeBlocks, ItemStack tool, int durabilityCost) {
        // Apply durability damage to the tool
        applyToolDamage(tool, durabilityCost);

        // Break blocks with animation
        animationHandler.animateTreeFelling(player, treeBlocks);
    }

    /**
     * Apply damage to the tool
     */
    private void applyToolDamage(ItemStack tool, int durabilityCost) {
        if (tool.getItemMeta() instanceof Damageable) {
            Damageable meta = (Damageable) tool.getItemMeta();
            int newDamage = meta.getDamage() + durabilityCost;
            meta.setDamage(newDamage);
            tool.setItemMeta((ItemMeta) meta);
        }
    }
}