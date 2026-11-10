package io.github.millymilo00.milFunny.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class UtilItemMark {
    public static ItemStack markItem(ItemStack input) {
        ItemMeta meta = input.getItemMeta();
        if (!input.hasItemMeta()) {
            meta = Bukkit.getItemFactory().getItemMeta(Material.STONE);
        }
        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            lore.add("Admin Marked");
            meta.setLore(lore);
        }
        else {
            meta.setLore(Collections.singletonList("Admin Marked"));
        }
        input.setItemMeta(meta);
        return input;
    }
    public static Location getShortestDist(Location plrLoc, HashSet<Player> markedPlayers) {
        Location shortestDist = null;
        if (markedPlayers.isEmpty()) {
            return null;
        }
        for (Player plr : markedPlayers) {
            if (plrLoc.distance(plr.getLocation()) < plrLoc.distance(shortestDist)) {
                shortestDist = plr.getLocation();
            }
        }
        return shortestDist;
    }
}
