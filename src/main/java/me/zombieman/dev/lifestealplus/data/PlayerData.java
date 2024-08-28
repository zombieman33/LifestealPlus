package me.zombieman.dev.lifestealplus.data;


import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.events.PlayerDataChangeEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {

    public static final String DATA_FOLDER_NAME = "playerData";

    public static final ConcurrentHashMap<UUID, FileConfiguration> playerDataCache = new ConcurrentHashMap<>();

    public static void initDataFolder(LifestealPlus plugin) {
        File playerDataFolder = new File(plugin.getDataFolder(), PlayerData.DATA_FOLDER_NAME);
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }

    public static FileConfiguration getPlayerDataConfig(LifestealPlus plugin, Player player) {
        return getPlayerDataConfig(plugin, player.getUniqueId());
    }

    public static FileConfiguration getPlayerDataConfig(LifestealPlus plugin, UUID uuid) {
        FileConfiguration data = getCached(uuid);
        if (data != null) return data;

        File playerFile = getPlayerFile(plugin, uuid);
        if (!playerFile.exists()) {
            createFile(plugin, uuid);
        }

        data = YamlConfiguration.loadConfiguration(playerFile);
        cache(uuid, data);

        return data;
    }

    public static void createFile(LifestealPlus plugin, Player player) {
        createFile(plugin, player.getUniqueId());
    }

    public static void createFile(LifestealPlus plugin, UUID uuid) {
        File playerFile = getPlayerFile(plugin, uuid);

        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void savePlayerData(LifestealPlus plugin, UUID playerUUID) {
        File playerFile = getPlayerFile(plugin, playerUUID);

        // Load the old data from cache or from file if it's not cached
        FileConfiguration oldData = YamlConfiguration.loadConfiguration(playerFile);
        if (oldData == null) {
            oldData = YamlConfiguration.loadConfiguration(playerFile);
        }

        // Create a copy of the old data for modification
        FileConfiguration newData = getCached(playerUUID);

        // Trigger the custom event
        Player player = plugin.getServer().getPlayer(playerUUID);
        if (player != null) {
            if (oldData != null) {
                PlayerDataChangeEvent event = new PlayerDataChangeEvent(player, oldData, newData);
                plugin.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    try {
                        newData.save(playerFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        cache(playerUUID, newData);
    }



    @NotNull
    private static File getPlayerFile(LifestealPlus plugin, UUID playerUUID) {
        return new File(plugin.getDataFolder(), DATA_FOLDER_NAME + "/" + playerUUID + ".yml");
    }

    public static FileConfiguration getCached(UUID uuid) {
        if (uuid != null && playerDataCache.containsKey(uuid)) {
            return playerDataCache.get(uuid);
        }
        return null;
    }

    private static void cache(UUID uuid, FileConfiguration data) {
        playerDataCache.put(uuid, data);
    }

    public static void cleanupCache(Player player) {
        playerDataCache.remove(player.getUniqueId());
    }

    public static boolean checkPlayerExist(LifestealPlus plugin, String playerName) {
        File playerDataFolder = new File(plugin.getDataFolder(), DATA_FOLDER_NAME);
        if (playerDataFolder.exists() &&  playerDataFolder.isDirectory()) {
            for (File playerFile : playerDataFolder.listFiles()) {
                FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);
                String name = data.getString("name");
                if (playerName.equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }
    public static FileConfiguration getPlayerDataConfigByName(LifestealPlus plugin, String playerName) {
        File playerDataFolder = new File(plugin.getDataFolder(), DATA_FOLDER_NAME);
        if (playerDataFolder.exists() &&  playerDataFolder.isDirectory()) {
            for (File playerFile : playerDataFolder.listFiles()) {
                FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);
                String name = data.getString("name");
                if (playerName.equalsIgnoreCase(name)) {
                    return getPlayerDataConfig(plugin, UUID.fromString(playerFile.getName().replace(".yml", "")));
                }
            }
        }
        return null;
    }
    public static void savePlayerDataByName(LifestealPlus plugin, String playerName) {
        File playerDataFolder = new File(plugin.getDataFolder(), DATA_FOLDER_NAME);
        if (playerDataFolder.exists() && playerDataFolder.isDirectory()) {
            for (File playerFile : playerDataFolder.listFiles()) {
                FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);
                String name = data.getString("name");
                if (playerName.equalsIgnoreCase(name)) {
                    savePlayerData(plugin, UUID.fromString(playerFile.getName().replace(".yml", "")));
                }
            }
        }
    }
    public static List<String> getAllPlayerDataFiles(LifestealPlus plugin) {
        File playerDataFolder = new File(plugin.getDataFolder(), PlayerData.DATA_FOLDER_NAME);
        if (!playerDataFolder.exists() || !playerDataFolder.isDirectory()) {
            return Collections.emptyList();
        }

        File[] playerFiles = playerDataFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        if (playerFiles == null) {
            return Collections.emptyList();
        }

        List<String> playerFileNames = new ArrayList<>();
        for (File file : playerFiles) {
            String fileName = file.getName();
            if (fileName.endsWith(".yml")) {
                playerFileNames.add(fileName.substring(0, fileName.length() - 4));
            }
        }
        return playerFileNames;
    }
    public static List<String> getAllPlayerNames(LifestealPlus plugin) {
        List<String> playerNames = new ArrayList<>();
        File playerDataFolder = new File(plugin.getDataFolder(), PlayerData.DATA_FOLDER_NAME);

        if (playerDataFolder.exists() && playerDataFolder.isDirectory()) {
            for (File playerFile : playerDataFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"))) {
                FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);
                String name = data.getString("name");
                if (name != null) {
                    playerNames.add(name);
                }
            }
        }

        return playerNames;
    }

}