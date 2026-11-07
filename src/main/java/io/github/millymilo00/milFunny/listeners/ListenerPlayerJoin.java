package io.github.millymilo00.milFunny.listeners;

import io.github.millymilo00.milFunny.MilFunny;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class ListenerPlayerJoin implements Listener {
    private final MilFunny plugin;

    public ListenerPlayerJoin(MilFunny plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            plugin.database.applyVotes();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
