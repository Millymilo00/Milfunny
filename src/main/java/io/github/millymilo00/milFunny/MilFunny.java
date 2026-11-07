package io.github.millymilo00.milFunny;

import io.github.millymilo00.milFunny.commands.*;
import io.github.millymilo00.milFunny.listeners.ListenerCBSend;
import io.github.millymilo00.milFunny.listeners.ListenerCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Array;
import java.sql.SQLException;
import java.util.*;

public final class MilFunny extends JavaPlugin {

    FileConfiguration config = getConfig();
    public HashSet<String> tempOps = new HashSet<String>();
    public MilFunnyDatabase database;

    @Override
    public void onEnable() {
        setupConfig();
        setupDB();
        // TODO: in these following events, check if "execute" commands contain the banned commands
        getServer().getPluginManager().registerEvents(new ListenerCommand(this), this);
        getServer().getPluginManager().registerEvents(new ListenerCBSend(this), this);
        this.getCommand("opme").setExecutor(new CommandOpme(this));
        this.getCommand("vote").setExecutor(new CommandVote(this));
        // TODO: These commands should just be made into some kind of "/milfunny [add|remove] [opme|candidate]" type command
        this.getCommand("addopme").setExecutor(new CommandAddOpme(this));
        this.getCommand("addcandidate").setExecutor(new CommandAddCandidate(this));
        this.getCommand("removeopme").setExecutor(new CommandRemoveOpme(this));
        this.getCommand("removecandidate").setExecutor(new CommandRemoveCandidate(this));
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
        config.addDefault("bannedcommands", Arrays.asList("op", "gamerule", "addopme", "removeopme", "removecandidate"));
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
            Array candidates = database.getCandidates();
            if (candidates != null) {
                tempOps.addAll((Collection<? extends String>) candidates);
            }
        }
        catch (SQLException e) {
            this.getLogger().warning("Unable to manage candidate commands, ermm this may have bad consequences probably");
        }
    }
}
