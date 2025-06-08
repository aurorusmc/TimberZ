package com.zetaplugins.timberz.service.papi;

import com.zetaplugins.timberz.TimberZ;
import com.zetaplugins.timberz.service.PlayerStateService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PapiExpansion extends PlaceholderExpansion {
    private final TimberZ plugin;
    private PlayerStateService playerStateService;

    public PapiExpansion(TimberZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "ZetaPlugins";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "timberz";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null || player.getPlayer() == null) return "PlayerNotFound";

        if (identifier.contentEquals("can_timber")) {
            if (!(player instanceof Player onlinePlayer)) {
                return "PlayerNotFound";
            }
            return plugin.getPlayerStateService().isAllowedToTimber(onlinePlayer) + "";
        }

        return "InvalidPlaceholder";
    }
}
