package io.github.millymilo00.milFunny.commands;

import io.github.millymilo00.milFunny.MilFunny;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class CommandAddOpme implements CommandExecutor {
    final MilFunny plugin;

    public CommandAddOpme(MilFunny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        try {
            plugin.database.addPlayer(args[0], "opMePlrs");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage("Player added");
        return true;
    }
}
