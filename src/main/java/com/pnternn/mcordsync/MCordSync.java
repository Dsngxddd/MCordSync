package com.pnternn.mcordsync;

import com.sun.net.httpserver.HttpServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MCordSync extends JavaPlugin {
    private static MCordSync instance;
    @Override
    public void onEnable() {
        instance = this;

        HttpServer server;
        ExecutorService excutor;
        InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 8000);
        try {
            server = HttpServer.create(addr, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/", new HttpServerHandler("1"));
        excutor = Executors.newCachedThreadPool();
        server.setExecutor(excutor);
        server.start();

    }

    @Override
    public void onDisable() {
    }
    public static MCordSync getInstance(){
        return instance;
    }
}
