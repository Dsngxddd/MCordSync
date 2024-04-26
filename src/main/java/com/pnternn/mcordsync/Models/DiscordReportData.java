package com.pnternn.mcordsync.Models;

import java.util.UUID;

public class DiscordReportData {
    private int reportID;
    private UUID reporterUUID;
    private UUID reportedUUID;
    private String reason;
    private String date;
    private String status;

    public DiscordReportData(int reportID, UUID reporterUUID, UUID reportedUUID, String reason, String date, String status) {
        this.reportID = reportID;
        this.reporterUUID = reporterUUID;
        this.reportedUUID = reportedUUID;
        this.reason = reason;
        this.date = date;
        this.status = status;
    }
}
