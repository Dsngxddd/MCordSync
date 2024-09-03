package com.pnternn.mcordsync;

import com.pnternn.mcordsync.Commands.DiscordAdminCommand;
import com.pnternn.mcordsync.Commands.DiscordLinkCommand;
import com.pnternn.mcordsync.Commands.DiscordReportCommand;
import com.pnternn.mcordsync.Handlers.DiscordLinkWebsiteHandler;
import com.pnternn.mcordsync.Listeners.DiscordBotListener;
import com.pnternn.mcordsync.Listeners.PlayerListener;
import com.pnternn.mcordsync.Managers.DiscordUserManager;
import com.pnternn.mcordsync.Managers.DiscordReportManager;
import com.pnternn.mcordsync.Managers.PlayerManager;
import com.pnternn.mcordsync.Models.DiscordUserData;
import com.pnternn.mcordsync.Config.ConfigurationHandler;
import com.pnternn.mcordsync.Services.MySQL;
import com.pnternn.mcordsync.Services.Redis;
import com.pnternn.mcordsync.Utils.Color;
import com.pnternn.mcordsync.placeholder.MCordSyncExpansion;
import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MCordSync extends JavaPlugin {
    private MySQL mySql;
    private Redis redis;
    private static JDA jda;
    private static PlayerManager playerManager;
    private static DiscordReportManager reportManager;
    private final ConfigurationHandler configurationHandler = new ConfigurationHandler();
    HttpServer server;

    private static MCordSync instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();


        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MCordSyncExpansion().register();
        }

        configurationHandler.init();
        setupServer();

        if (ConfigurationHandler.getValue("mysql.enabled").equals("true")) {
            setupMySQL();
        }



        if (ConfigurationHandler.getValue("redis.enabled").equals("true")) {
            setupRedis();
        }

        if (ConfigurationHandler.getValue("bot.enabled").equals("true")) {
            setupDiscordBot();
        }

        playerManager = new PlayerManager();
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        reportManager = new DiscordReportManager();

        this.getCommand("report").setExecutor(new DiscordReportCommand());
        this.getCommand("mcordsync").setExecutor(new DiscordLinkCommand());
        this.getCommand("mcordsync-admin").setExecutor(new DiscordAdminCommand());




    }

    private void setupDiscordBot() {
        jda = JDABuilder.createDefault(ConfigurationHandler.getValue("bot.token"))
                .setActivity(Activity.playing(ConfigurationHandler.getValue("bot.status")))
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS)
                .build();

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            Bukkit.getLogger().severe("Discord bot could not start");
            Thread.currentThread().interrupt();
        }
        jda.addEventListener(new DiscordBotListener());
    }


    private void setupRedis() {
        String host = ConfigurationHandler.getValue("redis.host");
        int port = Integer.parseInt(ConfigurationHandler.getValue("redis.port"));
        redis = new Redis(host, port);
    }

    private void setupMySQL() {
        String host = ConfigurationHandler.getValue("mysql.host");
        String port = ConfigurationHandler.getValue("mysql.port");
        String database = ConfigurationHandler.getValue("mysql.database");
        String username = ConfigurationHandler.getValue("mysql.username");
        String password = ConfigurationHandler.getValue("mysql.password");
        mySql = new MySQL(host, port, database, username, password);
        mySql.createTable();  // Tabloyu oluştur
        for (DiscordUserData user : mySql.getUsers()) {
            DiscordUserManager.addUserData(user, false);
        }
    }

    private void setupServer() {
        ExecutorService executor;
        InetSocketAddress addr = new InetSocketAddress(Integer.parseInt(ConfigurationHandler.getValue("bot.port")));
        try {
            server = HttpServer.create(addr, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/", new DiscordLinkWebsiteHandler());
        executor = Executors.newCachedThreadPool();
        server.setExecutor(executor);
        server.start();
    }

    @Override
    public void onDisable() {
        if (server != null) {
            server.stop(0);
        }
        if (jda != null) {
            jda.shutdownNow();
        }
        if (redis != null) {
            redis.close();
        }

    }

    private String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    public static MCordSync getInstance() {
        return instance;
    }

    public static JDA getJDA() {
        return jda;
    }

    public MySQL getMySQL() {
        return mySql;
    }


    public Redis getRedis() {
        return redis;
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    public static DiscordReportManager getReportManager() {
        return reportManager;
    }
}
