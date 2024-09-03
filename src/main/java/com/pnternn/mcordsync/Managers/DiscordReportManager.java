package com.pnternn.mcordsync.Managers;

import com.pnternn.mcordsync.Config.ConfigurationHandler;
import com.pnternn.mcordsync.Models.DiscordReportData;
import com.pnternn.mcordsync.Models.DiscordUserData;
import com.pnternn.mcordsync.Utils.PropertiesUtil;
import com.pnternn.mcordsync.MCordSync;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bukkit.Bukkit;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class DiscordReportManager {
    private static List<DiscordReportData> reports = new ArrayList<>();
    private static int lastreportID = 0;

    public DiscordReportManager() {
        DiscordReportManager.reports = MCordSync.getInstance().getMySQL().getReports();
        for (DiscordReportData report : reports) {
            if (report.getReportID() > lastreportID) {
                lastreportID = report.getReportID();
            }
        }
    }

    public static void createReport(UUID reporterUUID, UUID reportedUUID, String reason, String date, String status, Boolean saveMysql) {
        lastreportID++;
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        DiscordReportData report = new DiscordReportData(lastreportID, reporterUUID, reportedUUID, reason, date, status, MCordSync.getPlayerManager().getPlayerMessages(reportedUUID), MCordSync.getPlayerManager().getPlayerCPS(reportedUUID));
        reports.add(report);

        EmbedBuilder eb = getEmbed(report);
        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.primary("report_" + lastreportID + "_close", ConfigurationHandler.getValue("reportbutton.reportclose")));
        buttons.add(Button.success("report_" + lastreportID + "_gift", ConfigurationHandler.getValue("reportbutton.giftedplayer")));
        buttons.add(Button.danger("report_" + lastreportID + "_ban", ConfigurationHandler.getValue("reportbutton.banned")));
        buttons.add(Button.danger("report_" + lastreportID + "_mute", ConfigurationHandler.getValue("reportbutton.muted")));
        buttons.add(Button.danger("report_" + lastreportID + "_15mmute", ConfigurationHandler.getValue("reportbutton.15mmuted")));

        MCordSync.getJDA().getGuildById(ConfigurationHandler.getValue("guild.id")).getTextChannelById(ConfigurationHandler.getValue("guild.reportChannelID")).sendMessageEmbeds(eb.build()).addActionRow(buttons).queue();

        if (saveMysql) {
            MCordSync.getInstance().getMySQL().createReport(report);
        }
    }

    public static void closeReport(Integer reportID) {
        DiscordReportData report = getReport(reportID);
        if (report != null) {
            report.setStatus("closed");
        }
    }

    public static DiscordReportData getReport(Integer reportID) {
        for (DiscordReportData report : reports) {
            if (report.getReportID() == reportID) {
                return report;
            }
        }
        return null;
    }

    public static EmbedBuilder getEmbed(DiscordReportData report) {
        String author = ConfigurationHandler.getValue("reportembed.author").replace("%reportID%", String.valueOf(report.getReportID()));
        String authorUrl = ConfigurationHandler.getValue("reportembed.author_url")
                .replace("%host%", ConfigurationHandler.getValue("bot.host"))
                .replace("%port%", ConfigurationHandler.getValue("bot.port"))
                .replace("%reportID%", String.valueOf(report.getReportID()));
        String authorIcon = ConfigurationHandler.getValue("reportembed.author_icon");


        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(author, authorUrl, authorIcon);
        eb.setColor(Color.orange);

        DiscordUserData reporter = DiscordUserManager.getUserData(report.getReporterUUID());
        DiscordUserData reported = DiscordUserManager.getUserData(report.getReportedUUID());

        String content = ConfigurationHandler.getValue(reporter != null ? "reportembed.content.reporter" : "reportembed.content.reporter_no_mention")
                .replace("%reporterName%", Bukkit.getOfflinePlayer(report.getReporterUUID()).getName())
                .replace("%reporterMention%", reporter != null ? MCordSync.getJDA().getUserById(reporter.getDiscordID()).getAsMention() : "");
        content += "\n";
        content += "\n" + ConfigurationHandler.getValue(reported != null ? "reportembed.content.reported" : "reportembed.content.reported_no_mention")
                .replace("%reportedName%", Bukkit.getOfflinePlayer(report.getReportedUUID()).getName())
                .replace("%reportedMention%", reported != null ? MCordSync.getJDA().getUserById(reported.getDiscordID()).getAsMention() : "");
        content += "\n";
        content += "\n" + ConfigurationHandler.getValue("reportembed.content.reported_ip")
                .replace("%reportedIP%", reported != null ? PlayerManager.getPlayerIPAddress(reported.getUUID()) : "Bilinmiyor");
        content += "\n";
        content += "\n" + ConfigurationHandler.getValue("reportembed.content.reporter_ip")
                .replace("%reporterIP%", reporter != null ? PlayerManager.getPlayerIPAddress(reporter.getUUID()) : "Bilinmiyor");
        content += "\n";
        content += "\n" + ConfigurationHandler.getValue("reportembed.content.server")
                .replace("%serverName%", "LiteRpg");
        content += "\n";
        content += "\n" + ConfigurationHandler.getValue("reportembed.content.reason")
                .replace("%reason%", report.getReason());
        content += "\n";
        content += "\n" + ConfigurationHandler.getValue("reportembed.content.cps")
                .replace("%cps%", String.valueOf(MCordSync.getPlayerManager().getPlayerCPS(report.getReportedUUID()) / 20));
        content += "\n";
        TreeMap<LocalDateTime, String> messages = MCordSync.getPlayerManager().getPlayerMessages(report.getReportedUUID());
        if (!messages.isEmpty()) {
            content += "\n" + ConfigurationHandler.getValue("reportembed.content.last_messages")
                    .replace("%messageCount%", String.valueOf(messages.size()));
            content += "```apache\n";
            for (LocalDateTime date1 : messages.keySet()) {
                content += date1.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + " = " + messages.get(date1) + "\n";
            }
            content += "```";
        }
        content += "\n";
        content += "\n" + ConfigurationHandler.getValue("reportembed.content.status")
                .replace("%status%", report.getStatus());
        content += "\n";
        content += "\n" + ConfigurationHandler.getValue("reportembed.content.date")
                .replace("%date%", report.getDate());
        content += "\n";
        content += "\n" + ConfigurationHandler.getValue("reportembed.content.staff_role")
                .replace("%staffRoleMention%", MCordSync.getJDA().getRoleById(ConfigurationHandler.getValue("guild.staffRoleID")).getAsMention());
        content += "\n";
        eb.setDescription(content);
        eb.setThumbnail("https://mineskin.eu/avatar/" + Bukkit.getOfflinePlayer(report.getReportedUUID()).getName());

        return eb;
    }

    public static int getLastReportID() {
        return lastreportID;
    }

    public static void setLastreportID(int lastreportID) {
        DiscordReportManager.lastreportID = lastreportID;
    }
}
