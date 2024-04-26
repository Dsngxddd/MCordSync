package com.pnternn.mcordsync.Managers;

import com.pnternn.mcordsync.MCordSync;
import com.pnternn.mcordsync.Models.DiscordReportData;
import com.pnternn.mcordsync.Models.DiscordUserData;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;

import java.awt.*;
import java.util.UUID;

public class DiscordReportManager {
    private static int LastreportID;
    public static void createReport(UUID reporterUUID, UUID reportedUUID, String reason, String date, String status){
        LastreportID++;
        DiscordReportData report = new DiscordReportData(LastreportID, reporterUUID, reportedUUID, reason, date, status);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Rapor (" + LastreportID + ")");
        eb.setColor(Color.yellow);
        String content = "\n";
        DiscordUserData reporter = DiscordLinkManager.getUserData(reporterUUID);
        DiscordUserData reported = DiscordLinkManager.getUserData(reportedUUID);
        if(reporter != null){
            content += "Raporlayan: ** "+Bukkit.getOfflinePlayer(reporterUUID).getName()+" ("+ MCordSync.getJDA().getUserById(reporter.getDiscordID()).getAsTag() +")**\n";
        }else{
            content += "Raporlayan: ** "+ Bukkit.getOfflinePlayer(reporterUUID).getName() +"**\n";
        }
        if(reported != null){
            content += "Raporlanan: ** "+Bukkit.getOfflinePlayer(reportedUUID).getName()+" ("+ MCordSync.getJDA().getUserById(reported.getDiscordID()).getAsTag() +")**\n";
        }else{
            content += "Raporlanan: ** "+ Bukkit.getOfflinePlayer(reportedUUID).getName() +"**\n";
        }
        content += "\n";
        content += "Sunucu: "+ Bukkit.getServer().getName() +"\n";


    }

}
