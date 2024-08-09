package me.zombieman.dev.lifestealplus.manager;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DeathArenaManager implements Listener {

    private final LifestealPlus plugin;
    public Location teleportInLocation;
    public Location teleportOutLocation;

    public DeathArenaManager(LifestealPlus plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        reload();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void loadLocations() {
        if (plugin.getConfig().contains("Death Arena.TeleportIn")) {
            teleportInLocation = (Location) plugin.getConfig().get("Death Arena.TeleportIn");
        }
        if (plugin.getConfig().contains("Death Arena.TeleportOut")) {
            teleportOutLocation = (Location) plugin.getConfig().get("Death Arena.TeleportOut");
        }
    }

    private void reload() {
        loadLocations();
    }

    public void setTeleportInLocation(Location location) {
        this.teleportInLocation = location;
    }

    public void setTeleportOutLocation(Location location) {
        this.teleportOutLocation = location;
    }

    public void teleportPlayerIn(Player player) {
        if (teleportInLocation != null) {
            player.teleport(teleportInLocation);
            PlayerData.getPlayerDataConfig(plugin, player).set("inDeathArena", true);
            PlayerData.savePlayerData(plugin, player.getUniqueId());
            startDeathArenaTimer(player);
        } else {
            player.sendMessage("Death Arena In location is not set.");
        }
    }

    public Location getTeleportInLocation() {
        return teleportInLocation;
    }
    public Location getTeleportOutLocation() {
        return teleportOutLocation;
    }

    public void teleportPlayerOut(Player player) {
        if (teleportOutLocation != null) {
            player.teleport(teleportOutLocation);
            PlayerData.getPlayerDataConfig(plugin, player).set("inDeathArena", false);
            PlayerData.savePlayerData(plugin, player.getUniqueId());
        } else {
            player.sendMessage("Death Arena Out location is not set.");
        }
    }

    private void startDeathArenaTimer(Player player) {
        if (plugin.getConfig().getBoolean("Death Arena.Time")) {
            int days = plugin.getConfig().getInt("Death Arena.Timer");
            long timeInTicks = days * 24 * 60 * 60 * 20L; // Convert days to ticks
            UUID playerUUID = player.getUniqueId();
            PlayerData.getPlayerDataConfig(plugin, player).set("deathArenaEntryTime", System.currentTimeMillis());
            PlayerData.savePlayerData(plugin, playerUUID);

            new BukkitRunnable() {
                @Override
                public void run() {
                    teleportPlayerOut(player);
                    PlayerData.getPlayerDataConfig(plugin, playerUUID).set("deathArenaEntryTime", false);
                    PlayerData.savePlayerData(plugin, playerUUID);
                }
            }.runTaskLater(plugin, timeInTicks);
        }
    }
    public boolean isPlayerInDeathArena(Player player) {
        return PlayerData.getPlayerDataConfig(plugin, player).getBoolean("inDeathArena", false);
    }
    public boolean isPlayerInDeathArenaUUID(UUID uuid) {
        return PlayerData.getPlayerDataConfig(plugin, uuid).getBoolean("inDeathArena", false);
    }

    public void removePlayerFromDeathArena(UUID uuid) {
        PlayerData.getPlayerDataConfig(plugin, uuid).set("inDeathArena", null);
        PlayerData.getPlayerDataConfig(plugin, uuid).set("deathArenaEntryTime", null);
        PlayerData.getPlayerDataConfig(plugin, uuid).set("needsToBeTeleportedOut", true);
        PlayerData.savePlayerData(plugin, uuid);
    }
}
