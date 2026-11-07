package io.github.millymilo00.milFunny;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;

public class MilFunnyDatabase {

    private final Connection connection;
    private final MilFunny plugin;
    private String topVote = null;
    private HashSet<String> ops = new HashSet<>();

    public MilFunnyDatabase(MilFunny plugin, String path) throws SQLException {
        this.plugin = plugin;
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS opMePlrs (" +
                    "username TEXT PRIMARY KEY)");
            statement.execute("CREATE TABLE IF NOT EXISTS candidates (" +
                    "username TEXT PRIMARY KEY, " +
                    "voteYes TEXT NOT NULL DEFAULT \"\", " +
                    "voteNo TEXT NOT NULL DEFAULT \"\", " +
                    "isOp INT NOT NULL DEFAULT false)");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }


    public void addPlayer(String player, String table) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO "+table+" (username) VALUES (?) ON CONFLICT (username) DO NOTHING")) {
            // shutttup table is set by program not by player, this is not a sql-inject vector
            preparedStatement.setString(1, player);
            preparedStatement.executeUpdate();
        }
    }
    public boolean removePlayer(String player, String table) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM "+table+" WHERE username = ?")) {
            preparedStatement.setString(1, player);
            return preparedStatement.executeUpdate() == 1;
        }
    }

    public boolean playerExists(String player, String table) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM "+table+" WHERE username = ?")) {
            preparedStatement.setString(1, player);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public void setPlayerVotes(String candidate, String newVoteYes, String newVoteNo) throws SQLException{
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE candidates SET voteYes = ?, voteNo = ? WHERE username = ?")) {
            preparedStatement.setString(1, newVoteYes);
            preparedStatement.setString(2, newVoteNo);
            preparedStatement.setString(3, candidate);
            preparedStatement.executeUpdate();
        }
    }

    public String[] getPlayerVotes(String player) throws SQLException {
        String[] votes = new String[]{"", ""};
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT voteYes, voteNo FROM candidates WHERE USERNAME = ?")) {
            preparedStatement.setString(1, player);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                votes[0] = resultSet.getString("voteYes");
                votes[1] = resultSet.getString("voteNo");
                return votes;
            } else {
                return null; // Return null if the player isn't in there
            }
        }
    }

    public HashSet<String> getCandidates() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT username FROM candidates")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            HashSet<String> candidates = new HashSet<>();
            while (resultSet.next()) {
                candidates.add(resultSet.getString("username"));
            }
            return candidates;
        }
    }

    public void setCurOp(String player, boolean status) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE candidates SET isOp = ? WHERE username = ?")) {
            preparedStatement.setBoolean(1, status);
            preparedStatement.setString(2, player);
            preparedStatement.executeUpdate();
        }
    }

    public void countVotes() throws SQLException {
        // Get the topVote and current ops
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM candidates")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            int prevMax = 5;
            while (resultSet.next()) {
                String voteYes = resultSet.getString("voteYes");
                String voteNo = resultSet.getString("voteNo");
                int votes = (voteYes.length() - voteYes.replace(",", "").length()) - (voteNo.length() - voteNo.replace(",", "").length());
                if (votes > prevMax) {
                    topVote = resultSet.getString("username");
                    prevMax = votes;
                }
                if (resultSet.getBoolean("isOp")) {
                    ops.add(resultSet.getString("username"));
                }
            }
        }
        applyVotes();
    }

    public void applyVotes() throws SQLException {
        // Set all outdates ops to not be ops
        for (String oppedCandidate : ops) {
            Player oppedPlayer = plugin.getServer().getPlayer(oppedCandidate);
            if (!oppedCandidate.equals(topVote)) {
                setCurOp(oppedCandidate, false);
                if (oppedPlayer != null && oppedPlayer.isOp()) {
                    oppedPlayer.setOp(false);
                    oppedPlayer.sendMessage(ChatColor.RED + "You have been voted out of op!");
                }
            }
        }
        if (topVote == null) {
            return;
        }
        // Op the guy who should be op, if they aren't already
        Player shouldBeOpPlr = plugin.getServer().getPlayer(topVote);
        if (shouldBeOpPlr != null && !shouldBeOpPlr.isOp()) {
            setCurOp(topVote, true);
            shouldBeOpPlr.setOp(true);
            shouldBeOpPlr.sendMessage(ChatColor.AQUA + "You have been voted into op!");
        }
    }
}
