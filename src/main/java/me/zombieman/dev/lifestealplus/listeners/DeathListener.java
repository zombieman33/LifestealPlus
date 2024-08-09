package me.zombieman.dev.lifestealplus.listeners;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.data.ItemData;
import me.zombieman.dev.lifestealplus.data.PlayerData;
import me.zombieman.dev.lifestealplus.manager.HeartManager;
import me.zombieman.dev.lifestealplus.manager.SettingsManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class DeathListener implements Listener {

    private final LifestealPlus plugin;

    public DeathListener(LifestealPlus plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        Player killer = victim.getKiller();

        SettingsManager settingsManager = plugin.getSettingsManager();
        HeartManager heartManager = plugin.heartManager;
        ItemStack heart = ItemData.loadItem(plugin, plugin.getConfig().getString("heart.item").replace("item-", ""));
        heart.setAmount(1);

        if (plugin.settingsManager.getTeleportToDeathArenaOnMinHeartDeath() && settingsManager.getMinimumHearts() >= PlayerData.getPlayerDataConfig(plugin, victim.getUniqueId()).getInt("hearts")) {
            plugin.deathArenaManager.teleportPlayerIn(victim);
            return;
        }

        if (killer != null) {

            if (settingsManager.isPlayerGainHeartOnKill() && PlayerData.getPlayerDataConfig(plugin, killer.getUniqueId()).getInt("hearts") < settingsManager.getHeartCap()) {
                if (settingsManager.getMinimumHearts() != PlayerData.getPlayerDataConfig(plugin, victim.getUniqueId()).getInt("hearts")) {
                    heartManager.addHeart(killer, 1);
                    killer.sendActionBar(MiniMessage.miniMessage().deserialize("<#00FF00> +1 Heart"));
                }
//                } else {
//                    String sOrNot = (PlayerData.getPlayerDataConfig(plugin, victim.getUniqueId()).getInt("hearts") > 1) ? "s" : "";
//                    killer.sendMessage(MiniMessage.miniMessage().deserialize("<#FF0000>You didn't receive a heart because " + victim.getName() + " only had " + PlayerData.getPlayerDataConfig(plugin, victim.getUniqueId()).getInt("hearts") + " heart" + sOrNot + "."));
//                }
            }

            if (settingsManager.isHeartItemDropOnDeath() && PlayerData.getPlayerDataConfig(plugin, victim.getUniqueId()).getInt("hearts") > settingsManager.getMinimumHearts()) {
                heartManager.removeHeart(victim, 1);
                victim.sendActionBar(MiniMessage.miniMessage().deserialize("<#FF0000> -1 Heart"));
                event.getDrops().add(heart);
                return;
            }

            if (settingsManager.isPlayerLoseHeartOnDeath() && PlayerData.getPlayerDataConfig(plugin, victim.getUniqueId()).getInt("hearts") > settingsManager.getMinimumHearts()) {
                heartManager.removeHeart(victim, 1);
                victim.sendActionBar(MiniMessage.miniMessage().deserialize("<#FF0000> -1 Heart"));
            }
        } else {
            if (settingsManager.isPlayerLoseHeartOnDeathWhenKillerIsNotAPlayer() && PlayerData.getPlayerDataConfig(plugin, victim.getUniqueId()).getInt("hearts") > settingsManager.getMinimumHearts()) {
                heartManager.removeHeart(victim, 1);
                victim.sendActionBar(MiniMessage.miniMessage().deserialize("<#FF0000> -1 Heart"));
                if (settingsManager.isHeartItemDropOnDeath()) event.getDrops().add(heart);
            }

        }
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (plugin.deathArenaManager.isPlayerInDeathArena(event.getPlayer())) {
            event.setRespawnLocation(plugin.deathArenaManager.getTeleportInLocation());
        }
    }

}
