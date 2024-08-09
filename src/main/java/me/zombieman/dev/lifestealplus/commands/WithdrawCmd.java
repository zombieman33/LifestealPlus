package me.zombieman.dev.lifestealplus.commands;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.crafting.HeartCrafting;
import me.zombieman.dev.lifestealplus.data.ItemData;
import me.zombieman.dev.lifestealplus.manager.HeartManager;
import me.zombieman.dev.lifestealplus.manager.PlayerManager;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WithdrawCmd implements CommandExecutor, TabCompleter {
    private final LifestealPlus plugin;
    public WithdrawCmd(LifestealPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("lifestealplus.command.withdraw")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to run this command!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1.0f, 1.0f);
            return false;
        }

        HeartManager heartManager = plugin.heartManager;

        if (args.length >= 1) {
            String amountStr = args[0];

            int amount = 1;

            try {
                amount = Integer.parseInt(amountStr);
            } catch (IllegalArgumentException e) {
                amount = 1;
            }

            if (withdrawHeart(heartManager, player, amount)) return false;
        } else {
            if (withdrawHeart(heartManager, player, 1)) return false;
        }
        return false;
    }

    private boolean withdrawHeart(HeartManager heartManager, Player player, int amount) {
        int currentHearts = heartManager.getHeart(player);
        int minimumHearts = plugin.getSettingsManager().getMinimumHearts();

        String sOrNotMin = (minimumHearts > 1) ? "s" : "";
        if (currentHearts - amount < minimumHearts) {
            player.sendMessage(ChatColor.RED + "You cannot withdraw that many hearts! You must have at least " + minimumHearts + " heart" + sOrNotMin + " remaining.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        String sOrNot = (amount > 1) ? "s" : "";
        ItemStack item = ItemData.loadItem(plugin, plugin.getConfig().getString("heart.item").replace("item-", ""));
        item.setAmount(amount);

        if (!PlayerManager.hasSpaceForItem(player, item)) {
            player.sendMessage(ChatColor.RED + "You don't have enough space for " + amount + " heart" + sOrNot + "!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        heartManager.removeHeart(player, amount);

        player.getInventory().addItem(item);

        player.sendMessage(ChatColor.GREEN + "Successfully withdrawn " + amount + " heart" + sOrNot + "!");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL,1.0f, 1.0f);
        return false;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        Player player = (Player) sender;

        if (player.hasPermission("lifestealplus.command.withdraw")) {
            if (args.length == 1) {
                completions.add("<amount>");
            }
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return completions.stream().filter(s -> s.toLowerCase().startsWith(lastArg.toLowerCase())).collect(Collectors.toList());
    }
}
