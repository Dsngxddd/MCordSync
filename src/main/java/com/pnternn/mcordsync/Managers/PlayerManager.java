package com.pnternn.mcordsync.Managers;

import com.pnternn.mcordsync.Config.ConfigurationHandler;
import com.pnternn.mcordsync.MCordSync;
import com.pnternn.mcordsync.Models.PlayerData;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.util.*;

public class PlayerManager {
    private static final List<PlayerData> players = new ArrayList<>();

    public PlayerManager() {
        players.addAll(MCordSync.getInstance().getMySQL().getPlayers());
        processCPS();
    }

    private void processCPS(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(MCordSync.getInstance(), new Runnable() {
            @Override
            public void run() {
                for(PlayerData p:players)
                {
                    p.setPlayerClickRate((double) p.getClickCount()/20);
                    p.setPlayerClickCount(0);
                }
            }
        }, 0L, 20*20L);
    }

    public static Double getPlayerCPS(UUID uuid){
        for(PlayerData p:players)
        {
            if(p.getUUID().equals(uuid))
            {
                return p.getClickRate();
            }
        }
        return 0.0;
    }
    public static TreeMap<LocalDateTime, String> getPlayerMessages(UUID uuid){

        for(PlayerData p:players)
        {
            if(p.getUUID().equals(uuid))
            {
                return p.getMessages();
            }
        }
        return new TreeMap<>();
    }
    public static List<PlayerData> getPlayers() {
        return players;
    }
    public void mutePlayer(UUID uuid, LocalDateTime time){
        for(PlayerData p:players)
        {
            if(p.getUUID().equals(uuid))
            {
                p.setMuteExpire(time);
                MCordSync.getInstance().getMySQL().mutePlayer(uuid, time);
            }
        }

    }
    public static PlayerData getPlayer(UUID uuid){
        for(PlayerData p:players)
        {
            if(p.getUUID().equals(uuid))
            {
                return p;
            }
        }
        return null;
    }
}
