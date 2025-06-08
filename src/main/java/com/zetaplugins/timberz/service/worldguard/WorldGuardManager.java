package com.zetaplugins.timberz.service.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.zetaplugins.timberz.TimberZ;
import org.bukkit.entity.Player;

public final class WorldGuardManager {
    private StateFlag TIMBER_FLAG;

    public WorldGuardManager() {
        registerFlags();
    }

    private void registerFlags() {
        StateFlag timberFlag = new TimberFlag();
        WorldGuard.getInstance().getFlagRegistry().register(timberFlag);
        TIMBER_FLAG = timberFlag;
    }

    public StateFlag getTimberFlag() {
        return TIMBER_FLAG;
    }

    public static boolean checkTimberFlag(Player player, TimberZ plugin) {
        WorldGuardManager worldGuardManager = plugin.getWorldGuardManager();

        if (worldGuardManager == null) return true;

        com.sk89q.worldguard.LocalPlayer localPlayer = com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().wrapPlayer(player);
        com.sk89q.worldedit.util.Location loc = com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(player.getLocation());
        com.sk89q.worldguard.protection.regions.RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
        com.sk89q.worldguard.protection.regions.RegionQuery query = container.createQuery();

        com.sk89q.worldguard.protection.ApplicableRegionSet set = query.getApplicableRegions(loc);

        return set.testState(localPlayer, worldGuardManager.getTimberFlag());
    }
}
