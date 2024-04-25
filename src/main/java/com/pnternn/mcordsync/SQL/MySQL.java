package com.pnternn.mcordsync.SQL;

import com.pnternn.mcordsync.Models.DiscordUserData;
import com.pnternn.mcordsync.Config.ConfigurationHandler;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQL {
    private Connection connection;
    public MySQL(String host, String port, String database, String username, String password){
        synchronized (this) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Could not connect to MySQL database: " + e.getMessage());
            }
        }
    }
    public void createTable()  {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS "+ ConfigurationHandler.getValue("mysql.table") +" (uuid VARCHAR(255), discordID VARCHAR(255), username VARCHAR(255), avatar VARCHAR(255))");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not create table: " + e.getMessage());
        }
    }

    public void createUser(DiscordUserData userData){
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO "+ ConfigurationHandler.getValue("mysql.table") +" (uuid, discordID, username, avatar) VALUES ('" + userData.getUUID() + "', '" + userData.getDiscordID() + "', '" + userData.getUsername() + "', '" + userData.getAvatar() + "')");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not create crew: " + e.getMessage());
        }
    }
    public void createUser(String uuid, String discordID, String username, String avatar) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO "+ ConfigurationHandler.getValue("mysql.table") +" (uuid, discordID, username, avatar) VALUES ('" + uuid + "', '" + discordID + "', '" + username + ", " + avatar + "')");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not create user: " + e.getMessage());
        }
    }
    public void deleteUser(String uuid) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM "+ ConfigurationHandler.getValue("mysql.table")+" WHERE uuid='" + uuid + "'");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not delete user: " + e.getMessage());
        }
    }
    public List<DiscordUserData> getUsers() {
        List<DiscordUserData> users = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM "+ ConfigurationHandler.getValue("mysql.table"));
            while (resultSet.next()) {
                users.add(new DiscordUserData(UUID.fromString(resultSet.getString("uuid")), resultSet.getString("discordID"), resultSet.getString("username"), resultSet.getString("avatar")));
            }
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not get users: " + e.getMessage());
        }
        return users;
    }

}
