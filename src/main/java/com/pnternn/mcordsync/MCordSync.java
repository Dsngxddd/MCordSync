package com.pnternn.mcordsync;

import com.pnternn.mcordsync.Commands.DiscordLinkCommand;
import com.pnternn.mcordsync.Handlers.DiscordLinkWebsiteHandler;
import com.pnternn.mcordsync.Listeners.DiscordBotListener;
import com.pnternn.mcordsync.Listeners.PlayerListener;
import com.pnternn.mcordsync.Managers.DiscordUserManager;
import com.pnternn.mcordsync.Managers.DiscordReportManager;
import com.pnternn.mcordsync.Managers.PlayerManager;
import com.pnternn.mcordsync.Models.DiscordUserData;
import com.pnternn.mcordsync.Config.ConfigurationHandler;
import com.pnternn.mcordsync.SQL.MySQL;
import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MCordSync extends JavaPlugin {
    private static MCordSync instance;
    private MySQL mySql;
    private static JDA jda;
    private static PlayerManager playerManager;
    private static DiscordReportManager reportManager;
    private final ConfigurationHandler configurationHandler = new ConfigurationHandler();
    HttpServer server;
    @Override
    public void onEnable() {
        instance = this;
        configurationHandler.init();

        setupServer();
        setupMySQL();
        setupDiscordBot();

        playerManager = new PlayerManager();
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        reportManager = new DiscordReportManager();

        this.getCommand("discord").setExecutor(new DiscordLinkCommand());
    }
    private void setupDiscordBot(){
        jda = JDABuilder.createDefault(ConfigurationHandler.getValue("bot.token"))
                .setActivity(Activity.playing(Emoji.fromUnicode("\uD83D\uDE0E")+" Hayatınla"))
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS)
                .build();

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            Bukkit.getLogger().severe("Discord bot could not start");
        }
        jda.addEventListener(new DiscordBotListener());
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
            DiscordUserManager.addUserData(user, false);
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
        jda.shutdownNow();
    }
    public static MCordSync getInstance(){
        return instance;
    }
    public static JDA getJDA(){
        return jda;
    }
    public MySQL getMySQL(){
        return mySql;
    }
    public static PlayerManager getPlayerManager(){
        return playerManager;
    }
    public static DiscordReportManager getReportManager(){
        return reportManager;
    }
}
