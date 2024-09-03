package com.pnternn.mcordsync.Models;

import java.util.UUID;

public class DiscordUserData {
    private UUID uuid;
    private String ipAddress;
    private String discordID;
    private String username;
    private String avatar;

    public DiscordUserData(UUID uuid,String discordID, String username, String avatar) {
        this.uuid = uuid;
        this.discordID = discordID;
        this.username = username;
        this.avatar = avatar;
    }

    public UUID getUUID() {
        return uuid;
    }



    public String getDiscordID() {
        return discordID;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }
}
