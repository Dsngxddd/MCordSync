package com.pnternn.mcordsync.Commands;

import com.pnternn.mcordsync.MCordSync;
import com.pnternn.mcordsync.Managers.DiscordReportManager;
import com.pnternn.mcordsync.Config.ConfigurationHandler;
import com.pnternn.mcordsync.Managers.DiscordUserManager;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiscordAdminCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        MiniMessage mm = MiniMessage.miniMessage();
        if(sender instanceof Player){
            Player player = (Player) sender;
            if (player.hasPermission("mcordsync.admin")) {
                if(args.length == 0){
                    player.sendMessage("");
                    ((Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.mcordsync-admin-usage")));
                    player.sendMessage("");
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")|| args[0].equalsIgnoreCase("yenile")) {
                    {
                        ConfigurationHandler.reloadConfig();
                        player.sendMessage(ChatColor.RED + "Plugin Reloaded");
                    }
                }
                if ((args[0].equalsIgnoreCase("unsync") || args[0].equalsIgnoreCase("hesapsil")) && args.length >= 2) {
                    OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
                    if (targetPlayer == null || !targetPlayer.hasPlayedBefore()) {
                        ((Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.reportnotfound")));
                        return true;
                    }
                    DiscordUserManager.takeDiscordRoles(DiscordUserManager.getDiscordID(targetPlayer.getUniqueId()));
                    DiscordUserManager.removeUserData(targetPlayer.getUniqueId());
                    MCordSync.getInstance().getMySQL().deleteUser(targetPlayer.getUniqueId().toString());
                    player.sendMessage(ChatColor.RED + targetPlayer.getName() + "&cAdlı Oyuncunun verileri silindi");
                } else if (args[0].equalsIgnoreCase("hesapkaldir") || args[0].equalsIgnoreCase("hesapsil")) {
                    ((Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.mcordsync-admin-delete")));
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
