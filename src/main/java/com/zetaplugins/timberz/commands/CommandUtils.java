package com.zetaplugins.timberz.commands;

import com.zetaplugins.timberz.TimberZ;
import com.zetaplugins.timberz.service.MessageService;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public final class CommandUtils {
    /**
     * Throws a usage error message to the sender.
     * @param sender Command sender
     * @param usage Usage string
     */
    public static void throwUsageError(CommandSender sender, String usage, TimberZ plugin) {
        Component msg = plugin.getMessageService().getAndFormatMsg(
                false,
                "usageError",
                "&cUsage: %usage%",
                new MessageService.Replaceable<>("%usage%", usage)
        );
        sender.sendMessage(msg);
    }

    /**
     * Throws a permission error message to the sender.
     * @param sender Command sender
     */
    public static void throwPermissionError(CommandSender sender, TimberZ plugin) {
        Component msg = plugin.getMessageService().getAndFormatMsg(
                false,
                "noPermissionError",
                "&cYou don't have permission to use this!"
        );
        sender.sendMessage(msg);
    }
}
