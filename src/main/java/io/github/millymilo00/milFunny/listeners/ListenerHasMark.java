package io.github.millymilo00.milFunny.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

public class ListenerHasMark implements Listener {

    private final HashSet<Player> markedPlayers;

    public ListenerHasMark(HashSet<Player> markedPlayers) {
        this.markedPlayers = markedPlayers;
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        updateMarks(event.getEntity());
    }

    @EventHandler
    public void onItemInventory(InventoryClickEvent event) {
        updateMarks(event.getWhoClicked());
    }

    private void updateMarks(Entity entity) {
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;
        for (ItemStack item : player.getInventory()) {
            if (!Material.AIR.equals(item.getType()) && item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().contains("Admin Marked")) {
                markedPlayers.add((Player) entity);
                return;
            }
        }
        // No markedItems were found
        markedPlayers.remove(player);
    }
}
