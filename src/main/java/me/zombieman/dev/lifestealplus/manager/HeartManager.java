package me.zombieman.dev.lifestealplus.manager;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.data.PlayerData;
import me.zombieman.dev.lifestealplus.enums.HeartCap;
import org.bukkit.entity.Player;

public class HeartManager {
    public HeartCap heartCap;
    private final LifestealPlus plugin;

    public HeartManager(LifestealPlus plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        if (plugin.getConfig().getString("settings.Heart Cap") != null &&
                !plugin.getConfig().getString("settings.Heart Cap").isEmpty() &&
                plugin.getSettingsManager().getHeartCap() > 0) {
            heartCap = HeartCap.HEART_CAP;
        } else {
            heartCap = HeartCap.NONE;
        }
    }

    public void addHeart(Player player, int amount) {
        int heart = PlayerData.getPlayerDataConfig(plugin, player.getUniqueId()).getInt("hearts");
        PlayerData.getPlayerDataConfig(plugin, player.getUniqueId()).set("hearts", heart + amount);
        PlayerData.savePlayerData(plugin, player.getUniqueId());

        heart = (heart + amount) * 2;
        player.setMaxHealth(heart);
    }

    public void removeHeart(Player player, int amount) {
        int heart = PlayerData.getPlayerDataConfig(plugin, player.getUniqueId()).getInt("hearts");
        PlayerData.getPlayerDataConfig(plugin, player.getUniqueId()).set("hearts", heart - amount);
        PlayerData.savePlayerData(plugin, player.getUniqueId());

        heart = (heart - amount) * 2;
        player.setMaxHealth(heart);
    }

    public void setHeart(Player player, int amount) {
        PlayerData.getPlayerDataConfig(plugin, player.getUniqueId()).set("hearts", amount);
        PlayerData.savePlayerData(plugin, player.getUniqueId());

        player.setMaxHealth(amount * 2);
    }

    public int getHeart(Player player) {
        return PlayerData.getPlayerDataConfig(plugin, player.getUniqueId()).getInt("hearts");
    }
}
