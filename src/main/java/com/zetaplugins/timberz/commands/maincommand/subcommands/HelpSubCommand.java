package com.zetaplugins.timberz.commands.maincommand.subcommands;

import com.zetaplugins.timberz.TimberZ;
import com.zetaplugins.timberz.commands.SubCommand;
import com.zetaplugins.timberz.service.MessageService;
import org.bukkit.command.CommandSender;

public class HelpSubCommand implements SubCommand {
    private final TimberZ plugin;

    public HelpSubCommand(TimberZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage(plugin.getMessageService().getAndFormatMsg(
                false,
                "help",
                "\\n &8<b>></b> <#00D26A><b><gradient:#00D26A:#00B24F>ServerLinksZ</gradient></b></#00D26A> <grey>v%version%</grey>\\n\\n <click:OPEN_URL:https://docs.zetaplugins.com/timberz>%ac%<u><b>Docs</b></u></click> <click:OPEN_URL:https://strassburger.org/discord>%ac%<u><b>Support Discord</b></u></click>\\n",
                new MessageService.Replaceable<>(
                        "%version%",
                        plugin.getDescription().getVersion()
                )
        ));
        return false;
    }

    @Override
    public String getUsage() {
        return "/timberz help";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return true;
    }
}
