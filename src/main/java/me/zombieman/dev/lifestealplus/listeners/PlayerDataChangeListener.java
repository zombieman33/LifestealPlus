package me.zombieman.dev.lifestealplus.listeners;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.data.PlayerData;
import me.zombieman.dev.lifestealplus.events.PlayerDataChangeEvent;
import me.zombieman.dev.lifestealplus.manager.HeartManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerDataChangeListener implements Listener {

    private final LifestealPlus plugin;

    public PlayerDataChangeListener(LifestealPlus plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDataChange(PlayerDataChangeEvent event) {

        Player player = event.getPlayer();

        FileConfiguration newData = event.getNewData();

        if (newData.getBoolean("needsToBeTeleportedOut")) {
            Location loc = plugin.deathArenaManager.teleportOutLocation;
            if (loc == null) {
                player.sendMessage(ChatColor.RED + "You couldn't be teleported since the location out isn't set.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }

            PlayerData.getPlayerDataConfig(plugin, player.getUniqueId()).set("needsToBeTeleportedOut", null);
            PlayerData.savePlayerData(plugin, player.getUniqueId());

            player.sendMessage(ChatColor.GREEN + "You have been revived!");
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
            plugin.heartManager.setHeart(player, plugin.settingsManager.getHeartsWhenRevived());
            player.teleport(loc);
        }

    }

}
