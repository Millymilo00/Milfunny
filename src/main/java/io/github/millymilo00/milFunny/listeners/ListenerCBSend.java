package io.github.millymilo00.milFunny.listeners;

import io.github.millymilo00.milFunny.MilFunny;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class ListenerCBSend implements Listener {
    final MilFunny plugin;

    public ListenerCBSend(MilFunny plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommandBlockSend(ServerCommandEvent event) {
        if(!(event.getSender() instanceof BlockCommandSender)) {
            return;
        }
        String inCommand = event.getCommand().split(" ")[0];
        inCommand = inCommand.substring((inCommand.lastIndexOf('/')+1));
        if (plugin.getConfig().getList("bannedcommands").contains(inCommand)) {
            event.setCancelled(true);
        }
    }
}
