package com.zetaplugins.timberz.handler;

import com.zetaplugins.timberz.TimberZ;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SaplingReplanter {

    private final TimberZ plugin;
    private static final Map<Material, Material> LOG_TO_SAPLING_MAP = new HashMap<>();

    static {
        LOG_TO_SAPLING_MAP.put(Material.OAK_LOG, Material.OAK_SAPLING);
        LOG_TO_SAPLING_MAP.put(Material.BIRCH_LOG, Material.BIRCH_SAPLING);
        LOG_TO_SAPLING_MAP.put(Material.SPRUCE_LOG, Material.SPRUCE_SAPLING);
        LOG_TO_SAPLING_MAP.put(Material.JUNGLE_LOG, Material.JUNGLE_SAPLING);
        LOG_TO_SAPLING_MAP.put(Material.ACACIA_LOG, Material.ACACIA_SAPLING);
        LOG_TO_SAPLING_MAP.put(Material.DARK_OAK_LOG, Material.DARK_OAK_SAPLING);
        LOG_TO_SAPLING_MAP.put(Material.MANGROVE_LOG, Material.MANGROVE_PROPAGULE);
        LOG_TO_SAPLING_MAP.put(Material.CHERRY_LOG, Material.CHERRY_SAPLING);

        // Add stripped logs mapping too
        LOG_TO_SAPLING_MAP.put(Material.STRIPPED_OAK_LOG, Material.OAK_SAPLING);
        LOG_TO_SAPLING_MAP.put(Material.STRIPPED_BIRCH_LOG, Material.BIRCH_SAPLING);
        LOG_TO_SAPLING_MAP.put(Material.STRIPPED_SPRUCE_LOG, Material.SPRUCE_SAPLING);
        LOG_TO_SAPLING_MAP.put(Material.STRIPPED_JUNGLE_LOG, Material.JUNGLE_SAPLING);
        LOG_TO_SAPLING_MAP.put(Material.STRIPPED_ACACIA_LOG, Material.ACACIA_SAPLING);
        LOG_TO_SAPLING_MAP.put(Material.STRIPPED_DARK_OAK_LOG, Material.DARK_OAK_SAPLING);
        LOG_TO_SAPLING_MAP.put(Material.STRIPPED_MANGROVE_LOG, Material.MANGROVE_PROPAGULE);
        LOG_TO_SAPLING_MAP.put(Material.STRIPPED_CHERRY_LOG, Material.CHERRY_SAPLING);
    }

    public SaplingReplanter(TimberZ plugin) {
        this.plugin = plugin;
    }

    /**
     * Identifies the type of tree and its planting pattern
     */
    public TreeInfo analyzeTreeType(Block sourceBlock, Set<Block> treeBlocks) {
        Material logType = sourceBlock.getType();
        Material saplingType = LOG_TO_SAPLING_MAP.get(logType);

        if (saplingType == null) {
            return null; // Unknown log type
        }

        // Find the base blocks (lowest Y-level logs)
        List<Block> baseBlocks = findBaseBlocks(treeBlocks);

        if (baseBlocks.isEmpty()) {
            return null; // Could not determine base
        }

        // Analyze pattern (single sapling or 2x2)
        boolean is2x2 = is2x2Pattern(baseBlocks);

        return new TreeInfo(saplingType, baseBlocks, is2x2);
    }

    /**
     * Schedules sapling replanting after tree has been felled
     */
    public void scheduleSaplingReplant(TreeInfo treeInfo) {
        // Wait a bit for all logs and leaves to be processed
        new BukkitRunnable() {
            @Override
            public void run() {
                if (treeInfo.is2x2Pattern) {
                    // Plant 2x2 pattern of saplings
                    plantSaplings2x2(treeInfo);
                } else {
                    // Plant single sapling at base
                    Block base = treeInfo.baseBlocks.get(0);
                    Block ground = base.getRelative(BlockFace.DOWN);
                    Block airBlock = ground.getRelative(BlockFace.UP);

                    // Check if we can place a sapling here
                    if (canPlaceSaplingAt(airBlock)) {
                        airBlock.setType(treeInfo.saplingType);
                    }
                }
            }
        }.runTaskLater(plugin, 60); // Schedule 3 seconds after tree felling
    }

    /**
     * Plants saplings in a 2x2 pattern for larger trees like dark oak or spruce
     */
    private void plantSaplings2x2(TreeInfo treeInfo) {
        if (treeInfo.baseBlocks.size() < 4) {
            return; // Not enough base blocks for 2x2 pattern
        }

        // Find the minimum X and Z coordinates
        int minX = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        for (Block base : treeInfo.baseBlocks) {
            minX = Math.min(minX, base.getX());
            minZ = Math.min(minZ, base.getZ());
        }

        // Get the ground level from the first base block
        int groundY = treeInfo.baseBlocks.get(0).getRelative(BlockFace.DOWN).getY();

        // Plant saplings in 2x2 pattern
        for (int xOffset = 0; xOffset < 2; xOffset++) {
            for (int zOffset = 0; zOffset < 2; zOffset++) {
                Location saplingLoc = new Location(
                        treeInfo.baseBlocks.get(0).getWorld(),
                        minX + xOffset,
                        groundY + 1, // One above ground
                        minZ + zOffset
                );

                Block airBlock = saplingLoc.getBlock();

                // Check if we can place a sapling here
                if (canPlaceSaplingAt(airBlock)) {
                    airBlock.setType(treeInfo.saplingType);
                }
            }
        }
    }

    /**
     * Finds the base blocks of the tree (lowest Y-level logs)
     */
    private List<Block> findBaseBlocks(Set<Block> treeBlocks) {
        // Find the minimum Y level
        int minY = Integer.MAX_VALUE;
        for (Block block : treeBlocks) {
            minY = Math.min(minY, block.getY());
        }

        // Collect all blocks at the minimum Y level
        List<Block> baseBlocks = new ArrayList<>();
        for (Block block : treeBlocks) {
            if (block.getY() == minY) {
                baseBlocks.add(block);
            }
        }

        return baseBlocks;
    }

    /**
     * Determines if the tree has a 2x2 pattern at its base
     */
    private boolean is2x2Pattern(List<Block> baseBlocks) {
        // Quick check - a 2x2 tree should have at least 4 base blocks
        if (baseBlocks.size() < 4) {
            return false;
        }

        // Check if the base logs form a 2x2 pattern
        Set<Integer> xCoords = new HashSet<>();
        Set<Integer> zCoords = new HashSet<>();

        for (Block block : baseBlocks) {
            xCoords.add(block.getX());
            zCoords.add(block.getZ());
        }

        // A 2x2 pattern should have exactly 2 X coordinates and 2 Z coordinates
        return xCoords.size() == 2 && zCoords.size() == 2;
    }

    /**
     * Checks if a sapling can be placed at the specified location
     */
    private boolean canPlaceSaplingAt(Block block) {
        // Check if the block is air or can be replaced
        if (!block.getType().isAir() && block.getType().isSolid() && block.getType() != Material.SNOW) {
            return false;
        }

        // Check if the block below can support a sapling
        Block below = block.getRelative(BlockFace.DOWN);
        Material belowType = below.getType();

        return belowType == Material.GRASS_BLOCK ||
                belowType == Material.DIRT ||
                belowType == Material.COARSE_DIRT ||
                belowType == Material.PODZOL ||
                belowType == Material.FARMLAND ||
                belowType == Material.ROOTED_DIRT;
    }

    /**
     * Container class to hold information about a tree
     */
    public static class TreeInfo {
        public final Material saplingType;
        public final List<Block> baseBlocks;
        public final boolean is2x2Pattern;

        public TreeInfo(Material saplingType, List<Block> baseBlocks, boolean is2x2Pattern) {
            this.saplingType = saplingType;
            this.baseBlocks = baseBlocks;
            this.is2x2Pattern = is2x2Pattern;
        }
    }
}