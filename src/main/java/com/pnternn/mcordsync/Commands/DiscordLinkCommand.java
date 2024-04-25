package com.pnternn.mcordsync.Commands;

import com.pnternn.mcordsync.MCordSync;
import com.pnternn.mcordsync.Managers.DiscordLinkManager;
import com.pnternn.mcordsync.Config.ConfigurationHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DiscordLinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length >0){
                if(args[0].equalsIgnoreCase("link")) {
                    if (DiscordLinkManager.getUserData(player.getUniqueId()) != null){
                        net.kyori.adventure.audience.Audience.class.cast(player).sendMessage(Component.text("§3PirateSkyblock §7» §fHesabın bağlı! Bağlantıyı kaldırmak için").append(Component.text(" tıkla").color(TextColor.color(87,100,241))).hoverEvent(HoverEvent.showText(Component.text("§5Discord Hesap bilgileri\n\n§7 Kullanıcı adı: §e" + DiscordLinkManager.getUserData(player.getUniqueId()).getUsername() + "\n"))).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/discord unlink")));
                    }else{
                        String code = DiscordLinkManager.generateCode(player.getUniqueId());
                        player.sendMessage("");
                        net.kyori.adventure.audience.Audience.class.cast(player).sendMessage(Component.text("§3PirateSkyblock §7» §fDiscord hesabınızı bağlamak için")
                                .append(Component.text(" tıkla").color(TextColor.color(87,100,241)))
                                .hoverEvent(HoverEvent.showText(Component.text("Discord hesabınızı bağlamak için tıklayın")))
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "http://"+ ConfigurationHandler.getValue("bot.host") +":"+ConfigurationHandler.getValue("bot.port")+"/?id=" + code)));
                        player.sendMessage("");
                    }
                }if(args[0].equalsIgnoreCase("unlink")){
                    if(DiscordLinkManager.getUserData(player.getUniqueId()) == null){
                        net.kyori.adventure.audience.Audience.class.cast(player).sendMessage(Component.text("§3PirateSkyblock §7» §fHesabın bağlı değil! Bağlamak için").append(Component.text(" tıkla").color(TextColor.color(87,100,241)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/discord link"))).hoverEvent(HoverEvent.showText(Component.text("Discord hesabınızı bağlamak için tıklayın"))));
                    }else{
                        DiscordLinkManager.takeDiscordRoles(DiscordLinkManager.getDiscordID(player.getUniqueId()));
                        DiscordLinkManager.removeUserData(player.getUniqueId());
                        MCordSync.getInstance().getMySQL().deleteUser(player.getUniqueId().toString());
                        player.sendMessage("§3PirateSkyblock §7» §fBağlantı başarıyla kaldırıldı");
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
