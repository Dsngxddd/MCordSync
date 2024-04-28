package com.pnternn.mcordsync.Models;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

public class PlayerData {


    private UUID playerUUID;
    private LocalDateTime playerMuteExpire;
    private Integer playerWarns;
    private Double playerClickRate;
    private Integer playerClickCount;
    private final TreeMap<LocalDateTime, String> playerMessages = new TreeMap<>();

    public PlayerData(UUID playerUUID, LocalDateTime playerMuteExpire, Integer playerWarns){
        this.playerUUID = playerUUID;
        this.playerMuteExpire = playerMuteExpire;
        this.playerWarns = playerWarns;
        this.playerClickRate = 0.0;
        this.playerClickCount = 0;

    }
    public UUID getUUID(){
        return playerUUID;
    }

    public void setMuteExpire(LocalDateTime playerMuteExpire){
        this.playerMuteExpire = playerMuteExpire;
    }
    public LocalDateTime getMuteExpire(){
        return playerMuteExpire;
    }
    public Integer getWarns(){
        return playerWarns;
    }
    public Double getClickRate(){
        return playerClickRate;
    }
    public Integer getClickCount(){
        return playerClickCount;
    }
    public TreeMap<LocalDateTime, String> getMessages(){
        return playerMessages;
    }

    public void setPlayerClickRate(Double playerClickRate) {
        this.playerClickRate = playerClickRate;
    }

    public void setPlayerClickCount(Integer playerClickCount) {
        this.playerClickCount = playerClickCount;
    }

}
