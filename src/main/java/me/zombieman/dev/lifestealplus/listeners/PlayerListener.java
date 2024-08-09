package me.zombieman.dev.lifestealplus.listeners;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerListener implements Listener {

    private LifestealPlus plugin;
    public PlayerListener(LifestealPlus plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.deathArenaManager.isPlayerInDeathArena(player)) {
            if (!plugin.getConfig().getBoolean("Death Arena.Move")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (plugin.deathArenaManager.isPlayerInDeathArena(player)) {
            if (!plugin.getConfig().getBoolean("Death Arena.Commands")) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (plugin.deathArenaManager.isPlayerInDeathArena(player)) {
            if (!plugin.getConfig().getBoolean("Death Arena.Break")) event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (plugin.deathArenaManager.isPlayerInDeathArena(player)) {
            if (!plugin.getConfig().getBoolean("Death Arena.Place")) event.setCancelled(true);
        }
    }
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Player player = (Player) event.getEntity();
        if (plugin.deathArenaManager.isPlayerInDeathArena(player)) {
            if (!plugin.getConfig().getBoolean("Death Arena.Damage")) event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.deathArenaManager.isPlayerInDeathArena(player)) {
            if (!plugin.getConfig().getBoolean("Death Arena.Item-Pickup")) event.setCancelled(true);
        }
    }
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.deathArenaManager.isPlayerInDeathArena(player)) {
            if (!plugin.getConfig().getBoolean("Death Arena.Item-Drop")) event.setCancelled(true);
        }
    }
}
