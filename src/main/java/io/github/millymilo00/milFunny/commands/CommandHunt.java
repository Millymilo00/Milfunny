package io.github.millymilo00.milFunny.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

public class CommandHunt implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only!");
            return true;
        }
        Player player = (Player) sender;
        HashSet<Player> holdingMarks = new HashSet<>();
        Location shortestDist = null;
        for (Player wrldPlr : player.getWorld().getPlayers()) {
            for (ItemStack item : wrldPlr.getInventory().getContents()) {
                if (item.getItemMeta().getLore().contains("Admin Marked")) {
                    holdingMarks.add(wrldPlr);
                    shortestDist = wrldPlr.getLocation();
                }
            }
        }
        if (holdingMarks.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No player is holding any marked items");
            return true;
        }
        for (Player plr : holdingMarks) {
            if (player.getLocation().distance(plr.getLocation()) < player.getLocation().distance(shortestDist)) {
                shortestDist = plr.getLocation();
            }
        }
        ItemStack compass = new ItemStack(Material.COMPASS);
        player.setCompassTarget(shortestDist);
        compass.getItemMeta().setDisplayName(ChatColor.AQUA + "Marked Player Locator");
        compass.getItemMeta().getLore().add("Currently targeting a player");
        player.getInventory().addItem(compass);
        return true;
    }
}
