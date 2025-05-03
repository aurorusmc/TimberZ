package com.zetaplugins.timberz.commands.maincommand;

import com.zetaplugins.timberz.TimberZ;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MainTabCompleter implements TabCompleter {
    private final TimberZ plugin;

    public MainTabCompleter(TimberZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return switch (args.length) {
            case 1 -> getFirstArgOptions(sender, args);
            default -> List.of();
        };
    }

    public List<String> getFirstArgOptions(CommandSender sender, String[] args) {
        List<String> availableOptions = new ArrayList<>();

        if ("help".startsWith(args[0].toLowerCase()) || args[0].equalsIgnoreCase("help"))
            availableOptions.add("help");

        if (sender.hasPermission("serverlinksz.admin")) {
            List<String> adminCommands = List.of("reload");
            for (String adminCommand : adminCommands) {
                if (adminCommand.startsWith(args[0].toLowerCase()) || args[0].equalsIgnoreCase(adminCommand)) {
                    availableOptions.add(adminCommand);
                }
            }
        }

        return availableOptions;
    }
}
