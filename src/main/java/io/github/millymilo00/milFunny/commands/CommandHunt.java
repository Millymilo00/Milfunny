package io.github.millymilo00.milFunny.commands;

import io.github.millymilo00.milFunny.utils.UtilItemMark;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CommandHunt implements CommandExecutor {

    private final HashSet<Player> markedPlayers;

    public CommandHunt(HashSet<Player> markedPlayers) {
        this.markedPlayers = markedPlayers;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only!");
            return true;
        }
        Player player = (Player) sender;
        Location shortestDist = UtilItemMark.getShortestDist(player.getLocation(), markedPlayers);
        if (shortestDist == null) {
            sender.sendMessage(ChatColor.YELLOW + "There are no players with marked items");
            return true;
        }
        ItemStack compass = new ItemStack(Material.COMPASS);
        player.setCompassTarget(shortestDist);
        ItemMeta itemMeta = compass.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "Marked Player Locator");
        itemMeta.setLore(Arrays.asList(new String[]{"Interact while holding to update", "t"}));
        compass.setItemMeta(itemMeta);
        player.getInventory().addItem(compass);
        return true;
    }
}
