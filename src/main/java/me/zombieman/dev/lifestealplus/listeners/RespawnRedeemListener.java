package me.zombieman.dev.lifestealplus.listeners;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.data.ItemData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class RespawnRedeemListener implements Listener {

    private final LifestealPlus plugin;

    public RespawnRedeemListener(LifestealPlus plugin) {
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

            String itemInConfig = plugin.getConfig().getString("respawnItem.item").replace("item-", "");
            ItemStack heartItem = ItemData.loadItem(plugin, itemInConfig);

            if (heldItem.isSimilar(heartItem)) {
                event.setCancelled(true);
                plugin.guiManager.openDeathArenaGui(player, 1);
            }
        }
    }
}
