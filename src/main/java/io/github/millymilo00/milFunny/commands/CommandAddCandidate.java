package io.github.millymilo00.milFunny.commands;

import io.github.millymilo00.milFunny.MilFunny;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.HashMap;

public class CommandAddCandidate implements CommandExecutor {

    private final MilFunny plugin;

    public CommandAddCandidate(MilFunny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        try {
            plugin.database.addPlayer(args[0], "candidates");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage("Candidate added");
        plugin.tempOps.add(args[0]);
        return true;
    }
}
