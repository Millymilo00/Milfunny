package io.github.millymilo00.milFunny;

import io.github.millymilo00.milFunny.commands.*;
import io.github.millymilo00.milFunny.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.*;

public final class MilFunny extends JavaPlugin {

    FileConfiguration config = getConfig();
    public HashSet<String> tempOps = new HashSet<>();
    public HashSet<Player> markedPlayers = new HashSet<>();
    public MilFunnyDatabase database;

    @Override
    public void onEnable() {
        setupConfig();
        setupDB();
        // TODO: in these following events, check if "execute" commands contain the banned commands
        getServer().getPluginManager().registerEvents(new ListenerCommand(this), this);
        getServer().getPluginManager().registerEvents(new ListenerCBSend(this), this);

        getServer().getPluginManager().registerEvents(new ListenerPlayerJoin(this), this);
        getServer().getPluginManager().registerEvents(new ListenerCreativeItem(this), this);
        getServer().getPluginManager().registerEvents(new ListenerHasMark(markedPlayers), this);
        getServer().getPluginManager().registerEvents(new ListenerInteractCompass(markedPlayers), this);
        this.getCommand("opme").setExecutor(new CommandOpme(this));
        this.getCommand("vote").setExecutor(new CommandVote(this));
        this.getCommand("milfunny").setExecutor(new CommandAdmin(this));
        this.getCommand("hunt").setExecutor(new CommandHunt(markedPlayers));
    }

    @Override
    public void onDisable() {
        try {
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupConfig() {
        config.addDefault("bannedcommands", Arrays.asList("op", "gamerule", "addopme", "removeopme", "removecandidate", "seed"));
        config.options().copyDefaults(true);
        saveConfig();
    }

    private void setupDB()  {
        try {
            // Ensure the plugin's data folder exists
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            database = new MilFunnyDatabase(this, getDataFolder().getAbsolutePath() + "/data.db");
        } catch (SQLException e) {
            e.printStackTrace();
            this.getLogger().severe("Failed to connect to database! " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
        try {
            tempOps.addAll(database.getCandidates());
        }
        catch (SQLException e) {
            this.getLogger().warning("Unable to manage candidate commands, ermm this may have bad consequences probably");
        }
    }
}
