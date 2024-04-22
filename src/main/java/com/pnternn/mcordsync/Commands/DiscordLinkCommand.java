package com.pnternn.mcordsync.Commands;

import com.pnternn.mcordsync.MCordSync;
import com.pnternn.mcordsync.Managers.DiscordLinkManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class DiscordLinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length >0){
                if(args[0].equalsIgnoreCase("link")) {
                    if (DiscordLinkManager.getUserData(player.getUniqueId()) != null){
                        player.sendMessage("Hesabınız zaten bağlı. Bağlantıyı kaldırmak için /discord unlink yazın");
                    }else{
                        String code = DiscordLinkManager.generateCode(player.getUniqueId());
                        player.sendMessage("");
                        player.sendMessage(Component.text("Discord hesabınızı bağlamak için")
                                .append(Component.text(" tıkla").color(TextColor.color(87,100,241)))
                                .hoverEvent(HoverEvent.showText(Component.text("Discord hesabınızı bağlamak için tıklayın")))
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "http://127.0.0.1:800/?id=" + code)));
                        player.sendMessage("");
                    }
                }if(args[0].equalsIgnoreCase("unlink")){
                    if(DiscordLinkManager.getUserData(player.getUniqueId()) == null){
                        player.sendMessage("Hesabınız zaten bağlı değil. Bağlamak için /discord link yazın");
                    }else{
                        DiscordLinkManager.removeUserData(player.getUniqueId());
                        MCordSync.getInstance().getMySQL().deleteUser(player.getUniqueId().toString());
                        player.sendMessage("Bağlantı başarıyla kaldırıldı");
                    }
                }
            }



            return true;
        }else{
            sender.sendMessage("You must be a player to use this command");
        }
        return false;
    }
}
