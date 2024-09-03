package com.pnternn.mcordsync.placeholder;

import com.pnternn.mcordsync.Managers.DiscordUserManager;
import com.pnternn.mcordsync.Managers.PlayerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MCordSyncExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "mcordsync";
    }

    @Override
    public @NotNull String getAuthor() {
        return "cengiz1x, pnternn";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }
        if (identifier.equals("discordname")) {
            String discordID = DiscordUserManager.getDiscordID(player.getUniqueId());
            if (discordID != null) {
                return DiscordUserManager.getUserData(discordID).getUsername();
            } else {
                return "Unkown";
            }
        }

        return null;
    }
}
