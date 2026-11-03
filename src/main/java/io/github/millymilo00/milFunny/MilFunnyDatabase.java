package io.github.millymilo00.milFunny;

import java.sql.*;

public class MilFunnyDatabase {

    private final Connection connection;

    public MilFunnyDatabase(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS opMePlrs (" +
                    "username TEXT PRIMARY KEY)");
            statement.execute("CREATE TABLE IF NOT EXISTS candidates (" +
                    "username TEXT PRIMARY KEY, " +
                    "voteYes TEXT NOT NULL DEFAULT \"\")" +
                    "voteNo TEXT NOT NULL DEFAULT \"\")");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }


    public void addPlayer(String player, String table) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO "+table+" (username) VALUES (?) ON CONFLICT (username) DO NOTHING;")) {
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

    public void setPlayerVotes(String candidate, String newVoterList) throws SQLException{
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE candidates SET votes = ? WHERE username = ?")) {
            preparedStatement.setString(1, newVoterList);
            preparedStatement.setString(2, candidate);
            preparedStatement.executeUpdate();
        }
    }

    public String getPlayerVotes(String player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT votes FROM candidates WHERE username = ?")) {
            preparedStatement.setString(1, player);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("votes");
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
}
