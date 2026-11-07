package io.github.millymilo00.milFunny;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;

public class MilFunnyDatabase {

    private final Connection connection;
    private final MilFunny plugin;

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

    public Array getCandidates() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT username FROM candidates")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getArray("username");
            } else {
                return null;
            }
        }
    }

    public String getHighestVote() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM candidates")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            String winner = null;
            int prevMax = 5;
            while (resultSet.next()) {
                String voteYes = resultSet.getString("voteYes");
                String voteNo = resultSet.getString("voteNo");
                int votes = (voteYes.length() - voteYes.replace(",", "").length()) - (voteNo.length() - voteNo.replace(",", "").length());
                if (votes > prevMax) {
                    winner = resultSet.getString("username");
                    prevMax = votes;
                }
            }
            return winner;
        }
    }

    public void setCurOp(String player, boolean status) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE candidates SET isOp = ? WHERE username = ?")) {
            preparedStatement.setBoolean(1, status);
            preparedStatement.setString(2, player);
            preparedStatement.executeUpdate();
        }
    }

    public HashSet<String> getOps() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT username FROM candidates WHERE isOp = true")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            HashSet<String> opped = new HashSet<>();
            while (resultSet.next()) {
                opped.add(resultSet.getString("username"));
            }
            return opped;
        }
    }

    public void applyVotes() throws SQLException {
        String shouldBeOp = getHighestVote();
        for (String oppedCandidate : getOps()) {
            Player oppedPlayer = plugin.getServer().getPlayer(oppedCandidate);
            if (!oppedCandidate.equals(shouldBeOp)) {
                setCurOp(oppedCandidate, false);
                if (oppedPlayer.isOp()) {
                    oppedPlayer.setOp(false);
                    oppedPlayer.sendMessage(ChatColor.RED + "You have been voted out of op!");
                }
            }
        }
        if (shouldBeOp == null) {
            return;
        }
        Player shouldBeOpPlr = plugin.getServer().getPlayer(shouldBeOp);
        if (!shouldBeOpPlr.isOp()) {
            setCurOp(shouldBeOp, true);
            shouldBeOpPlr.setOp(true);
            shouldBeOpPlr.sendMessage(ChatColor.AQUA + "You have been voted into op!");
        }
    }
}
