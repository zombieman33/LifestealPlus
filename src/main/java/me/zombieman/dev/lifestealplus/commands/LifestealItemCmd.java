package me.zombieman.dev.lifestealplus.commands;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.crafting.HeartCrafting;
import me.zombieman.dev.lifestealplus.crafting.RespawnItemRecipe;
import me.zombieman.dev.lifestealplus.data.ItemData;
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

public class LifestealItemCmd implements CommandExecutor, TabCompleter {
    private final LifestealPlus plugin;
    private final HeartCrafting heartCrafting;
    private final RespawnItemRecipe respawnItemRecipe;
    public LifestealItemCmd(LifestealPlus plugin) {
        this.plugin = plugin;
        this.heartCrafting = plugin.heartCrafting;
        this.respawnItemRecipe = plugin.respawnItemRecipe;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("lifestealplus.command.lifestealitem")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to run this command!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1.0f, 1.0f);
            return false;
        }

//        if (args.length >= 1) {
//            String action = args[0];
//
//            if (action.equalsIgnoreCase("reload")) {
//
//                long startTime = System.currentTimeMillis();
//
//                ItemData.saveAllItems(plugin);
//                plugin.reloadConfig();
//                plugin.heartManager.reload();
//
//                heartCrafting.reloadHeartRecipe();
//                long endTime = System.currentTimeMillis();
//                long duration = endTime - startTime;
//                player.sendMessage(MiniMessage.miniMessage().deserialize("<green><strikethrough>                                                            "));
//                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Successfully reloaded all files!"));
//                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Time: <underlined>" + duration + " (ms)</underlined>"));
//                player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Info:"));
//                player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow><bold> |</bold> Updating the heart recipe requires the server"));
//                player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow><bold> |</bold> to restart (to fully remove the old recipe)"));
//                player.sendMessage(MiniMessage.miniMessage().deserialize("<green><strikethrough>                                                            "));
//
//                return false;
//            }
//        }
        if (args.length >= 2) {
            String action = args[0];

            String itemID = args[1];

            ItemStack heldItem = player.getInventory().getItemInMainHand();

            String replacedId = itemID.replace("item-", "");

            List<String> idList = new ArrayList<>();

            idList.addAll(ItemData.getAllItemIds(plugin));
            if (action.equalsIgnoreCase("add")) {

                if (heldItem.getType().isAir()) {
                    player.sendMessage(ChatColor.RED + "You need to hold a item to run this command!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1.0f, 1.0f);
                    return false;
                }

                if (idList.contains(replacedId)) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>This ID is already used. /item replace <ID>")
                            .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize("<red>/item replace " + replacedId)))
                            .clickEvent(ClickEvent.runCommand("/item replace " + replacedId)));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1.0f, 1.0f);
                    return false;
                }

                ItemData.saveItem(plugin, replacedId, heldItem);

                player.sendMessage(MiniMessage.miniMessage().deserialize("<green><strikethrough>                                                            "));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Successfully Added: <underlined>" + heldItem.getType() + "</underlined>"));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Item Info:"));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>ID: <underlined>" + replacedId + "</underlined>"));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Item: <underlined>" + heldItem.getType() + "</underlined>"));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Amount: <underlined>" + heldItem.getAmount() + "</underlined>"));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Enchantments: <underlined>" + heldItem.getEnchantments() + "</underlined>"));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green><strikethrough>                                                            "));

                heartCrafting.reloadHeartRecipe();
                respawnItemRecipe.reloadRespawnRecipe();
            } else if (action.equalsIgnoreCase("remove")) {

                if (checkID(idList, replacedId, player)) return false;

                player.sendMessage(MiniMessage.miniMessage().deserialize("<green><strikethrough>                                                            "));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Successfully Removed: <underlined>" + replacedId + "</underlined>."));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green><strikethrough>                                                            "));

                ItemData.removeItem(plugin, replacedId);

                heartCrafting.reloadHeartRecipe();
                respawnItemRecipe.reloadRespawnRecipe();
            } else if (action.equalsIgnoreCase("replace")) {

                if (heldItem.getType().isAir()) {
                    player.sendMessage(ChatColor.RED + "You need to hold a item to run this command!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return false;
                }

                if (checkID(idList, replacedId, player)) return false;

                ItemData.replaceItem(plugin, replacedId, heldItem);

                ItemStack itemStack = ItemData.loadItem(plugin, replacedId);

                player.sendMessage(MiniMessage.miniMessage().deserialize("<green><strikethrough>                                                            "));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Successfully Replaced: <underlined>" + replacedId + "</underlined>."));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>New Item Info:"));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>ID: <underlined>" + replacedId + "</underlined>"));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Item: <underlined>" + itemStack.getType() + "</underlined>"));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Amount: <underlined>" + itemStack.getAmount() + "</underlined>"));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Enchantments: <underlined>" + itemStack.getEnchantments() + "</underlined>"));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green><strikethrough>                                                            "));

                heartCrafting.reloadHeartRecipe();
                respawnItemRecipe.reloadRespawnRecipe();
            } else {
                player.sendMessage(ChatColor.YELLOW + "/lifestealitem <add, remove, replace> <ID>");
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "/lifestealitem <add, remove, replace> <ID>");
        }
        return false;
    }

    private static boolean checkID(List<String> idList, String replacedId, Player player) {
        if (!idList.contains(replacedId)) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>This ID is not used. /item add <ID>")
                    .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize("<red>/item add " + replacedId)))
                    .clickEvent(ClickEvent.runCommand("/item add " + replacedId)));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1.0f, 1.0f);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        Player player = (Player) sender;

        if (player.hasPermission("lifestealplus.command.lifestealitem")) {
            if (args.length == 1) {
                completions.add("add");
                completions.add("remove");
                completions.add("replace");
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("replace")) {
                    completions.addAll(ItemData.getAllItemIds(plugin));
                }
                if (args[0].equalsIgnoreCase("add")) {
                    completions.add("ID");
                }
            }
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return completions.stream().filter(s -> s.toLowerCase().startsWith(lastArg.toLowerCase())).collect(Collectors.toList());
    }
}
