package io.github.millymilo00.milFunny.commands;

import io.github.millymilo00.milFunny.MilFunny;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.List;

public class CommandRemoveOpme implements CommandExecutor {
    final MilFunny plugin;

    public CommandRemoveOpme(MilFunny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        try {
            if (plugin.database.removePlayer(args[0], "opMePlrs")) {
                sender.sendMessage("Player removed");
            }
            else {
                sender.sendMessage(ChatColor.RED + "Player not removed (likely not in list)");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
