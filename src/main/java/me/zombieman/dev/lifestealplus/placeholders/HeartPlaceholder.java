package me.zombieman.dev.lifestealplus.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.data.PlayerData;
import org.bukkit.entity.Player;

public class HeartPlaceholder extends PlaceholderExpansion {

    private final LifestealPlus plugin;


    public HeartPlaceholder(LifestealPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "lifesteal";
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
        if (identifier.equals("hearts")) {
            return PlayerData.getPlayerDataConfig(plugin, player.getUniqueId()).getString("hearts");
        }

        return null;
    }
}
