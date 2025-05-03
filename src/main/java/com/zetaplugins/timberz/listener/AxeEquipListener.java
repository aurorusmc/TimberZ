package com.zetaplugins.timberz.listener;

import com.zetaplugins.timberz.TimberZ;
import com.zetaplugins.timberz.service.PlayerStateService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static com.zetaplugins.timberz.service.MaterialTypeChecks.isAxe;
import static com.zetaplugins.timberz.service.MaterialTypeChecks.isValidAxe;

public final class AxeEquipListener implements Listener {
    private final TimberZ plugin;
    private final PlayerStateService playerStateService;

    public AxeEquipListener(TimberZ plugin) {
        this.plugin = plugin;
        this.playerStateService = plugin.getPlayerStateService();
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) return;

        if (!player.hasPermission("timberz.useTimber") && plugin.getConfig().getBoolean("toggleTimber")) {
            player.sendMessage(plugin.getMessageService().getAndFormatMsg(
                    false,
                    "noTimberPermissionError",
                    "&cYou do not have permission to use TimberZ!"
            ));
            return;
        }

        if (!plugin.getConfig().getBoolean("toggleTimber")) return;

        ItemStack main = event.getMainHandItem();
        ItemStack off = event.getOffHandItem();

        if (isValidAxe(main, plugin) || isValidAxe(off, plugin)) {
            event.setCancelled(true);
            playerStateService.toggleTimberState(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player) || !plugin.getConfig().getBoolean("toggleTimber")) return;

        ItemStack cursor = event.getCursor();

        if (event.getSlot() == 40 && isAxeFromItemStack(cursor)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || !plugin.getConfig().getBoolean("toggleTimber")) return;

        ItemStack dragged = event.getOldCursor();
        if (!isAxeFromItemStack(dragged)) return;

        if (event.getRawSlots().contains(40)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMessageService().getAndFormatMsg(
                    false,
                    "cannotDragAxesIntoOffHand",
                    "&cYou cannot drag axes into your offhand!"
            ));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerStateService.cleanupPlayer(player);
    }

    private boolean isAxeFromItemStack(ItemStack item) {
        return isAxe(item.getType(), plugin.getConfigService().getBlocksConfig());
    }
}