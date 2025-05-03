package com.zetaplugins.timberz.listener;

import com.zetaplugins.timberz.TimberZ;
import com.zetaplugins.timberz.service.PlayerStateService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class AxeEquipListener implements Listener {
    private final TimberZ plugin;
    private final PlayerStateService playerStateService;

    public AxeEquipListener(TimberZ plugin, PlayerStateService playerStateService) {
        this.plugin = plugin;
        this.playerStateService = playerStateService;
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) return;

        player.sendMessage(plugin.getMessageService().getAndFormatMsg(
                true,
                "testmsg",
                "&7This is a test message with an %ac%accent &7color."
        ));

        ItemStack main = event.getMainHandItem();
        ItemStack off = event.getOffHandItem();

        if (isAxe(main) || isAxe(off)) {
            event.setCancelled(true);
            playerStateService.toggleTimberState(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack cursor = event.getCursor();

        if (event.getSlot() == 40 && isAxe(cursor)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack dragged = event.getOldCursor();
        if (!isAxe(dragged)) return;

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

    private boolean isAxe(ItemStack item) {
        return item != null && item.getType() != null && item.getType().name().endsWith("_AXE");
    }
}