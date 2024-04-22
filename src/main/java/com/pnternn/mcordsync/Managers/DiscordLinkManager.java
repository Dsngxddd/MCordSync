package com.pnternn.mcordsync.Managers;

import com.pnternn.mcordsync.MCordSync;
import com.pnternn.mcordsync.Models.DiscordUserData;
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

    public static void addUserData(DiscordUserData data){
        userData.add(data);
    }
    public static void addUserDataWithMysql(DiscordUserData data){
        userData.add(data);
        MCordSync.getInstance().getMySQL().createUser(data);
    }

    public static DiscordUserData getUserData(UUID uuid){
        for(DiscordUserData data : userData){
            if(data.getUUID().equals(uuid)){
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
