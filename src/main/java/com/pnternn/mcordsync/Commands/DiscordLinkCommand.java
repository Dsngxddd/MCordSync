package com.pnternn.mcordsync.Commands;

import com.pnternn.mcordsync.MCordSync;
import com.pnternn.mcordsync.Managers.DiscordLinkManager;
import com.pnternn.mcordsync.Config.ConfigurationHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DiscordLinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        MiniMessage mm = MiniMessage.miniMessage();
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length >0){
                if(player.hasPermission("mcordsync.player")){
                    if(args[0].equalsIgnoreCase("link")) {

                        if (DiscordLinkManager.getUserData(player.getUniqueId()) != null){
                            player.sendMessage("");
                            net.kyori.adventure.audience.Audience.class.cast(player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.alreadySynced")));
                            player.sendMessage("");
                        }else{
                            String code = DiscordLinkManager.generateCode(player.getUniqueId());
                            player.sendMessage("");
                            net.kyori.adventure.audience.Audience.class.cast(player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.syncMessage"), Placeholder.styling("link", ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "http://"+ConfigurationHandler.getValue("bot.host")+":"+ConfigurationHandler.getValue("bot.port")+"/?id=" + code))));
                            player.sendMessage("");
                        }
                    }if(args[0].equalsIgnoreCase("unlink")){
                        if(DiscordLinkManager.getUserData(player.getUniqueId()) == null){
                            player.sendMessage("");
                            net.kyori.adventure.audience.Audience.class.cast(player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.alreadyUnsynced")));
                            player.sendMessage("");
                        }else{
                            DiscordLinkManager.takeDiscordRoles(DiscordLinkManager.getDiscordID(player.getUniqueId()));
                            DiscordLinkManager.removeUserData(player.getUniqueId());
                            MCordSync.getInstance().getMySQL().deleteUser(player.getUniqueId().toString());
                            player.sendMessage("");
                            net.kyori.adventure.audience.Audience.class.cast(player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.successfullyUnsync")));
                            player.sendMessage("");
                        }
                    }
                }else{
                    player.sendMessage("");
                    net.kyori.adventure.audience.Audience.class.cast(player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.noPermission")));
                    player.sendMessage("");

                }
            }
            return true;
        }else{
            net.kyori.adventure.audience.Audience.class.cast(sender).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.noConsole")));
        }
        return false;
    }
}
