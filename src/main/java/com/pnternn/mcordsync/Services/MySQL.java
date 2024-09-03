package com.pnternn.mcordsync.Services;

import com.pnternn.mcordsync.Models.DiscordReportData;
import com.pnternn.mcordsync.Models.DiscordUserData;
import com.pnternn.mcordsync.Config.ConfigurationHandler;
import com.pnternn.mcordsync.Models.PlayerData;
import org.bukkit.Bukkit;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

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
        if(ConfigurationHandler.getValue("mysql.enabled").equals("false")) return;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS "+ ConfigurationHandler.getValue("mysql.table") +" (uuid VARCHAR(255), discordID VARCHAR(255), username VARCHAR(255), avatar VARCHAR(255))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS "+ ConfigurationHandler.getValue("mysql.table") + "_reports" +" (reportID INT, reporterUUID VARCHAR(255), reportedUUID VARCHAR(255), reason VARCHAR(255), date VARCHAR(255), status VARCHAR(255), server VARCHAR(255), messages TEXT, cps DOUBLE)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS "+ ConfigurationHandler.getValue("mysql.table") + "_players" +" (uuid VARCHAR(255), warns INT, muteExpire VARCHAR(255))");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not create table: " + e.getMessage());
        }
    }
    public void createPlayer(UUID uuid){
        if(ConfigurationHandler.getValue("mysql.enabled").equals("false")) return;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO "+ ConfigurationHandler.getValue("mysql.table") + "_players" +" (uuid, warns, muteExpire) VALUES ('" + uuid + "', '0', '0')");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not create player: " + e.getMessage());
        }
    }

    public void createReport(DiscordReportData reportData){
        if(ConfigurationHandler.getValue("mysql.enabled").equals("false")) return;
        try {
            TreeMap<LocalDateTime, String> messages = reportData.getMessages();
            StringBuilder messageString = new StringBuilder();
            for(LocalDateTime date: messages.keySet()){
                messageString.append(date.toString()).append("=").append(messages.get(date)).append(",");
            }
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO "+ ConfigurationHandler.getValue("mysql.table") + "_reports" +" (reportID, reporterUUID, reportedUUID, reason, date, status, server, messages, cps) VALUES ('" + reportData.getReportID() + "', '" + reportData.getReporterUUID() + "', '" + reportData.getReportedUUID() + "', '" + reportData.getReason() + "', '" + reportData.getDate() + "', '" + reportData.getStatus() + "', '" + reportData.getServer() + "', '" + messageString + "', '" + reportData.getCps() + "')");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not create report: " + e.getMessage());
        }
    }

    public void createUser(DiscordUserData userData){
        if(ConfigurationHandler.getValue("mysql.enabled").equals("false")) return;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO "+ ConfigurationHandler.getValue("mysql.table") +" (uuid, discordID, username, avatar) VALUES ('" + userData.getUUID() + "', '" + userData.getDiscordID() + "', '" + userData.getUsername() + "', '" + userData.getAvatar() + "')");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not create crew: " + e.getMessage());
        }
    }
    public void deleteReport(int reportID) {
        if(ConfigurationHandler.getValue("mysql.enabled").equals("false")) return;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM "+ ConfigurationHandler.getValue("mysql.table") + "_reports" +" WHERE reportID='" + reportID + "'");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not delete report: " + e.getMessage());
        }
    }
    public void deleteUser(String uuid) {
        if(ConfigurationHandler.getValue("mysql.enabled").equals("false")) return;
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
        if(ConfigurationHandler.getValue("mysql.enabled").equals("false")) return users;
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
    public void mutePlayer(UUID uuid, LocalDateTime time){
        if(ConfigurationHandler.getValue("mysql.enabled").equals("false")) return;
        try {
            Statement statement = connection.createStatement();
            if(time == null){
                statement.executeUpdate("UPDATE "+ ConfigurationHandler.getValue("mysql.table") + "_players" +" SET muteExpire='0' WHERE uuid='" + uuid + "'");
            }else{
                statement.executeUpdate("UPDATE "+ ConfigurationHandler.getValue("mysql.table") + "_players" +" SET muteExpire='"+ time +"' WHERE uuid='" + uuid + "'");
            }
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not mute player: " + e.getMessage());
        }
    }
    public List<PlayerData> getPlayers(){
        List<PlayerData> players = new ArrayList<>();
        if(ConfigurationHandler.getValue("mysql.enabled").equals("false")) return players;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM "+ ConfigurationHandler.getValue("mysql.table") + "_players");
            while (resultSet.next()) {
                LocalDateTime muteExpire = null;
                if(!Objects.equals(resultSet.getString("muteExpire"), "0")) {
                    muteExpire = LocalDateTime.parse(resultSet.getString("muteExpire"));
                }
                players.add(new PlayerData(UUID.fromString(resultSet.getString("uuid")), muteExpire, resultSet.getInt("warns")));
            }
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not get players: " + e.getMessage());
        }
        return players;
    }

    public List<DiscordReportData> getReports(){
        List<DiscordReportData> reports = new ArrayList<>();
        if(ConfigurationHandler.getValue("mysql.enabled").equals("false")) return reports;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM "+ ConfigurationHandler.getValue("mysql.table") + "_reports");
            while (resultSet.next()) {
                String[] messages = resultSet.getString("messages").split(",");
                TreeMap<LocalDateTime, String> messageMap = new TreeMap<>();
                for(String message: messages){
                    String[] messageData = message.split("=");
                    if(messageData.length == 2){
                        messageMap.put(LocalDateTime.parse(messageData[0]), messageData[1]);
                    }
                }
                reports.add(new DiscordReportData(resultSet.getInt("reportID"), UUID.fromString(resultSet.getString("reporterUUID")), UUID.fromString(resultSet.getString("reportedUUID")), resultSet.getString("reason"), resultSet.getString("date"), resultSet.getString("status"), messageMap, resultSet.getDouble("cps")));
            }
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not get reports: " + e.getMessage());
        }
        return reports;
    }

    public void setStateReport(int reportID, String status) {
        if(ConfigurationHandler.getValue("mysql.enabled").equals("false")) return;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE "+ ConfigurationHandler.getValue("mysql.table") + "_reports" +" SET status='"+ status+"' WHERE reportID='" + reportID + "'");
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Could not close report: " + e.getMessage());
        }
    }

}
