package com.zetaplugins.timberz.listener;

import com.zetaplugins.timberz.TimberZ;
import com.zetaplugins.timberz.service.PlayerStateService;
import com.zetaplugins.timberz.service.TreeDetection;
import com.zetaplugins.timberz.service.TreeFellerService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Set;

public final class TreeBreakListener implements Listener {

    private final TimberZ plugin;
    private final TreeFellerService TreeFellerService;
    private final PlayerStateService playerStateService;

    public TreeBreakListener(TimberZ plugin) {
        this.plugin = plugin;
        this.TreeFellerService = plugin.getTreeFellerService();
        this.playerStateService = plugin.getPlayerStateService();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        Material blockType = brokenBlock.getType();
        Player player = event.getPlayer();

        if (player.isSneaking()) return;
        if (!playerStateService.isTimberEnabled(player)) return;

        // Check if block is a log and player is not in creative mode
        if (TreeDetection.containsLog(blockType)) { //  && player.getGameMode() != GameMode.CREATIVE
            ItemStack handItem = player.getInventory().getItemInMainHand();

            // Check if the player is using an axe
            if (isAxe(handItem.getType())) {
                // Identify tree structure
                Set<Block> treeBlocks = TreeDetection.identifyTreeStructure(brokenBlock);

                // If we found a valid tree
                if (!treeBlocks.isEmpty()) {
                    // Calculate durability cost
                    int logsCount = treeBlocks.size();
                    int durabilityCost = calculateDurabilityCost(logsCount);

                    // Check if tool has enough durability
                    if (hasEnoughDurability(handItem, durabilityCost)) {
                        // Cancel the original event
                        event.setCancelled(true);

                        // Process the tree felling
                        TreeFellerService.fellTree(player, brokenBlock, treeBlocks, handItem, durabilityCost);
                    }
                    // If not enough durability, just let the vanilla event proceed
                }
            }
        }
    }



    private boolean isAxe(Material material) {
        return material == Material.WOODEN_AXE ||
                material == Material.STONE_AXE ||
                material == Material.IRON_AXE ||
                material == Material.GOLDEN_AXE ||
                material == Material.DIAMOND_AXE ||
                material == Material.NETHERITE_AXE;
    }

    private int calculateDurabilityCost(int logsCount) {
        // Apply 10% discount to durability cost
        return (int) Math.ceil(logsCount * 0.9);
    }

    private boolean hasEnoughDurability(ItemStack tool, int durabilityCost) {
        if (tool.getItemMeta() instanceof Damageable) {
            Damageable meta = (Damageable) tool.getItemMeta();
            int maxDurability = tool.getType().getMaxDurability();
            int currentDamage = meta.getDamage();
            int remainingDurability = maxDurability - currentDamage;

            // Check if we would have at least 10 durability left after the operation
            return remainingDurability - durabilityCost >= 10;
        }
        return false;
    }
}