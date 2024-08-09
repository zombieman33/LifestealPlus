package me.zombieman.dev.lifestealplus.manager;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import org.bukkit.configuration.file.FileConfiguration;

public class SettingsManager {

    private final LifestealPlus plugin;
    private FileConfiguration config;

    public SettingsManager(LifestealPlus plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        loadSettings();
    }

    public void loadSettings() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public boolean isPlayerLoseHeartOnDeath() {
        return config.getBoolean("settings.Player Lose Heart On Death", true);
    }

    public void setPlayerLoseHeartOnDeath(boolean value) {
        config.set("settings.Player Lose Heart On Death", value);
        saveConfig();
    }

    public boolean isPlayerLoseHeartOnDeathWhenKillerIsNotAPlayer() {
        return config.getBoolean("settings.Player Lose Heart On Death When Killer Is Not A Player", true);
    }

    public void setPlayerLoseHeartOnDeathWhenKillerIsNotAPlayer(boolean value) {
        config.set("settings.Player Lose Heart On Death When Killer Is Not A Player", value);
        saveConfig();
    }

    public boolean isPlayerGainHeartOnKill() {
        return config.getBoolean("settings.Player Gain Heart On Kill", true);
    }

    public void setPlayerGainHeartOnKill(boolean value) {
        config.set("settings.Player Gain Heart On Kill", value);
        saveConfig();
    }

    public int getHeartCap() {
        return config.getInt("settings.Heart Cap", 20);
    }

    public void setHeartCap(int value) {
        config.set("settings.Heart Cap", value);
        saveConfig();
    }
    public int getHeartsWhenRevived() {
        return config.getInt("settings.Hearts To Get When Revived", 1);
    }

    public void setHeartsWhenRevived(int value) {
        config.set("settings.Hearts To Get When Revived", value);
        saveConfig();
    }

    public int getMinimumHearts() {
        saveConfig();
        return config.getInt("settings.Minimum Hearts", 1);
    }

    public void setMinimumHearts(int value) {
        config.set("settings.Minimum Hearts", value);
        saveConfig();
    }

    public boolean isHeartItemDropOnDeath() {
        saveConfig();
        return config.getBoolean("settings.Heart Item Drop On Death", false);
    }

    public void setHeartItemDropOnDeath(boolean value) {
        config.set("settings.Heart Item Drop On Death", value);
        saveConfig();
    }

    public boolean isHeartItemCrafting() {
        saveConfig();
        return config.getBoolean("settings.Heart Item Crafting", true);
    }

    public void setHeartItemCrafting(boolean value) {
        config.set("settings.Heart Item Crafting", value);
        saveConfig();
    }
    public boolean isRespawnItemCrafting() {
        saveConfig();
        return config.getBoolean("settings.Respawn Item Crafting", true);
    }

    public void setRespawnItemCrafting(boolean value) {
        config.set("settings.Respawn Item Crafting", value);
        saveConfig();
    }
    public boolean getTeleportToDeathArenaOnMinHeartDeath() {
        saveConfig();
        return config.getBoolean("settings.Teleport To Death Arena On Min Heart Death", true);
    }

    public void setTeleportToDeathArenaOnMinHeartDeath(boolean value) {
        config.set("settings.Teleport To Death Arena On Min Heart Death", value);
        saveConfig();
    }

    private void saveConfig() {
        plugin.saveConfig();
    }
}
