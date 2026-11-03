package io.github.millymilo00.milFunny.commands;

import io.github.millymilo00.milFunny.MilFunny;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public class CommandVote implements CommandExecutor {

    final MilFunny plugin;

    public CommandVote(MilFunny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        int vote = 0;
        String votes = null;
        if (player.getStatistic(Statistic.CHEST_OPENED) < 10 || player.getStatistic(Statistic.MOB_KILLS) < 30 || player.getStatistic(Statistic.PLAY_ONE_MINUTE) < 216000 && player.getStatistic(Statistic.CRAFT_ITEM) < 10) {
//            player.sendMessage("You are not active enough to vote!");
//            return true;
        }

        if (args.length < 1) { return false; }
        if ("yes".equals(args[1])) { vote = 1; }
        else if ("no".equals(args[1])) { vote = -1; }
        else { return false; }

        try {
            votes = plugin.database.getPlayerVotes(args[0]);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (votes == null) {
            player.sendMessage(ChatColor.RED + "You cannot vote for that player.");
            return true;
        }
        if (votes.contains(player.getName())) {
            sender.sendMessage(ChatColor.RED + "You've already voted yes for this person!");
            return true;
        }

        String[] votesNames = votes.split(",");
        sender.sendMessage(ChatColor.YELLOW + "Vote added");
        Player candidate = plugin.getServer().getPlayer(args[0]);
        // Update player op status
        if (votesNames.length == 6 && vote == 1) {
            candidate.sendMessage(ChatColor.AQUA + "You have been voted back into Op");
            candidate.setOp(true);
        } else if (votesNames.length == 6 && vote == -1) {
            candidate.sendMessage(ChatColor.DARK_RED + "You have been voted out of Op");
            candidate.setOp(false);
        }

        try {
            plugin.database.setPlayerVotes(args[0], votes+","+player.getName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
