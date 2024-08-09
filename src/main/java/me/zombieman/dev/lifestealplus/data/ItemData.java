package me.zombieman.dev.lifestealplus.data;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ItemData {

    public static final String DATA_FOLDER_NAME = "itemData";
    private static final ConcurrentHashMap<String, FileConfiguration> itemDataCache = new ConcurrentHashMap<>();

    public static void initDataFolder(LifestealPlus plugin) {
        File itemDataFolder = new File(plugin.getDataFolder(), ItemData.DATA_FOLDER_NAME);
        if (!itemDataFolder.exists()) {
            itemDataFolder.mkdirs();
        }
    }

    public static FileConfiguration getItemDataConfig(LifestealPlus plugin, String id) {
        FileConfiguration data = getCached(id);
        if (data != null) return data;

        File itemFile = getItemFile(plugin, id);
        if (!itemFile.exists()) {
            createFile(plugin, id);
        }

        data = YamlConfiguration.loadConfiguration(itemFile);
        cache(id, data);

        return data;
    }

    public static void createFile(LifestealPlus plugin, String id) {
        File itemFile = getItemFile(plugin, id);

        if (!itemFile.exists()) {
            try {
                itemFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveItemData(LifestealPlus plugin, String id) {
        FileConfiguration data = getCached(id);
        File itemFile = getItemFile(plugin, id);

        try {
            if (data != null) data.save(itemFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save item data to " + itemFile.getName(), e);
        }
    }

    @NotNull
    private static File getItemFile(LifestealPlus plugin, String id) {
        return new File(plugin.getDataFolder(), DATA_FOLDER_NAME + "/item-" + id + ".yml");
    }

    public static FileConfiguration getCached(String id) {
        return itemDataCache.get(id);
    }

    private static void cache(String id, FileConfiguration data) {
        itemDataCache.put(id, data);
    }

    public static void cleanupCache(String id) {
        itemDataCache.remove(id);
    }

    public static void saveItem(LifestealPlus plugin, String id, ItemStack item) {
        FileConfiguration config = getItemDataConfig(plugin, id);
        config.set("item", item);
        saveItemData(plugin, id);
    }

    public static ItemStack loadItem(LifestealPlus plugin, String id) {
        FileConfiguration config = getItemDataConfig(plugin, id);
        return config.getItemStack("item");
    }

    public static boolean removeItem(LifestealPlus plugin, String id) {
        cleanupCache(id);
        File itemFile = getItemFile(plugin, id);
        return itemFile.delete();
    }

    public static void replaceItem(LifestealPlus plugin, String id, ItemStack newItem) {
        saveItem(plugin, id, newItem);
    }

    public static List<String> getAllItemIds(LifestealPlus plugin) {
        File itemDataFolder = new File(plugin.getDataFolder(), DATA_FOLDER_NAME);
        List<String> itemIds = new ArrayList<>();
        if (itemDataFolder.exists() && itemDataFolder.isDirectory()) {
            for (File file : itemDataFolder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".yml")) {
                    String id = file.getName().replace(".yml", "");
                    itemIds.add(id.replace("item-", ""));
                }
            }
        }
        return itemIds;
    }
    public static void saveAllItems(LifestealPlus plugin) {
        List<String> allItemIds = getAllItemIds(plugin);
        for (String id : allItemIds) {
            saveItemData(plugin, id);
        }
        Bukkit.getLogger().info("All item data saved successfully.");
    }
}
