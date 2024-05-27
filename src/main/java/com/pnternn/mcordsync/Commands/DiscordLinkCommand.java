package com.pnternn.mcordsync.Commands;

import com.pnternn.mcordsync.MCordSync;
import com.pnternn.mcordsync.Managers.DiscordReportManager;
import com.pnternn.mcordsync.Managers.DiscordUserManager;
import com.pnternn.mcordsync.Config.ConfigurationHandler;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiscordLinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        MiniMessage mm = MiniMessage.miniMessage();
        if(sender instanceof Player){
            Player player = (Player) sender;
            if (player.hasPermission("mcordsync.player")) {
                if(args.length == 0){
                    player.sendMessage("");
                    player.sendMessage("§cDoğru kullanım: /discord-esle <bağla/kaldır>");
                    player.sendMessage("");
                    return true;
                }
                if (args[0].equalsIgnoreCase("bagla")|| args[0].equalsIgnoreCase("bağla")) {
                    if (DiscordUserManager.getUserData(player.getUniqueId()) != null) {
                        player.sendMessage("");
                        ((net.kyori.adventure.audience.Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.alreadySynced")));
                        player.sendMessage("");
                    } else {
                        String code = DiscordUserManager.generateCode(player.getUniqueId());
                        player.sendMessage("");
                        ((net.kyori.adventure.audience.Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.syncMessage"), Placeholder.styling("link", ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "http://" + ConfigurationHandler.getValue("bot.host") + ":" + ConfigurationHandler.getValue("bot.port") + "/?id=" + code))));
                        player.sendMessage("");
                    }
                }
                if (args[0].equalsIgnoreCase("kaldir") || args[0].equalsIgnoreCase("kaldır")) {
                    if (DiscordUserManager.getUserData(player.getUniqueId()) == null) {
                        player.sendMessage("");
                        ((net.kyori.adventure.audience.Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.alreadyUnsynced")));
                        player.sendMessage("");
                    } else {

                        DiscordUserManager.takeDiscordRoles(DiscordUserManager.getDiscordID(player.getUniqueId()));

                        DiscordUserManager.removeUserData(player.getUniqueId());

                        MCordSync.getInstance().getMySQL().deleteUser(player.getUniqueId().toString());

                        player.sendMessage("");
                        ((net.kyori.adventure.audience.Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.successfullyUnsync")));
                        player.sendMessage("");
                    }
                }
                return true;
            } else {
                player.sendMessage("");
                ((net.kyori.adventure.audience.Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.noPermission")));
                player.sendMessage("");
            }
            return true;
        }else{
            ((net.kyori.adventure.audience.Audience) sender).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.noConsole")));
        }
        return false;
    }
}
