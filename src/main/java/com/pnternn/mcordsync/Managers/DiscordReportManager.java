package com.pnternn.mcordsync.Managers;

import com.pnternn.mcordsync.Config.ConfigurationHandler;
import com.pnternn.mcordsync.MCordSync;
import com.pnternn.mcordsync.Models.DiscordReportData;
import com.pnternn.mcordsync.Models.DiscordUserData;
import com.pnternn.mcordsync.Utils.PropertiesUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bukkit.Bukkit;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class DiscordReportManager {
    private static List<DiscordReportData> reports = new ArrayList<DiscordReportData>();
    private static int lastreportID = 0;
    public DiscordReportManager() {
        DiscordReportManager.reports = MCordSync.getInstance().getMySQL().getReports();
        for(DiscordReportData report: reports){
            if(report.getReportID()>lastreportID){
                lastreportID = report.getReportID();
            }
        }
    }
    public static void createReport(UUID reporterUUID, UUID reportedUUID, String reason, String date, String status, Boolean saveMysql){
        lastreportID++;
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        DiscordReportData report = new DiscordReportData(lastreportID, reporterUUID, reportedUUID, reason, date, status, propertiesUtil.getValue("motd"), MCordSync.getPlayerManager().getPlayerMessages(reportedUUID), MCordSync.getPlayerManager().getPlayerCPS(reportedUUID));
        reports.add(report);
        EmbedBuilder eb = getEmbed(report);
        List<Button> buttons = new ArrayList<Button>();
        buttons.add(Button.primary("report_"+ lastreportID +"_close", "Raporu Kapat"));
        buttons.add(Button.success("report_"+ lastreportID +"_gift", "Oyuncuya Hediye Ver"));
        buttons.add(Button.danger("report_"+ lastreportID +"_ban", "Oyuncuyu Yasakla"));
        buttons.add(Button.danger("report_"+ lastreportID +"_mute", "Oyuncuyu Sustur"));
        MCordSync.getJDA().getGuildById(ConfigurationHandler.getValue("guild.id")).getTextChannelById(ConfigurationHandler.getValue("guild.reportChannelID")).sendMessageEmbeds(eb.build()).addActionRow(buttons).queue();
        if(saveMysql){
            MCordSync.getInstance().getMySQL().createReport(report);
        }
    }
    public static void closeReport(Integer reportID){
        DiscordReportData report = getReport(reportID);
        if(report != null){
            report.setStatus("closed");
        }
    }

    public static DiscordReportData getReport(Integer reportID){
        for(DiscordReportData report: reports){
            if(report.getReportID() == reportID){
                return report;
            }
        }
        return null;
    }

    public static EmbedBuilder getEmbed(DiscordReportData report){
        PropertiesUtil propertiesUtil = new PropertiesUtil();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Rapor " + report.getReportID() + " (İndirmek için tıkla)", "http://"+ConfigurationHandler.getValue("bot.host") + ":"+ ConfigurationHandler.getValue("bot.port") + "/report/" + report.getReportID(), "https://cdn.icon-icons.com/icons2/1130/PNG/512/downloadwithcircularbutton_80316.png");
        eb.setColor(Color.orange);
        String content = "\n";
        DiscordUserData reporter = DiscordUserManager.getUserData(report.getReporterUUID());
        DiscordUserData reported = DiscordUserManager.getUserData(report.getReportedUUID());
        if(reporter != null){
            content += "Raporlayan: ** "+ Bukkit.getOfflinePlayer(report.getReportedUUID()).getName()+" ("+ MCordSync.getJDA().getUserById(reporter.getDiscordID()).getAsMention() +")**\n";
        }else{
            content += "Raporlayan: ** "+ Bukkit.getOfflinePlayer(report.getReporterUUID()).getName() +"**\n";
        }
        if(reported != null){
            content += "Raporlanan: ** "+Bukkit.getOfflinePlayer(report.getReportedUUID()).getName()+" ("+ MCordSync.getJDA().getUserById(reported.getDiscordID()).getAsMention() +")**\n";
        }else{
            content += "Raporlanan: ** "+ Bukkit.getOfflinePlayer(report.getReportedUUID()).getName() +"**\n";
        }
        content += "\n";
        content += "Sunucu: **"+ propertiesUtil.getValue("motd") +"**\n";
        content += "Sebep: **"+report.getReason()+"**\n";
        content += "\n";
        content += "Son 20 saniyedeki makro Oranı: **"+ MCordSync.getPlayerManager().getPlayerCPS(report.getReportedUUID())/20 +"/Saniye**\n";
        content += "\n";

        TreeMap<LocalDateTime, String> messages = MCordSync.getPlayerManager().getPlayerMessages(report.getReportedUUID());
        if(messages.size()>0){
            content += "**Son "+(messages.size())+" Mesajı:**\n";
            content += "```apache\n";
            for(LocalDateTime date1: messages.keySet()){
                content += date1.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))+" = "+messages.get(date1)+"\n";
            }
            content += "```";
        }
        content += "\n";
        content += "Durum: **"+report.getStatus()+"**\n";
        content += "Tarih: **"+report.getDate()+"**\n";
        content += "\n";
        content += MCordSync.getJDA().getRoleById(ConfigurationHandler.getValue("guild.staffRoleID")).getAsMention();
        eb.setDescription(content);
        eb.setThumbnail("https://mineskin.eu/avatar/"+Bukkit.getOfflinePlayer(reported.getUUID()).getName());
        return eb;
    }

    public static int getLastReportID(){
        return lastreportID;
    }
    public static void setLastreportID(int lastreportID) {
        DiscordReportManager.lastreportID = lastreportID;
    }
}
