package me.zombieman.dev.lifestealplus.commands;

import com.sun.tools.jconsole.JConsoleContext;
import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.crafting.HeartCrafting;
import me.zombieman.dev.lifestealplus.crafting.RespawnItemRecipe;
import me.zombieman.dev.lifestealplus.data.ItemData;
import me.zombieman.dev.lifestealplus.data.PlayerData;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LifestealAdminCmd implements CommandExecutor, TabCompleter {
    private final LifestealPlus plugin;
    public LifestealAdminCmd(LifestealPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("lifestealplus.command.lifestealadmin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to run this command!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1.0f, 1.0f);
            return false;
        }

        if (args.length >= 3) {

            String targetName = args[1];

            if (!PlayerData.checkPlayerExist(plugin, targetName)) {
                player.sendMessage(ChatColor.RED + targetName + " doesn't exist.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return false;
            }
            FileConfiguration config = PlayerData.getPlayerDataConfigByName(plugin, targetName);

            String uuidStr = config.getString("uuid");

            if (uuidStr == null) {
                player.sendMessage(ChatColor.RED + targetName + "'s uuid doesn't exist, please tell them to rejoin!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return false;
            }

            int amount;

            try {
                amount = Integer.parseInt(args[2]) * 2;
            } catch (IllegalArgumentException e) {
                amount = 0;
            }

            int hearts = config.getInt("hearts") * 2;

            if (args[0].equalsIgnoreCase("add")) {

                hearts = hearts + amount;

                config.set("hearts", (hearts / 2));
                PlayerData.savePlayerData(plugin, UUID.fromString(uuidStr));

                player.sendMessage(MiniMessage.miniMessage().deserialize("<#00FF00>You successfully added " + (amount / 2) + " hearts to " + targetName));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<#00FF00>" + targetName + "'s hearts is now: " + (hearts / 2)));

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);

                if (Bukkit.getPlayer(targetName) != null) {
                    Bukkit.getPlayer(targetName).setMaxHealth(hearts);
                }

            } else if (args[0].equalsIgnoreCase("remove")) {

                hearts = hearts - amount;

                if (hearts <= 0) {
                    player.sendMessage(ChatColor.RED + "You cannot remove hearts from this guy if it ends up being negative or 0.");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return false;
                }

                config.set("hearts", (hearts / 2));
                PlayerData.savePlayerData(plugin, UUID.fromString(uuidStr));

                player.sendMessage(MiniMessage.miniMessage().deserialize("<#00FF00>You successfully removed " + (amount / 2) + " hearts from " + targetName));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<#00FF00>" + targetName + "'s hearts is now: " + (hearts / 2)));

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);

                if (Bukkit.getPlayer(targetName) != null) {
                    Bukkit.getPlayer(targetName).setMaxHealth(hearts);
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                hearts = amount;

                if (checkIfYouTryToKillThisGuy(hearts, player)) return false;
                config.set("hearts", (hearts / 2));
                PlayerData.savePlayerData(plugin, UUID.fromString(uuidStr));

                player.sendMessage(MiniMessage.miniMessage().deserialize("<#00FF00>You successfully set " + (amount / 2) + " hearts to " + targetName));

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);

                if (Bukkit.getPlayer(targetName) != null) {
                    Bukkit.getPlayer(targetName).setMaxHealth(hearts);
                }
            } else {
                player.sendMessage(ChatColor.YELLOW + "/lifestealadmin <add, remove, set> <player> <amount>");
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "/lifestealadmin <add, remove, set> <player> <amount>");
        }
        return false;
    }

    private static boolean checkIfYouTryToKillThisGuy(int hearts, Player player) {
        if (hearts <= 0) {
            player.sendMessage(ChatColor.RED + "You cannot remove hearts from this guy if it ends up being negative or 0.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        Player player = (Player) sender;

        if (player.hasPermission("lifestealplus.command.lifestealadmin")) {
            if (args.length == 1) {
                completions.add("add");
                completions.add("remove");
                completions.add("set");
            }
            if (args.length == 2) {
                completions.addAll(PlayerData.getAllPlayerNames(plugin));
            }
            if (args.length == 3) {
                completions.add("<amount>");
            }
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return completions.stream().filter(s -> s.toLowerCase().startsWith(lastArg.toLowerCase())).collect(Collectors.toList());
    }
}
