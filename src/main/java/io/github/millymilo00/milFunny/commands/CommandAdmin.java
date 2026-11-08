package io.github.millymilo00.milFunny.commands;

import io.github.millymilo00.milFunny.MilFunny;
import io.github.millymilo00.milFunny.utils.UtilItemMark;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class CommandAdmin implements CommandExecutor {

    private final MilFunny plugin;

    public CommandAdmin(MilFunny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if ("mark".equals(args[0])) {
                return markItem(sender);
            }
        }
        else if (args.length == 3) {
            if ("opme".equals(args[0])) {
                return manageTables(sender, args, "opMePlrs");
            }
            else if ("candidate".equals(args[0])) {
                return manageTables(sender, args, "candidates");
            }
        }
        sender.sendMessage("§e/milfunny /mf admin commands - key: §3command §b<required> §2[option1|option2]"
        + "\n§3mf opme §2[add|remove] §b<player> §r- Add or remove players that can use the /opme command"
        + "\n§3mf candidate §2[add|remove] §b<player> §r- Add or remove players who can be voted for op"
        + "\n§3mf mark §r- Mark the held item with the Admin Mark");
        return true;
    }

    private boolean manageTables(CommandSender sender, String[] args, String table) {
        if ("add".equals(args[1])) {
            try {
                plugin.database.addPlayer(args[2], table);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("Player added");
        }
        else if ("remove".equals(args[1])) {
            try {
                if (plugin.database.removePlayer(args[2], table)) {
                    sender.sendMessage("Player removed");
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Player not removed (likely not in list)");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    private boolean markItem(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only!");
            return true;
        }
        Player player = (Player) sender;
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (Material.AIR.equals(heldItem.getType())) {
            sender.sendMessage(ChatColor.RED + "You need to be holding an item to run this command!");
            return true;
        }
        player.getInventory().setItemInMainHand(UtilItemMark.markItem(heldItem));
        sender.sendMessage("Item marked");
        return true;
    }
}
