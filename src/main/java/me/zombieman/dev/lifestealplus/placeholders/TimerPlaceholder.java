package me.zombieman.dev.lifestealplus.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.data.PlayerData;
import org.bukkit.entity.Player;

public class TimerPlaceholder extends PlaceholderExpansion {

    private final LifestealPlus plugin;


    public TimerPlaceholder(LifestealPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "deatharenatimer";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equals("remaining_time")) {
            if (!plugin.getConfig().getBoolean("Death Arena.Time")) {
                return "You can only get out by using the revive item.";
            }

            long entryTime = PlayerData.getPlayerDataConfig(plugin, player).getLong("deathArenaEntryTime");
            if (entryTime == 0) {
                return "Rejoin to be revived!";
            }

            long timeElapsed = System.currentTimeMillis() - entryTime;
            long timeLeft = (plugin.getConfig().getInt("Death Arena.Timer") * 24 * 60 * 60 * 1000L) - timeElapsed;

            if (timeLeft <= 0) {
                plugin.deathArenaManager.removePlayerFromDeathArena(player.getUniqueId());
                return "You're revived!";
            }

            long daysLeft = timeLeft / (24 * 60 * 60 * 1000);
            long hoursLeft = (timeLeft / (60 * 60 * 1000)) % 24;
            long minutesLeft = (timeLeft / (60 * 1000)) % 60;

            return daysLeft + "d " + hoursLeft + "h " + minutesLeft + "m";
        }

        return null;
    }
}
