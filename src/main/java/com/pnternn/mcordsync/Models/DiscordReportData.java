package com.pnternn.mcordsync.Models;

import com.pnternn.mcordsync.MCordSync;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

public class DiscordReportData {
    private int reportID;
    private UUID reporterUUID;
    private UUID reportedUUID;
    private String reason;
    private String date;
    private String status;
    private String server;
    private TreeMap<LocalDateTime, String> messages;
    private Double cps;
    public DiscordReportData(int reportID, UUID reporterUUID, UUID reportedUUID, String reason, String date, String status, String server, TreeMap<LocalDateTime, String> messages, Double cps) {
        this.reportID = reportID;
        this.reporterUUID = reporterUUID;
        this.reportedUUID = reportedUUID;
        this.reason = reason;
        this.date = date;
        this.status = status;
        this.server = server;
        this.messages = messages;
        this.cps = cps;
    }
    public int getReportID() {
        return reportID;
    }
    public UUID getReporterUUID() {
        return reporterUUID;
    }

    public UUID getReportedUUID() {
        return reportedUUID;
    }

    public String getReason() {
        return reason;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getServer() {
        return server;
    }

    public TreeMap<LocalDateTime, String> getMessages() {
        return messages;
    }

    public Double getCps() {
        return cps;
    }
    public void setStatus(String status) {
        MCordSync.getInstance().getMySQL().setStateReport(reportID, status);
        this.status = status;
    }

}
