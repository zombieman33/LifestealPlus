package me.zombieman.dev.lifestealplus.commands;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.manager.DeathArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

public class DeathArenaSettingsCmd implements CommandExecutor, TabCompleter {

    private final LifestealPlus plugin;
    private final DeathArenaManager deathArenaManager;

    public DeathArenaSettingsCmd(LifestealPlus plugin, DeathArenaManager deathArenaManager) {
        this.plugin = plugin;
        this.deathArenaManager = deathArenaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("lifestealplus.command.deatharena")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return false;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /deatharena <setmove|setcommands|settime|setdamage|setplace|setbreak|setpickup|setdrop|settimer|setteleportin|setteleportout>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "setmove":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /deatharena setmove <true|false>");
                    return true;
                }
                boolean move = Boolean.parseBoolean(args[1]);
                plugin.getConfig().set("Death Arena.Move", move);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Move in Death Arena set to " + move);
                break;

            case "setcommands":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /deatharena setcommands <true|false>");
                    return true;
                }
                boolean commands = Boolean.parseBoolean(args[1]);
                plugin.getConfig().set("Death Arena.Commands", commands);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Commands in Death Arena set to " + commands);
                break;

            case "settime":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /deatharena settime <true|false>");
                    return true;
                }
                boolean time = Boolean.parseBoolean(args[1]);
                plugin.getConfig().set("Death Arena.Time", time);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Time restriction in Death Arena set to " + time);
                break;
            case "setbreak":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /deatharena setbreak <true|false>");
                    return true;
                }
                boolean setbreak = Boolean.parseBoolean(args[1]);
                plugin.getConfig().set("Death Arena.Break", setbreak);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Breaking blocks in Death Arena set to " + setbreak);
                break;
            case "setplace":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /deatharena setplace <true|false>");
                    return true;
                }
                boolean setPlace = Boolean.parseBoolean(args[1]);
                plugin.getConfig().set("Death Arena.Place", setPlace);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Placing blocks in Death Arena set to " + setPlace);
                break;
            case "setdamage":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /deatharena setdamage <true|false>");
                    return true;
                }
                boolean setDamage = Boolean.parseBoolean(args[1]);
                plugin.getConfig().set("Death Arena.Damage", setDamage);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Damaging players in Death Arena set to " + setDamage);
                break;
            case "setpickup":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /deatharena setpickup <true|false>");
                    return true;
                }
                boolean setpickup = Boolean.parseBoolean(args[1]);
                plugin.getConfig().set("Death Arena.Item-Pickup", setpickup);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Player pickup in Death Arena set to " + setpickup);
                break;

            case "setdrop":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /deatharena setdrop <true|false>");
                    return true;
                }
                boolean setdrop = Boolean.parseBoolean(args[1]);
                plugin.getConfig().set("Death Arena.Item-Drop", setdrop);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Player drop in Death Arena set to " + setdrop);
                break;

            case "settimer":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /deatharena settimer <time_in_days>");
                    return true;
                }
                int timer = Integer.parseInt(args[1]);
                plugin.getConfig().set("Death Arena.Timer", timer);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Timer for Death Arena set to " + timer + " days");
                break;

            case "setteleportin":
                Location locationIn = player.getLocation();
                deathArenaManager.setTeleportInLocation(locationIn);
                plugin.getConfig().set("Death Arena.TeleportIn", locationIn);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Teleport in location set.");
                break;

            case "setteleportout":
                Location locationOut = player.getLocation();
                deathArenaManager.setTeleportOutLocation(locationOut);
                plugin.getConfig().set("Death Arena.TeleportOut", locationOut);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Teleport out location set.");
                break;

            default:
                player.sendMessage(ChatColor.RED + "Unknown subcommand.");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions = Arrays.asList("setmove", "setcommands", "setbreak", "setpickup", "setdrop", "setplace", "setdamage", "settime", "settimer", "setteleportin", "setteleportout");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setmove") || args[0].equalsIgnoreCase("setcommands") || args[0].equalsIgnoreCase("settime") || args[0].equalsIgnoreCase("setbreak") || args[0].equalsIgnoreCase("setplace") || args[0].equalsIgnoreCase("setdamage") || args[0].equalsIgnoreCase("setpickup") || args[0].equalsIgnoreCase("setdrop")) {
                completions = Arrays.asList("true", "false");
            } else if (args[0].equalsIgnoreCase("settimer")) {
                completions = Arrays.asList("<timer>", "1", "7", "30", "60");
            }
        }
        String lastArg = args[args.length - 1].toLowerCase();
        return completions.stream().filter(s -> s.toLowerCase().startsWith(lastArg.toLowerCase())).collect(Collectors.toList());
    }
}
