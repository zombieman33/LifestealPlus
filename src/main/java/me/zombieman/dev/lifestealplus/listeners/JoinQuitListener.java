package me.zombieman.dev.lifestealplus.listeners;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.data.PlayerData;
import me.zombieman.dev.lifestealplus.enums.HeartCap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final LifestealPlus plugin;

    public JoinQuitListener(LifestealPlus plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (PlayerData.getPlayerDataConfig(plugin, player.getUniqueId()).getBoolean("needsToBeTeleportedOut")) {
            Location teleportOutLocation = plugin.deathArenaManager.teleportOutLocation;
            if (teleportOutLocation != null) {

                PlayerData.getPlayerDataConfig(plugin, player.getUniqueId()).set("needsToBeTeleportedOut", false);
                PlayerData.savePlayerData(plugin, player.getUniqueId());
                player.teleport(teleportOutLocation);

                plugin.heartManager.setHeart(player, plugin.settingsManager.getHeartsWhenRevived());
                player.sendMessage(ChatColor.GREEN + "You have been revived!");

            } else {
                player.sendMessage(ChatColor.RED + "You couldn't be teleported from the death arena since there isn't a location set.");
            }
        }

        PlayerData.getPlayerDataConfig(plugin, player).set("name", player.getName());
        PlayerData.getPlayerDataConfig(plugin, player).set("uuid", player.getUniqueId().toString());

        if (PlayerData.getPlayerDataConfig(plugin, player).getString("hearts") == null) {
            PlayerData.getPlayerDataConfig(plugin, player).set("hearts", 10);
        }

        PlayerData.savePlayerData(plugin, player.getUniqueId());

        int hearts = PlayerData.getPlayerDataConfig(plugin, player).getInt("hearts");

        if (plugin.heartManager.heartCap == HeartCap.HEART_CAP && hearts > plugin.settingsManager.getHeartCap()) hearts = plugin.settingsManager.getHeartCap();

        player.setMaxHealth(hearts * 2);

        if (plugin.deathArenaManager.isPlayerInDeathArena(player)) {
            player.teleport(plugin.deathArenaManager.teleportInLocation);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> PlayerData.cleanupCache(event.getPlayer()));
    }

}
