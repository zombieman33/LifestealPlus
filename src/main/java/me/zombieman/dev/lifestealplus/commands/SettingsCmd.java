package me.zombieman.dev.lifestealplus.commands;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.manager.SettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsCmd implements CommandExecutor, TabCompleter {

    private final SettingsManager settingsManager;
    private final LifestealPlus plugin;

    public SettingsCmd(LifestealPlus plugin) {
        this.plugin = plugin;
        this.settingsManager = plugin.getSettingsManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("lifestealplus.command.settings")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to run this command!");
            return false;
        }

        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: /lifesteal <setting> <value>");
            return false;
        }

        String key = args[0].toLowerCase();
        String value = args[1].toLowerCase();

        switch (key) {
            case "loseondeath":
                updateSetting(player, "Player Lose Heart On Death", value, this::handleBooleanSetting);
                break;
            case "loseondeathnpc":
                updateSetting(player, "Player Lose Heart On Death When Killer Is Not A Player", value, this::handleBooleanSetting);
                break;
            case "gainonkill":
                updateSetting(player, "Player Gain Heart On Kill", value, this::handleBooleanSetting);
                break;
            case "cap":
                updateSetting(player, "Heart Cap", value, this::handleIntegerSetting);
                break;
            case "minhearts":
                updateSetting(player, "Minimum Hearts", value, this::handleIntegerSetting);
                break;
            case "dropondeath":
                updateSetting(player, "Heart Item Drop On Death", value, this::handleBooleanSetting);
                break;
            case "heartcrafting":
                updateSetting(player, "Heart Item Crafting", value, this::handleBooleanSetting);
                break;
            case "respawncrafting":
                updateSetting(player, "Respawn Item Crafting", value, this::handleBooleanSetting);
                break;
            case "deatharena":
                updateSetting(player, "Teleport To Death Arena On Min Heart Death", value, this::handleBooleanSetting);
                break;
            case "heartsonrevive":
                updateSetting(player, "Hearts To Get When Revived", value, this::handleIntegerSetting);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown setting key: " + key);
                return false;
        }

        return true;
    }

    private void updateSetting(Player player, String settingName, String value, SettingHandler handler) {
        boolean success = handler.handle(player, settingName, value);
        if (success) {
            plugin.heartCrafting.reloadHeartRecipe();
            player.sendMessage(ChatColor.GREEN + "Successfully updated " + settingName + " to " + value + "!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        }
    }

    private boolean handleBooleanSetting(Player player, String settingName, String value) {
        boolean booleanValue;
        if (value.equals("true")) {
            booleanValue = true;
        } else if (value.equals("false")) {
            booleanValue = false;
        } else {
            player.sendMessage(ChatColor.RED + "Invalid value for " + settingName + ". Use 'true' or 'false'.");
            return false;
        }

        switch (settingName) {
            case "Player Lose Heart On Death":
                settingsManager.setPlayerLoseHeartOnDeath(booleanValue);
                break;
            case "Player Lose Heart On Death When Killer Is Not A Player":
                settingsManager.setPlayerLoseHeartOnDeathWhenKillerIsNotAPlayer(booleanValue);
                break;
            case "Player Gain Heart On Kill":
                settingsManager.setPlayerGainHeartOnKill(booleanValue);
                break;
            case "Heart Item Drop On Death":
                settingsManager.setHeartItemDropOnDeath(booleanValue);
                break;
            case "Respawn Item Crafting":
                settingsManager.setRespawnItemCrafting(booleanValue);
                break;
            case "Teleport To Death Arena On Min Heart Death":
                settingsManager.setTeleportToDeathArenaOnMinHeartDeath(booleanValue);
                break;
        }
        return true;
    }

    private boolean handleIntegerSetting(Player player, String settingName, String value) {
        int intValue;
        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid value for " + settingName + ". Use a number.");
            return false;
        }

        switch (settingName) {
            case "Heart Cap":
                settingsManager.setHeartCap(intValue);
                break;
            case "Minimum Hearts":
                settingsManager.setMinimumHearts(intValue);
                break;
            case "Hearts To Get When Revived":
                settingsManager.setHeartsWhenRevived(intValue);
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions = Arrays.asList("loseondeath", "loseondeathnpc", "gainonkill", "cap", "minhearts", "dropondeath", "heartcrafting", "respawncrafting", "deatharena", "heartsonrevive");
        } else if (args.length == 2) {
            String key = args[0].toLowerCase();
            if (Arrays.asList("loseondeath", "loseondeathnpc", "gainonkill", "dropondeath", "heartcrafting", "respawncrafting", "deatharena").contains(key)) {
                completions = Arrays.asList("true", "false");
            } else if (Arrays.asList("cap", "minhearts", "heartsonrevive").contains(key)) {
                completions = Arrays.asList("<amount>");
            }
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return completions.stream().filter(s -> s.startsWith(lastArg)).collect(Collectors.toList());
    }

    @FunctionalInterface
    private interface SettingHandler {
        boolean handle(Player player, String settingName, String value);
    }
}
