package io.github.millymilo00.milFunny.listeners;

import io.github.millymilo00.milFunny.utils.UtilItemMark;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;

public class ListenerInteractCompass implements Listener {

    private final HashSet<Player> markedPlayers;

    public ListenerInteractCompass(HashSet<Player> markedPlayers) {
        this.markedPlayers = markedPlayers;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!Material.COMPASS.equals(event.getItem().getType()) || !event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasLore() || !event.getItem().getItemMeta().getLore().contains("t")) {
            return;
        }
        Location shortestDist = null;
        Player player = event.getPlayer();
        shortestDist = UtilItemMark.getShortestDist(player.getLocation(), markedPlayers);
        ItemMeta itemMeta = event.getItem().getItemMeta();
        if (shortestDist == null) {
            itemMeta.setDisplayName(ChatColor.RED + "Marked Player Locator");
            player.sendMessage(ChatColor.YELLOW + "There are no players with marked items");
            return;
        }
        player.setCompassTarget(shortestDist);
        itemMeta.setDisplayName(ChatColor.AQUA + "Marked Player Locator");
    }
}
