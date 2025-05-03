package com.zetaplugins.timberz.commands.maincommand;

import com.zetaplugins.timberz.TimberZ;
import com.zetaplugins.timberz.commands.SubCommand;
import com.zetaplugins.timberz.commands.maincommand.subcommands.HelpSubCommand;
import com.zetaplugins.timberz.commands.maincommand.subcommands.ReloadSubCommand;
import com.zetaplugins.timberz.service.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MainCommandHandler implements CommandExecutor {
    private final TimberZ plugin;
    private final Map<String, SubCommand> commands = new HashMap<>();

    public MainCommandHandler(TimberZ plugin) {
        this.plugin = plugin;

        commands.put("help", new HelpSubCommand(plugin));
        commands.put("reload", new ReloadSubCommand(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sendVersionMessage(sender);
            return true;
        }

        SubCommand subCommand = commands.get(args[0]);

        if (subCommand == null) {
            sendVersionMessage(sender);
            return true;
        }

        return subCommand.execute(sender, args);
    }

    private void sendVersionMessage(CommandSender sender) {
        sender.sendMessage(plugin.getMessageService().getAndFormatMsg(
                true,
                "versionMsg",
                "&7You are using version %ac%%version%",
                new MessageService.Replaceable<>(
                        "%version%",
                        plugin.getDescription().getVersion()
                )
        ));
    }
}
