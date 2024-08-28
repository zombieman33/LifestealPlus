package me.zombieman.dev.lifestealplus;

import me.zombieman.dev.lifestealplus.commands.*;
import me.zombieman.dev.lifestealplus.crafting.HeartCrafting;
import me.zombieman.dev.lifestealplus.crafting.RespawnItemRecipe;
import me.zombieman.dev.lifestealplus.data.ItemData;
import me.zombieman.dev.lifestealplus.data.PlayerData;
import me.zombieman.dev.lifestealplus.listeners.*;
import me.zombieman.dev.lifestealplus.manager.DeathArenaManager;
import me.zombieman.dev.lifestealplus.manager.GuiManager;
import me.zombieman.dev.lifestealplus.manager.HeartManager;
import me.zombieman.dev.lifestealplus.manager.SettingsManager;
import me.zombieman.dev.lifestealplus.placeholders.HeartPlaceholder;
import me.zombieman.dev.lifestealplus.placeholders.TimerPlaceholder;
import org.bukkit.plugin.java.JavaPlugin;

public final class LifestealPlus extends JavaPlugin {
    public HeartManager heartManager;
    public HeartCrafting heartCrafting;
    public RespawnItemRecipe respawnItemRecipe;
    public SettingsManager settingsManager;
    private TimerPlaceholder timerPlaceholder;
    private HeartPlaceholder heartPlaceholder;
    public DeathArenaManager deathArenaManager;
    public GuiManager guiManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.settingsManager = new SettingsManager(this);

        this.heartManager = new HeartManager(this);

        PlayerData.initDataFolder(this);
        ItemData.initDataFolder(this);

        heartCrafting = new HeartCrafting(this);
        respawnItemRecipe = new RespawnItemRecipe(this);
        deathArenaManager = new DeathArenaManager(this);

        new JoinQuitListener(this);
        new DeathListener(this);
        new HeartRedeemListener(this);
        new PlayerListener(this);
        new RespawnRedeemListener(this);
        new PlayerDataChangeListener(this);

        guiManager = new GuiManager(this);

        saveDefaultConfig();

        getCommand("lifestealitem").setExecutor(new LifestealItemCmd(this));
        getCommand("withdraw").setExecutor(new WithdrawCmd(this));
        getCommand("lifesteal").setExecutor(new SettingsCmd(this));
        getCommand("deatharena").setExecutor(new DeathArenaSettingsCmd(this, deathArenaManager));
        getCommand("lifestealadmin").setExecutor(new LifestealAdminCmd(this));

        timerPlaceholder = new TimerPlaceholder(this);
        timerPlaceholder.register();

        heartPlaceholder = new HeartPlaceholder(this);
        heartPlaceholder.register();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (timerPlaceholder != null) timerPlaceholder.unregister();
        if (heartPlaceholder != null) heartPlaceholder.unregister();
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

}
