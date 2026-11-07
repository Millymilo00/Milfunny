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
import java.util.HashSet;

// this is the worst code i've ever created
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
        boolean voteYes = true;
        String[] votes = null;
        if (player.getStatistic(Statistic.CHEST_OPENED) < 10 || player.getStatistic(Statistic.MOB_KILLS) < 30 || player.getStatistic(Statistic.PLAY_ONE_MINUTE) < 216000 && player.getStatistic(Statistic.CRAFT_ITEM) < 10) {
//            player.sendMessage("You are not active enough to vote!");
//            return true;
        }

        if (args.length < 2) { return false; }
        if ("yes".equals(args[1])) { voteYes = true; }
        else if ("no".equals(args[1])) { voteYes = false; }
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

        HashSet<String> votesYes = new HashSet<>();
        HashSet<String> votesNo = new HashSet<>();
        votesYes.addAll(Arrays.asList(votes[0].split(",")));
        votesNo.addAll(Arrays.asList(votes[1].split(",")));

        if (voteYes) {
            if (votesYes.contains(player.getName())) {
                player.sendMessage(ChatColor.RED + "You've already voted yes for that candidate!");
                return true;
            }
            if (votesNo.contains(player.getName())) {
                votesNo.remove(player.getName());
            }
            votesYes.add(player.getName());
        }
        else {
            if (votesNo.contains(player.getName())) {
                player.sendMessage(ChatColor.RED + "You've already voted no for that candidate!");
                return true;
            }
            if (votesYes.contains(player.getName())) {
                votesYes.remove(player.getName());
            }
            votesNo.add(player.getName());
        }
        sender.sendMessage(ChatColor.YELLOW + "Vote added");
        try {
            plugin.database.setPlayerVotes(args[0], String.join(",", votesYes), String.join(",", votesNo));
            plugin.database.applyVotes();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
