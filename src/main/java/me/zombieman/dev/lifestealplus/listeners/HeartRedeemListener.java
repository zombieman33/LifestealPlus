package me.zombieman.dev.lifestealplus.listeners;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.data.ItemData;
import me.zombieman.dev.lifestealplus.enums.HeartCap;
import me.zombieman.dev.lifestealplus.manager.HeartManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class HeartRedeemListener implements Listener {

    private final LifestealPlus plugin;

    public HeartRedeemListener(LifestealPlus plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {

            ItemStack heldItem = player.getInventory().getItemInMainHand();

            String itemInConfig = plugin.getConfig().getString("heart.item").replace("item-", "");
            ItemStack heartItem = ItemData.loadItem(plugin, itemInConfig);

            if (heldItem.isSimilar(heartItem)) {
                event.setCancelled(true);

                HeartManager heartManager = plugin.heartManager;
                int heartsToRedeem = 1;

                if (player.isSneaking()) {
                    heartsToRedeem = heldItem.getAmount();
                }

                redeemHeart(player, heartManager, heartsToRedeem, heldItem);
            }
        }
    }

    private boolean redeemHeart(Player player, HeartManager heartManager, int amount, ItemStack heldItem) {
        int currentHearts = heartManager.getHeart(player);
        int heartCapAmount = plugin.settingsManager.getHeartCap();

        if (player.isSneaking()) {
            int maxRedeemable = heartCapAmount - currentHearts;
            amount = Math.min(amount, maxRedeemable);
        }

        String sOrNot = (amount > 1) ? "s" : "";

        String plus = ChatColor.GREEN + "+" + amount + " Heart" + sOrNot + "!";
        if (heartManager.heartCap == HeartCap.HEART_CAP) {
            if (currentHearts < heartCapAmount) {
                heartManager.addHeart(player, amount);
                player.sendMessage(plus);
                player.sendActionBar(plus);
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
                removeHeartsFromPlayer(player, heldItem, amount);
                return false;
            } else {
                String cap = ChatColor.RED + "You've reached the heart cap! (" + heartCapAmount + ")";
                player.sendMessage(cap);
                player.sendActionBar(cap);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);

                if (currentHearts > heartCapAmount) {
                    heartManager.setHeart(player, heartCapAmount);
                }

                return true;
            }
        } else {
            heartManager.addHeart(player, amount);
            player.sendMessage(plus);
            player.sendActionBar(plus);
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
            removeHeartsFromPlayer(player, heldItem, amount);
            return false;
        }
    }

    private void removeHeartsFromPlayer(Player player, ItemStack heartItem, int amountToRemove) {
        ItemStack[] inventory = player.getInventory().getContents();

        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            if (item != null && item.isSimilar(heartItem)) {
                int newAmount = item.getAmount() - amountToRemove;
                if (newAmount <= 0) {
                    player.getInventory().setItem(i, null);
                    amountToRemove = -newAmount;
                } else {
                    item.setAmount(newAmount);
                    break;
                }
            }
        }
    }
}
