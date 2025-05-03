package com.zetaplugins.timberz.commands.maincommand.subcommands;

import com.zetaplugins.timberz.TimberZ;
import com.zetaplugins.timberz.commands.CommandUtils;
import com.zetaplugins.timberz.commands.SubCommand;
import org.bukkit.command.CommandSender;

public final class ReloadSubCommand implements SubCommand {
    private final TimberZ plugin;

    public ReloadSubCommand(TimberZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            CommandUtils.throwPermissionError(sender, plugin);
            return false;
        }

        plugin.getConfigService().initConfigs();
        plugin.reloadConfig();
        plugin.getLocalizationService().reload();
        plugin.getTreeDetectionService().fetchLogToLeaveMap();
        sender.sendMessage(plugin.getMessageService().getAndFormatMsg(
                true,
                "reloadMsg",
                "&7Successfully reloaded the plugin!"
        ));
        return false;
    }

    @Override
    public String getUsage() {
        return "/timberz reload";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("timberz.admin");
    }
}
