package io.github.millymilo00.milFunny.commands;

import io.github.millymilo00.milFunny.MilFunny;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.Map;

public class CommandRemoveCandidate implements CommandExecutor {

    private final MilFunny plugin;

    public CommandRemoveCandidate(MilFunny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        try {
             if (plugin.database.removePlayer(args[0], "candidates")) {
                sender.sendMessage("Candidate removed");
                plugin.tempOps.remove(args[0]);
             }
             else {
                 sender.sendMessage(ChatColor.RED + "Candidate not removed (likely not in list)");
             }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
