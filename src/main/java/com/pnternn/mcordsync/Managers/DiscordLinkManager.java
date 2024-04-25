package com.pnternn.mcordsync.Managers;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DiscordLinkManager {
    private final static HashMap<UUID, String> codes = new HashMap<>();
    private static final List<DiscordUserData> userData = new ArrayList<>();
    public DiscordLinkManager() {
        super();
    }

    public static String generateCode(UUID playerUUID){
        String code = RandomStringUtils.randomAlphanumeric(16);
        codes.put(playerUUID, code);
        return code;
    }
    public static String getCode(UUID uuid){
        return codes.get(uuid);
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

    public static void addUserData(DiscordUserData data, boolean saveMysql){
        userData.add(data);
        if(saveMysql){
            MCordSync.getInstance().getMySQL().createUser(data);
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
    }
    public static void takeDiscordRole(String discordID, String roleID){
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
    }
    public static void takeDiscordRoles(String discordID){
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
    }

    public static void giveDiscordRoles(String discordID){
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
    }
}
