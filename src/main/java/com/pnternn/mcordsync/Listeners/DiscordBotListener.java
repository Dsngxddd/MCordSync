package com.pnternn.mcordsync.Listeners;

import com.pnternn.mcordsync.MCordSync;
import com.pnternn.mcordsync.Managers.DiscordReportManager;
import com.pnternn.mcordsync.Managers.DiscordUserManager;
import com.pnternn.mcordsync.Models.DiscordReportData;
import com.pnternn.mcordsync.Models.DiscordUserData;
import com.pnternn.mcordsync.Config.ConfigurationHandler;
import com.pnternn.mcordsync.Utils.PropertiesUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.LuckPermsEvent;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.prefs.NodeChangeEvent;
import java.util.stream.Collectors;

public class DiscordBotListener extends ListenerAdapter{

    public DiscordBotListener() {

    }
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if(event.getGuild().getId().equals(ConfigurationHandler.getValue("guild.id"))){
            DiscordUserManager.giveDiscordRoles(event.getUser().getId());
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        super.onButtonInteraction(event);
        String[] data = event.getButton().getId().split("_");
        if(data[0].equals("report")){
            DiscordReportData report = DiscordReportManager.getReport(Integer.parseInt(data[1]));
            if(report != null){
                List<ItemComponent> actionRow = event.getMessage().getActionRows().get(0).getComponents();
                MiniMessage mm = MiniMessage.miniMessage();
                OfflinePlayer reported = Bukkit.getOfflinePlayer(report.getReportedUUID());
                OfflinePlayer reporter = Bukkit.getOfflinePlayer(report.getReporterUUID());
                if (data[2].equals("close")) {
                    report.setStatus("closed");
                    EmbedBuilder embed = DiscordReportManager.getEmbed(report);
                    embed.setColor(Color.GREEN);
                    embed.appendDescription("\n\n**Raporu kapatan:** " + event.getUser().getAsMention());
                    event.getChannel().editMessageEmbedsById(event.getMessageId(), embed.build()).queue();
                    event.reply("Rapor kapatıldı").setEphemeral(true).queue();
                    if(reported.isOnline()){
                        ((net.kyori.adventure.audience.Audience) reported.getPlayer()).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.reportChangeStatusMessage"), Placeholder.styling("link", ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "http://" + ConfigurationHandler.getValue("bot.host") + ":" + ConfigurationHandler.getValue("bot.port") + "/report/" + Integer.parseInt(data[1])))));
                    }
                    if(reporter.isOnline()){
                        ((net.kyori.adventure.audience.Audience) reporter.getPlayer()).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.reportChangeStatusMessage"), Placeholder.styling("link", ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "http://" + ConfigurationHandler.getValue("bot.host") + ":" + ConfigurationHandler.getValue("bot.port") + "/report/" + Integer.parseInt(data[1])))));
                    }
                } else if (data[2].equals("gift")) {
                    report.setStatus("gifted");
                    EmbedBuilder embed = DiscordReportManager.getEmbed(report);
                    embed.setColor(Color.BLUE);
                    embed.appendDescription("\n\n**Hediye Veren:** " + event.getUser().getAsMention());
                    event.getChannel().editMessageEmbedsById(event.getMessageId(), embed.build()).queue();
                    event.reply("Oyuncuya hediye verildi").setEphemeral(true).queue();
                    if(reported.isOnline()){
                        ((net.kyori.adventure.audience.Audience) reported.getPlayer()).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.reportChangeStatusMessage"), Placeholder.styling("link", ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "http://" + ConfigurationHandler.getValue("bot.host") + ":" + ConfigurationHandler.getValue("bot.port") + "/report/" + Integer.parseInt(data[1])))));
                    }
                    if(reporter.isOnline()){
                        ((net.kyori.adventure.audience.Audience) reporter.getPlayer()).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.reportChangeStatusMessage"), Placeholder.styling("link", ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "http://" + ConfigurationHandler.getValue("bot.host") + ":" + ConfigurationHandler.getValue("bot.port") + "/report/" + Integer.parseInt(data[1])))));
                    }
                } else if (data[2].equals("ban")) {
                    report.setStatus("banned");
                    EmbedBuilder embed = DiscordReportManager.getEmbed(report);
                    embed.setColor(Color.RED);
                    embed.appendDescription("\n\n**Yasaklayan:** " + event.getUser().getAsMention());
                    event.getChannel().editMessageEmbedsById(event.getMessageId(), embed.build()).queue();
                    event.reply("Oyuncu yasaklandı").setEphemeral(true).queue();
                    if(reported.isOnline()){
                        ((net.kyori.adventure.audience.Audience) reported.getPlayer()).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.reportChangeStatusMessage"), Placeholder.styling("link", ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "http://" + ConfigurationHandler.getValue("bot.host") + ":" + ConfigurationHandler.getValue("bot.port") + "/report/" + Integer.parseInt(data[1])))));
                    }
                    if(reporter.isOnline()){
                        ((net.kyori.adventure.audience.Audience) reporter.getPlayer()).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.reportChangeStatusMessage"), Placeholder.styling("link", ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "http://" + ConfigurationHandler.getValue("bot.host") + ":" + ConfigurationHandler.getValue("bot.port") + "/report/" + Integer.parseInt(data[1])))));
                    }
                } else if (data[2].equals("mute")) {
                    report.setStatus("muted");
                    EmbedBuilder embed = DiscordReportManager.getEmbed(report);
                    embed.setColor(Color.YELLOW);
                    embed.appendDescription("\n\n**Susturan:** " + event.getUser().getAsMention());
                    event.getChannel().editMessageEmbedsById(event.getMessageId(), embed.build()).queue();
                    event.reply("Oyuncu susturuldu").setEphemeral(true).queue();
                    Bukkit.getOfflinePlayer(report.getReportedUUID()).getPlayer();
                    if(reported.isOnline()){
                        ((net.kyori.adventure.audience.Audience) reported.getPlayer()).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.reportChangeStatusMessage"), Placeholder.styling("link", ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "http://" + ConfigurationHandler.getValue("bot.host") + ":" + ConfigurationHandler.getValue("bot.port") + "/report/" + Integer.parseInt(data[1])))));
                    }
                    if(reporter.isOnline()){
                        ((net.kyori.adventure.audience.Audience) reporter.getPlayer()).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.reportChangeStatusMessage"), Placeholder.styling("link", ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "http://" + ConfigurationHandler.getValue("bot.host") + ":" + ConfigurationHandler.getValue("bot.port") + "/report/" + Integer.parseInt(data[1])))));
                    }
                }
            }
        }
    }
}
