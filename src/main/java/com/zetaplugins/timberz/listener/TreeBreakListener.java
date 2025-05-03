package com.zetaplugins.timberz.listener;

import com.zetaplugins.timberz.TimberZ;
import com.zetaplugins.timberz.service.PlayerStateService;
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

import static com.zetaplugins.timberz.service.MaterialTypeChecks.isAxe;

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
        if (plugin.getTreeDetectionService().containsLog(blockType)) { //  && player.getGameMode() != GameMode.CREATIVE
            ItemStack handItem = player.getInventory().getItemInMainHand();

            // Check if the player is using an axe
            if (isAxe(handItem.getType(), plugin.getConfigService().getBlocksConfig())) {
                // Identify tree structure
                Set<Block> treeBlocks = plugin.getTreeDetectionService().identifyTreeStructure(brokenBlock);

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
                    } else {
                        player.sendMessage(plugin.getMessageService().getAndFormatMsg(
                                false,
                                "notEnoughDurability",
                                "&cYou don't have enough durability left on your axe!"
                        ));
                    }
                    // If not enough durability, just let the vanilla event proceed
                }
            }
        }
    }

    private int calculateDurabilityCost(int logsCount) {
        double multiplier = plugin.getConfig().getDouble("durabilityMultiplier", 1.0);
        return (int) Math.ceil(logsCount * multiplier);
    }

    private boolean hasEnoughDurability(ItemStack tool, int durabilityCost) {
        if (tool.getItemMeta() instanceof Damageable meta) {
            int maxDurability = tool.getType().getMaxDurability();
            int currentDamage = meta.getDamage();
            int remainingDurability = maxDurability - currentDamage;

            int minDurability = plugin.getConfig().getInt("minDurability", 10);
            // Check if we would have at least 10 durability left after the operation
            return remainingDurability - durabilityCost >= minDurability;
        }
        return false;
    }
}