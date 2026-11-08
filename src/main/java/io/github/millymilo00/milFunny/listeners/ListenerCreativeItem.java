package io.github.millymilo00.milFunny.listeners;

import io.github.millymilo00.milFunny.MilFunny;
import io.github.millymilo00.milFunny.utils.UtilItemMark;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;

public class ListenerCreativeItem implements Listener {

    private final MilFunny plugin;

    public ListenerCreativeItem(MilFunny plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNewCreativeItem(InventoryCreativeEvent event) {
        // Sadly this marks EVERY single item an admin interacts with within their inventory
        if (!plugin.tempOps.contains(event.getWhoClicked().getName()) || Material.AIR.equals(event.getCursor().getType())) {
            return;
        }
        event.setCursor(UtilItemMark.markItem(event.getCursor()));
    }
}
