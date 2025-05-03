package com.zetaplugins.timberz.service.registrars;

import com.zetaplugins.timberz.TimberZ;
import com.zetaplugins.timberz.commands.maincommand.MainCommandHandler;
import com.zetaplugins.timberz.commands.maincommand.MainTabCompleter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public final class CommandRegistrar {
    private final TimberZ plugin;

    public CommandRegistrar(TimberZ plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers all commands
     */
    public void registerCommands() {
        registerCommand("timberz", new MainCommandHandler(plugin), new MainTabCompleter(plugin));
    }

    /**
     * Registers a command
     *
     * @param name The name of the command
     * @param executor The executor of the command
     * @param tabCompleter The tab completer of the command
     */
    private void registerCommand(String name, CommandExecutor executor, TabCompleter tabCompleter) {
        PluginCommand command = plugin.getCommand(name);

        if (command != null) {
            command.setExecutor(executor);
            command.setTabCompleter(tabCompleter);
            command.permissionMessage(plugin.getMessageService().getAndFormatMsg(
                    false,
                    "noPermissionError",
                    "&cYou don't have permission to use this!"
            ));
        }
    }
}
