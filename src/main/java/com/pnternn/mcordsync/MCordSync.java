package com.pnternn.mcordsync;

import com.pnternn.mcordsync.Commands.DiscordLinkCommand;
import com.pnternn.mcordsync.Handlers.DiscordLinkWebsiteHandler;
import com.pnternn.mcordsync.Managers.DiscordLinkManager;
import com.pnternn.mcordsync.Models.DiscordUserData;
import com.pnternn.mcordsync.config.ConfigurationHandler;
import com.pnternn.mcordsync.sql.MySQL;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MCordSync extends JavaPlugin {
    private static MCordSync instance;
    private MySQL mySql;
    private final ConfigurationHandler configurationHandler = new ConfigurationHandler();
    HttpServer server;
    @Override
    public void onEnable() {
        instance = this;
        configurationHandler.init();


        setupServer();
        setupMySQL();

        this.getCommand("discord").setExecutor(new DiscordLinkCommand());
    }
    private void setupMySQL(){
        String host = ConfigurationHandler.getValue("mysql.host");
        String port = ConfigurationHandler.getValue("mysql.port");
        String database = ConfigurationHandler.getValue("mysql.database");
        String username = ConfigurationHandler.getValue("mysql.username");
        String password = ConfigurationHandler.getValue("mysql.password");
        mySql = new MySQL(host, port, database, username, password);
        mySql.createTable();
        for(DiscordUserData user : mySql.getUsers()){
            DiscordLinkManager.addUserData(user);
        }
    }
    private void setupServer(){
        ExecutorService excutor;
        InetSocketAddress addr = new InetSocketAddress(800);
        try {
            server = HttpServer.create(addr, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/", new DiscordLinkWebsiteHandler());
        excutor = Executors.newCachedThreadPool();
        server.setExecutor(excutor);
        server.start();
    }

    @Override
    public void onDisable() {
        server.stop(0);
    }
    public static MCordSync getInstance(){
        return instance;
    }
    public MySQL getMySQL(){
        return mySql;
    }
}
