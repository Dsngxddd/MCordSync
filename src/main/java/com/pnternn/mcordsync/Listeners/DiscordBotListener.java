package com.pnternn.mcordsync.Listeners;

import com.pnternn.mcordsync.MCordSync;
import com.pnternn.mcordsync.Managers.DiscordLinkManager;
import com.pnternn.mcordsync.Models.DiscordUserData;
import com.pnternn.mcordsync.Config.ConfigurationHandler;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.jetbrains.annotations.NotNull;

public class DiscordBotListener extends ListenerAdapter {

    public DiscordBotListener() {
        LuckPerms luckPerms = LuckPermsProvider.get();
        EventBus eventBus = luckPerms.getEventBus();
        eventBus.subscribe(MCordSync.getInstance(), NodeRemoveEvent.class, this::onNodeRemoveEvent);
        eventBus.subscribe(MCordSync.getInstance(), NodeAddEvent.class, this::onNodeAddEvent);
    }
    private void onNodeAddEvent(NodeAddEvent event){
        if (!event.isUser()) {
            return;
        }
        User target = (User) event.getTarget();
        Node node = event.getNode();

        MCordSync.getInstance().getServer().getScheduler().runTask(MCordSync.getInstance(), () -> {
            DiscordUserData userData = DiscordLinkManager.getUserData(target.getUniqueId());
            if(userData == null){
                return;
            }
            if (node instanceof PermissionNode) {
                for (String key : ConfigurationHandler.getKeys("roles")) {
                    if (node.getKey().equals(ConfigurationHandler.getValue("roles." + key + ".permission"))) {
                        DiscordLinkManager.giveDiscordRole(userData.getDiscordID(), ConfigurationHandler.getValue("roles." + key + ".id"));
                    }
                }
            }
        });
    }
    private void onNodeRemoveEvent(NodeRemoveEvent event){
        if (!event.isUser()) {
            return;
        }
        User target = (User) event.getTarget();
        Node node = event.getNode();

        MCordSync.getInstance().getServer().getScheduler().runTask(MCordSync.getInstance(), () -> {
            DiscordUserData userData = DiscordLinkManager.getUserData(target.getUniqueId());
            if(userData == null){
                return;
            }
            if (node instanceof PermissionNode) {
                for (String key : ConfigurationHandler.getKeys("roles")) {
                    if (node.getKey().equals(ConfigurationHandler.getValue("roles." + key + ".permission"))) {
                        DiscordLinkManager.takeDiscordRole(userData.getDiscordID(), ConfigurationHandler.getValue("roles." + key + ".id"));
                    }
                }
            }
        });
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if(event.getGuild().getId().equals(ConfigurationHandler.getValue("guild.id"))){
            DiscordLinkManager.giveDiscordRoles(event.getUser().getId());
        }
    }

}
