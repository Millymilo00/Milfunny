package io.github.millymilo00.milFunny.listeners;

import io.github.millymilo00.milFunny.MilFunny;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ListenerCommand implements Listener {

    final MilFunny plugin;

    public ListenerCommand(MilFunny plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerUseCommand(PlayerCommandPreprocessEvent event) {
        if (!plugin.tempOps.contains(event.getPlayer().getName())) {
            return;
        }

        String inCommand = event.getMessage().split(" ")[0];
        inCommand = inCommand.substring(inCommand.lastIndexOf('/')+1);
        if (plugin.getConfig().getList("bannedcommands").contains(inCommand)) {
            event.getPlayer().sendMessage(ChatColor.DARK_RED + "no way twin");
            event.setCancelled(true);
        }
    }
}
