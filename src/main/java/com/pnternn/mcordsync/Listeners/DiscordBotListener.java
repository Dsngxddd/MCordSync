package com.pnternn.mcordsync.Listeners;

import com.pnternn.mcordsync.Config.ConfigurationHandler;
import com.pnternn.mcordsync.Managers.DiscordReportManager;
import com.pnternn.mcordsync.Models.DiscordReportData;
import com.pnternn.mcordsync.MCordSync;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class DiscordBotListener extends ListenerAdapter {

    public DiscordBotListener() {
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        super.onButtonInteraction(event);
        String[] data = event.getButton().getId().split("_");
        if (data[0].equals("report")) {
            DiscordReportData report = DiscordReportManager.getReport(Integer.parseInt(data[1]));
            if (report != null) {
                OfflinePlayer reported = Bukkit.getOfflinePlayer(report.getReportedUUID());
                OfflinePlayer reporter = Bukkit.getOfflinePlayer(report.getReporterUUID());
                if (data[2].equals("close")) {
                    handleCloseReport(event, report);
                } else if (data[2].equals("gift")) {
                    handleGiftReport(event, report, reporter, reported);
                } else if (data[2].equals("ban")) {
                    handleBanReport(event, report, reporter, reported);
                } else if (data[2].equals("mute")) {
                    handleMuteReport(event, report, reported);
                } else if (data[2].equals("15mmute")) {
                    handleTempMuteReport(event, report, reporter, reported);
                }
            }
        }
    }

    private void handleCloseReport(ButtonInteractionEvent event, DiscordReportData report) {
        report.setStatus(ConfigurationHandler.getValue("messages.discordbutton.statusClosed"));
        EmbedBuilder embed = DiscordReportManager.getEmbed(report);
        embed.setColor(Color.GREEN);
        embed.appendDescription(ConfigurationHandler.getValue("message.discordbutton.closedDescription").replace("%user%", event.getUser().getAsMention()));
        event.getChannel().editMessageEmbedsById(event.getMessageId(), embed.build()).queue();
        event.reply(ConfigurationHandler.getValue("messages.discordbutton.reportClosed")).setEphemeral(true).queue();
    }

    private void handleGiftReport(ButtonInteractionEvent event, DiscordReportData report, OfflinePlayer reporter, OfflinePlayer reported) {
        report.setStatus(ConfigurationHandler.getValue("messages.discordbutton.statusGifted"));
        EmbedBuilder embed = DiscordReportManager.getEmbed(report);
        embed.setColor(Color.BLUE);
        embed.appendDescription(ConfigurationHandler.getValue("messages.discordbutton.giftedDescription").replace("%user%", event.getUser().getAsMention()));
        event.getChannel().editMessageEmbedsById(event.getMessageId(), embed.build()).queue();
        event.reply(ConfigurationHandler.getValue("messages.discordbutton.giftGiven")).setEphemeral(true).queue();

        String giftMessage = ConfigurationHandler.getValue("messages.discordbutton.giftMessageToReporter")
                .replace("%reported%", reported.getName());
        reporter.getPlayer().sendMessage(ChatColor.YELLOW + giftMessage);

        String command = ConfigurationHandler.getValue("commands.giveReward").replace("%player%", reporter.getName());
        executeCommand(command);
    }

    private void handleBanReport(ButtonInteractionEvent event, DiscordReportData report, OfflinePlayer reporter, OfflinePlayer reported) {
        report.setStatus(ConfigurationHandler.getValue("messages.discordbutton.statusBanned"));
        EmbedBuilder embed = DiscordReportManager.getEmbed(report);
        embed.setColor(Color.RED);
        embed.appendDescription(ConfigurationHandler.getValue("messages.discordbutton.bannedDescription").replace("%user%", event.getUser().getAsMention()));
        event.getChannel().editMessageEmbedsById(event.getMessageId(), embed.build()).queue();
        event.reply(ConfigurationHandler.getValue("messages.discordbutton.playerBanned")).setEphemeral(true).queue();

        String command = ConfigurationHandler.getValue("commands.banPlayer")
                .replace("%player%", reported.getName())
                .replace("%reporter%", reporter.getName())
                .replace("%reason%", report.getReason());
        executeCommand(command);
    }

    private void handleMuteReport(ButtonInteractionEvent event, DiscordReportData report, OfflinePlayer reported) {
        report.setStatus(ConfigurationHandler.getValue("messages.discordbutton.statusMuted"));
        EmbedBuilder embed = DiscordReportManager.getEmbed(report);
        embed.setColor(Color.YELLOW);
        embed.appendDescription(ConfigurationHandler.getValue("messages.discordbutton.mutedDescription").replace("%user%", event.getUser().getAsMention()));
        event.getChannel().editMessageEmbedsById(event.getMessageId(), embed.build()).queue();
        event.reply(ConfigurationHandler.getValue("messages.discordbutton.playerMuted")).setEphemeral(true).queue();

        String command = ConfigurationHandler.getValue("commands.mutePlayer").replace("%player%", reported.getName());
        executeCommand(command);
    }

    private void handleTempMuteReport(ButtonInteractionEvent event, DiscordReportData report, OfflinePlayer reporter, OfflinePlayer reported) {
        report.setStatus(ConfigurationHandler.getValue("messages.discordbutton.statusTempMuted"));
        EmbedBuilder embed = DiscordReportManager.getEmbed(report);
        embed.setColor(Color.YELLOW);
        embed.appendDescription(ConfigurationHandler.getValue("messages.discordbutton.tempMutedDescription").replace("%user%", event.getUser().getAsMention()));
        event.getChannel().editMessageEmbedsById(event.getMessageId(), embed.build()).queue();
        event.reply(ConfigurationHandler.getValue("messages.discordbutton.tempMute")).setEphemeral(true).queue();

        String command = ConfigurationHandler.getValue("commands.tempMutePlayer")
                .replace("%player%", reported.getName())
                .replace("%reporter%", reporter.getName())
                .replace("%reason%", report.getReason());
        executeCommand(command);
    }

    private void executeCommand(String command) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }.runTask(MCordSync.getInstance());
    }
}
