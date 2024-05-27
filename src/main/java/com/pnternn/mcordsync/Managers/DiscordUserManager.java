package com.pnternn.mcordsync.Managers;

import com.google.gson.JsonObject;
import com.pnternn.mcordsync.MCordSync;
import com.pnternn.mcordsync.Models.DiscordUserData;
import com.pnternn.mcordsync.Config.ConfigurationHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DiscordUserManager {
    private final static HashMap<UUID, String> codes = new HashMap<>();
    private static final List<DiscordUserData> userData = new ArrayList<>();
    public DiscordUserManager() {
        super();
    }

    public static String generateCode(UUID playerUUID){
        String code = RandomStringUtils.randomAlphanumeric(16);
        codes.put(playerUUID, code);
        return code;
    }
    public static void removeCode(String code){
        codes.remove(getUUID(code));
    }
    public static UUID getUUID(String code){
        for(UUID uuid : codes.keySet()){
            if(codes.get(uuid).equals(code)){
                return uuid;
            }
        }
        return null;
    }
    public static String getDiscordID(UUID uuid){
        for(DiscordUserData data : userData){
            if(data.getUUID().equals(uuid)){
                return data.getDiscordID();
            }
        }
        return null;
    }

    public static void addUserData(DiscordUserData data, boolean newUser){
        userData.add(data);
        if(newUser){

            MCordSync.getInstance().getMySQL().createUser(data);
            JSONObject user = new JSONObject();
            user.put("type", "GET_USER");
            user.put("uuid", data.getUUID().toString());
            user.put("discordID", data.getDiscordID());
            user.put("username", data.getUsername());
            user.put("avatar", data.getAvatar());
            MCordSync.getInstance().getRedis().publish(ConfigurationHandler.getValue("redis.channel"), user);
        }
    }

    public static DiscordUserData getUserData(UUID uuid){
        for(DiscordUserData data : userData){
            if(data.getUUID().equals(uuid)){
                return data;
            }
        }
        return null;
    }
    public static void giveDiscordRole(String discordID, String roleID){
        if(ConfigurationHandler.getValue("bot.enabled").equals("true")){
            Guild guild = MCordSync.getJDA().getGuildById(ConfigurationHandler.getValue("guild.id"));
            if(guild == null){
                return;
            }
            Member member = guild.getMemberById(discordID);
            if(member == null){
                return;
            }
            Role role = MCordSync.getJDA().getRoleById(roleID);
            if(role == null){
                return;
            }
            guild.addRoleToMember(member, role).queue();
        }else{
            JSONObject user = new JSONObject();
            user.put("type", "GIVE_ROLE");
            user.put("discordID", discordID);
            user.put("roleID", roleID);
            MCordSync.getInstance().getRedis().publish(ConfigurationHandler.getValue("redis.channel"), user);
        }
    }
    public static void takeDiscordRole(String discordID, String roleID){
        if(ConfigurationHandler.getValue("bot.enabled").equals("true")){
            Guild guild = MCordSync.getJDA().getGuildById(ConfigurationHandler.getValue("guild.id"));
            if(guild == null){
                return;
            }
            Member member = guild.getMemberById(discordID);
            if(member == null){
                return;
            }
            Role role = MCordSync.getJDA().getRoleById(roleID);
            if(role == null){
                return;
            }
            guild.removeRoleFromMember(member, role).queue();
        }else{
            JSONObject user = new JSONObject();
            user.put("type", "TAKE_ROLE");
            user.put("discordID", discordID);
            user.put("roleID", roleID);
            MCordSync.getInstance().getRedis().publish(ConfigurationHandler.getValue("redis.channel"), user);
        }
    }
    public static void takeDiscordRoles(String discordID){
        if(ConfigurationHandler.getValue("bot.enabled").equals("true")){
            Guild guild = MCordSync.getJDA().getGuildById(ConfigurationHandler.getValue("guild.id"));
            if(guild == null){
                return;
            }
            Member member = guild.getMemberById(discordID);
            if(member == null){
                return;
            }
            for (String key : ConfigurationHandler.getKeys("roles")) {
                Role role = MCordSync.getJDA().getRoleById(ConfigurationHandler.getValue("roles." + key + ".id"));
                if(role == null){
                    continue;
                }
                guild.removeRoleFromMember(member, role).queue();
            }
        }else{
            JSONObject user = new JSONObject();
            user.put("type", "TAKE_ROLES");
            user.put("discordID", discordID);
            MCordSync.getInstance().getRedis().publish(ConfigurationHandler.getValue("redis.channel"), user);
        }
    }

    public static void giveDiscordRoles(String discordID){
        if(ConfigurationHandler.getValue("bot.enabled").equals("true")){
            DiscordUserData discordUser = getUserData(discordID);
            if(discordUser != null){
                Member member = MCordSync.getJDA().getGuildById(ConfigurationHandler.getValue("guild.id")).getMemberById(discordID);
                if(member == null){
                    return;
                }
                UserManager userManager = LuckPermsProvider.get().getUserManager();
                User user = userManager.getUser(discordUser.getUUID());
                if(user==null){
                    user = userManager.loadUser(discordUser.getUUID()).join();
                }
                Guild guild = MCordSync.getJDA().getGuildById(ConfigurationHandler.getValue("guild.id"));
                if(guild == null){
                    return;
                }
                for (String key : ConfigurationHandler.getKeys("roles")) {
                    Role role = MCordSync.getJDA().getRoleById(ConfigurationHandler.getValue("roles." + key + ".id"));
                    if(role == null){
                        continue;
                    }
                    for(Node node : user.getNodes(NodeType.PERMISSION)){
                        if(node.getKey().equals(ConfigurationHandler.getValue("roles." + key + ".permission"))){
                            guild.addRoleToMember(member, role).queue();
                        }
                    }
                }
            }
        }else{
            JSONObject user = new JSONObject();
            user.put("type", "GIVE_ROLES");
            user.put("discordID", discordID);
            MCordSync.getInstance().getRedis().publish(ConfigurationHandler.getValue("redis.channel"), user);
        }
    }



    public static List<DiscordUserData> getUserDatas(){
        return userData;
    }
    public static DiscordUserData getUserData(String discordID){
        for(DiscordUserData data : userData){
            if(data.getDiscordID().equals(discordID)){
                return data;
            }
        }
        return null;
    }
    public static void removeUserData(UUID uuid){
        userData.remove(getUserData(uuid));
        MCordSync.getInstance().getMySQL().deleteUser(uuid.toString());
        JSONObject user = new JSONObject();
        user.put("type", "REMOVE_USER");
        user.put("uuid", uuid.toString());
        MCordSync.getInstance().getRedis().publish(ConfigurationHandler.getValue("redis.channel"), user);
    }
}
