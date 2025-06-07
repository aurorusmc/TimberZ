package com.zetaplugins.timberz.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.zetaplugins.timberz.TimberZ;
import com.zetaplugins.timberz.service.MessageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public final class TimberZCommand {
    private final TimberZ plugin;

    public TimberZCommand(TimberZ plugin) {
        this.plugin = plugin;
    }

    public LiteralCommandNode<CommandSourceStack> buildRootCommand() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("timberz");

        root
                .requires(source -> source.getSender().hasPermission("customplugin.use"))
                .executes(this::executeVersion);

        root.then(Commands.literal("reload")
                .requires(source -> source.getSender().hasPermission("customplugin.admin"))
                .executes(this::executeReload));

        root.then(Commands.literal("help")
                .requires(source -> true)
                .executes(this::executeHelp));


        return root.build();
    }

    private int executeVersion(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendMessage(plugin.getMessageService().getAndFormatMsg(
                true,
                "versionMsg",
                "&7You are using version %ac%%version%",
                new MessageService.Replaceable<>(
                        "%version%",
                        plugin.getDescription().getVersion()
                )
        ));
        return Command.SINGLE_SUCCESS;
    }

    private int executeHelp(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendMessage(plugin.getMessageService().getAndFormatMsg(
                false,
                "help",
                "\\n &8<b>></b> <#00D26A><b><gradient:#00D26A:#00B24F>ServerLinksZ</gradient></b></#00D26A> <grey>v%version%</grey>\\n\\n <click:OPEN_URL:https://docs.zetaplugins.com/timberz>%ac%<u><b>Docs</b></u></click> <click:OPEN_URL:https://strassburger.org/discord>%ac%<u><b>Support Discord</b></u></click>\\n",
                new MessageService.Replaceable<>(
                        "%version%",
                        plugin.getDescription().getVersion()
                )
        ));
        return Command.SINGLE_SUCCESS;
    }

    private int executeReload(CommandContext<CommandSourceStack> context) {
        plugin.getConfigService().initConfigs();
        plugin.reloadConfig();
        plugin.getLocalizationService().reload();
        plugin.getTreeDetectionService().fetchLogToLeaveMap();
        plugin.getPlayerStateService().cleanupAll();

        context.getSource().getSender().sendMessage(plugin.getMessageService().getAndFormatMsg(
                true,
                "reloadMsg",
                "&7Successfully reloaded the plugin!"
        ));
        return Command.SINGLE_SUCCESS;
    }
}