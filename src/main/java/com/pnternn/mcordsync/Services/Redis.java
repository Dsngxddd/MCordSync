package com.pnternn.mcordsync.Services;

import com.pnternn.mcordsync.Config.ConfigurationHandler;
import com.pnternn.mcordsync.Managers.DiscordUserManager;
import com.pnternn.mcordsync.Models.DiscordUserData;
import org.bukkit.Bukkit;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class Redis {

    Jedis jedis;
    Thread redisThread;

    public Redis(String host, int port) {
        jedis = new Jedis(host, port);
        jedis.auth(ConfigurationHandler.getValue("redis.password"));
        jedis.connect();
        subscribe();
        if(redisThread!=null) redisThread.start();
    }

    public void subscribe(){
        redisThread = new Thread("Redis Subscriber"){
            @Override
            public void run(){
                jedis.subscribe(new JedisPubSub(){
                    @Override
                    public void onMessage(String channel, String message){

                        if(channel.equals(ConfigurationHandler.getValue("redis.channel"))){
                            JSONObject obj = new JSONObject(message);
                            if(obj.get("type").toString().equals("GET_USER")) {
                                if( DiscordUserManager.getUserData(UUID.fromString(obj.get("uuid").toString())) == null){
                                    DiscordUserData user = new DiscordUserData(UUID.fromString(obj.get("uuid").toString()), obj.get("discordID").toString(), obj.get("username").toString(), obj.get("avatar").toString());
                                    DiscordUserManager.addUserData(user, false);
                                }
                            }else if(obj.get("type").toString().equals("REMOVE_USER")){
                                if( DiscordUserManager.getUserData(UUID.fromString(obj.get("uuid").toString())) != null){
                                    DiscordUserManager.removeUserData(UUID.fromString(obj.get("uuid").toString()));
                                }
                            }else if(obj.get("type").toString().equals("GIVE_ROLE")){
                                if(ConfigurationHandler.getValue("bot.enabled").equals("true")){
                                    String discordID = obj.get("discordID").toString();
                                    String roleID = obj.get("roleID").toString();
                                    DiscordUserManager.giveDiscordRole(discordID, roleID);
                                }
                            }else if(obj.get("type").toString().equals("TAKE_ROLE")){
                                if(ConfigurationHandler.getValue("bot.enabled").equals("true")){
                                    String discordID = obj.get("discordID").toString();
                                    String roleID = obj.get("roleID").toString();
                                    DiscordUserManager.takeDiscordRole(discordID, roleID);
                                }
                            }else if(obj.get("type").toString().equals("GIVE_ROLES")){
                                if(ConfigurationHandler.getValue("bot.enabled").equals("true")){
                                    String discordID = obj.get("discordID").toString();
                                    DiscordUserManager.giveDiscordRoles(discordID);
                                }
                            }else if(obj.get("type").toString().equals("TAKE_ROLES")){
                                if(ConfigurationHandler.getValue("bot.enabled").equals("true")){
                                    String discordID = obj.get("discordID").toString();
                                    DiscordUserManager.takeDiscordRoles(discordID);
                                }
                            }
                        }

                    }
                }, ConfigurationHandler.getValue("redis.channel"));
            }
        };
    }

    public void close() {
        if(redisThread!=null && redisThread.isAlive()) {
            redisThread.interrupt();
        }
    }
    public void publish(String channel, JSONObject obj){
        if(ConfigurationHandler.getValue("redis.enabled").equals("false")) return;

        try(Jedis publisher = new Jedis(ConfigurationHandler.getValue("redis.host"), Integer.parseInt(ConfigurationHandler.getValue("redis.port")))) {
            publisher.auth(ConfigurationHandler.getValue("redis.password"));
            publisher.publish(channel, obj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
