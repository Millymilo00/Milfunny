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

        String[] splitCommand = event.getMessage().split(" ");
        String inCommand = splitCommand[0].substring(splitCommand[0].lastIndexOf('/')+1);
        // Block command
        if (plugin.getConfig().getList("bannedcommands").contains(inCommand)) {
            event.getPlayer().sendMessage(ChatColor.DARK_RED + "no way twin");
            event.setCancelled(true);
            return;
        }
        // Intercept /give to add admin mark
        if ("give".equals(inCommand) && splitCommand.length > 2) {
            String newItem;
            if (splitCommand[2].contains("{")) {
                if (splitCommand[2].contains("Lore:")) {
                    int lorePos = splitCommand[2].indexOf("Lore:[") + 6;
                    newItem = splitCommand[2].substring(0, lorePos) + "'[{\"text\":\"Admin Marked\",\"italic\":false}]'," + splitCommand[2].substring(lorePos);
                }
                else if (splitCommand[2].contains("display:")) {
                    int displayPos = splitCommand[2].indexOf("display:{") + 9;
                    newItem = splitCommand[2].substring(0, displayPos) + "Lore:['[{\"text\":\"Admin Marked\",\"italic\":false}]']," + splitCommand[2].substring(displayPos);
                }
                else {
                    int curlPos = splitCommand[2].indexOf("{") + 1;
                    newItem = splitCommand[2].substring(0, curlPos) + "display:{Lore:['[{\"text\":\"Admin Marked\",\"italic\":false}]']}," + splitCommand[2].substring(curlPos);
                }
            }
            else {
                newItem = splitCommand[2] + "{display:{Lore:['[{\"text\":\"Admin Marked\",\"italic\":false}]']}}";
            }
            // if we add more interceptions this will have to be changed
            splitCommand[2] = newItem;
            event.setMessage(String.join(" ", splitCommand));
        }
    }
}
