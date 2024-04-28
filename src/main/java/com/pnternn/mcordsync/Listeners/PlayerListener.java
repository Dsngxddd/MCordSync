package com.pnternn.mcordsync.Listeners;

import com.pnternn.mcordsync.Config.ConfigurationHandler;
import com.pnternn.mcordsync.MCordSync;
import com.pnternn.mcordsync.Managers.PlayerManager;
import com.pnternn.mcordsync.Models.PlayerData;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if(PlayerManager.getPlayer(p.getUniqueId())==null)
        {
            PlayerManager.getPlayers().add(new PlayerData(p.getUniqueId(), null, 0));
            MCordSync.getInstance().getMySQL().createPlayer(p.getUniqueId());
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        for (PlayerData player : PlayerManager.getPlayers()) {
            if (player.getUUID().equals(p.getUniqueId())) {
                if(player.getMuteExpire()!=null)
                {
                    if(player.getMuteExpire().isAfter(LocalDateTime.now()))
                    {
                        event.setCancelled(true);
                        MiniMessage mm = MiniMessage.miniMessage();
                        Duration remaining = Duration.between(LocalDateTime.now(), player.getMuteExpire());
                        ((net.kyori.adventure.audience.Audience) p).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.playerMuted"), Placeholder.parsed("time", remaining.toString())));
                        return;
                    }else{
                        player.setMuteExpire(null);
                        MCordSync.getInstance().getMySQL().mutePlayer(player.getUUID(), null);
                    }
                }
                player.getMessages().put(LocalDateTime.now(), event.getMessage());
                if(player.getMessages().size()>=11)
                {
                    player.getMessages().remove(player.getMessages().keySet().toArray()[0]);
                }
            }
        }
    }
    @EventHandler
    public void onClick(PlayerInteractEvent event)
    {
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            int newCount = 1;
            Player p = event.getPlayer();
            for (PlayerData player : PlayerManager.getPlayers()) {
                if (player.getUUID().equals(p.getUniqueId())) {
                    newCount = newCount + player.getClickCount();
                    player.setPlayerClickCount(newCount);
                }
            }
        }
    }
}
