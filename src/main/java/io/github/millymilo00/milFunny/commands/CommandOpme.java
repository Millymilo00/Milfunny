package io.github.millymilo00.milFunny.commands;

import io.github.millymilo00.milFunny.MilFunny;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.SQLException;
import java.util.List;

public class CommandOpme implements CommandExecutor {

    final MilFunny plugin;

    public CommandOpme(MilFunny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            try {
                if (plugin.database.playerExists(player.getName(), "opMePlrs")) {
                    player.setOp(true);
                    plugin.getLogger().info(player.getName() + " is now operator, brace yourselves.");
                    player.sendMessage(ChatColor.DARK_RED + "enjoy op lmao");
                    plugin.database.removePlayer(player.getName(), "opMePlrs");
                    plugin.tempOps.add(player.getName());

                    BukkitScheduler scheduler = Bukkit.getScheduler();
                    scheduler.runTaskLater(plugin, () -> {
                        player.setOp(false);
                        plugin.getLogger().info(player.getName() + " is no longer operator, the storm is over.");
                        plugin.tempOps.remove(player.getName());
                        player.sendMessage(ChatColor.DARK_RED + "you had your fun, no more op");
                    }, 72000L /*<-- 72000L is 1 hour I hope */);
                }
                else {
                    player.sendMessage(ChatColor.DARK_RED + "no");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            sender.sendMessage("Only players can run this command");
        }
        return true;
    }
}
