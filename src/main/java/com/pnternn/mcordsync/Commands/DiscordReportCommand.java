package com.pnternn.mcordsync.Commands;

import com.pnternn.mcordsync.Managers.DiscordReportManager;
import com.pnternn.mcordsync.Config.ConfigurationHandler;
import com.pnternn.mcordsync.Managers.DiscordUserManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

public class DiscordReportCommand implements CommandExecutor {

    private static final HashMap<UUID, LocalDateTime> lastReportTime = new HashMap<>();
    private static final long REPORT_COOLDOWN_MINUTES = 30;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        MiniMessage mm = MiniMessage.miniMessage();
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (DiscordUserManager.getUserData(player.getUniqueId()) == null) {
                player.sendMessage("");
                ((net.kyori.adventure.audience.Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.alreadyUnsynced")));
                player.sendMessage("");
                return true;
            }

            if (!player.hasPermission("mcordsync.player")) {
                player.sendMessage("");
                ((Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.noPermission")));
                player.sendMessage("");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage("");
                ((Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.reportusage")));
                player.sendMessage("");
                return true;
            }

            OfflinePlayer reportedPlayer = Bukkit.getOfflinePlayer(args[0]);
            if (reportedPlayer == null || !reportedPlayer.hasPlayedBefore()) {
                ((Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.reportnotfound")));
                return true;
            }

            if (reportedPlayer.getUniqueId().equals(player.getUniqueId())) {
                ((Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.selfreport")));
                return true;
            }

            LocalDateTime now = LocalDateTime.now();
            if (lastReportTime.containsKey(player.getUniqueId())) {
                LocalDateTime lastReport = lastReportTime.get(player.getUniqueId());
                if (lastReport.plusMinutes(REPORT_COOLDOWN_MINUTES).isAfter(now)) {
                    ((Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.reportcooldown")));
                    return true;
                }
            }

            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                reasonBuilder.append(args[i]).append(" ");
            }
            String reason = reasonBuilder.toString().trim();

            String date = now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

            try {
                DiscordReportManager.createReport(player.getUniqueId(), reportedPlayer.getUniqueId(), reason, date, "open", true);
                ((Audience) player).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.reportcreate")));
                lastReportTime.put(player.getUniqueId(), now);
            } catch (NullPointerException e) {
                player.sendMessage("§cError.");
                e.printStackTrace();
            }

            return true;
        } else {
            ((Audience) sender).sendMessage(mm.deserialize(ConfigurationHandler.getValue("messages.noConsole")));
        }
        return false;
    }
}
