package com.pnternn.mcordsync.Models;

import java.util.UUID;

public class DiscordUserData {
    private UUID uuid;
    private String DiscordID;
    private String Username;
    private String avatar;
    public DiscordUserData(UUID uuid, String DiscordID, String Username, String avatar) {
        this.uuid = uuid;
        this.DiscordID = DiscordID;
        this.Username = Username;
        this.avatar = avatar;
    }
    public UUID getUUID() {
        return uuid;
    }
    public String getDiscordID() {
        return DiscordID;
    }
    public String getUsername() {
        return Username;
    }
    public String getAvatar() {
        return avatar;
    }
}
