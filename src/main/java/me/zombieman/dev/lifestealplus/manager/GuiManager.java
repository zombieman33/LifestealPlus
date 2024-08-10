package me.zombieman.dev.lifestealplus.manager;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.data.ItemData;
import me.zombieman.dev.lifestealplus.data.PlayerData;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GuiManager implements Listener {

    private final LifestealPlus plugin;

    private int prevPage = 45;
    private int closeButton = 49;
    private int nextPage = 53;

    public GuiManager(LifestealPlus plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    List<UUID> uuids = new ArrayList<>();
    HashMap<UUID, String> getTitle = new HashMap<>();
    HashMap<UUID, Integer> getCurrentPage = new HashMap<>();
    HashMap<UUID, Integer> getMaxPage = new HashMap<>();

    public void openDeathArenaGui(Player player, int page) {
        List<UUID> deathArenaPlayers = getDeathArenaPlayers();

        int itemsPerPage = 45;
        int maxPages = (int) Math.ceil((double) deathArenaPlayers.size() / itemsPerPage);

        if (page < 1) page = 1;
        if (page > maxPages) page = maxPages;

        String title = "Death Ban Players " + page + "/" + maxPages;
        Inventory gui = Bukkit.createInventory(null, 54, title);

        ItemStack frameItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta frameMeta = frameItem.getItemMeta();
        frameMeta.setDisplayName(" ");
        frameItem.setItemMeta(frameMeta);

        for (int i = 0; i < 9; i++) {
            gui.setItem(i, frameItem);
            gui.setItem(45 + i, frameItem);
        }
        for (int i = 0; i < 6; i++) {
            gui.setItem(9 * i, frameItem);
            gui.setItem(9 * i + 8, frameItem);
        }

        // Add player heads
        if (deathArenaPlayers.isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are no players to revive!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            player.closeInventory();
            return;
        }

        for (int i = 0; i < itemsPerPage; i++) {
            int index = (page - 1) * itemsPerPage + i;
            if (index >= deathArenaPlayers.size()) break;

            UUID playerUUID = deathArenaPlayers.get(index);
            OfflinePlayer arenaPlayer = Bukkit.getOfflinePlayer(playerUUID);

            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            skullMeta.setOwningPlayer(arenaPlayer);
            skullMeta.setDisplayName(ChatColor.GREEN + arenaPlayer.getName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.RED + "Death banned!");
            skullMeta.setLore(lore);
            PersistentDataContainer container = skullMeta.getPersistentDataContainer();
            container.set(new NamespacedKey(plugin, "playerUUID"), PersistentDataType.STRING, playerUUID.toString());
            playerHead.setItemMeta(skullMeta);

            gui.addItem(playerHead);
        }

        // Add navigation items
        ItemStack nextPage = new ItemStack(Material.ARROW);
        ItemMeta nextPageMeta = nextPage.getItemMeta();
        nextPageMeta.setDisplayName(ChatColor.GREEN + "Next Page");
        nextPage.setItemMeta(nextPageMeta);

        ItemStack prevPage = new ItemStack(Material.ARROW);
        ItemMeta prevPageMeta = prevPage.getItemMeta();
        prevPageMeta.setDisplayName(ChatColor.RED + "Previous Page");
        prevPage.setItemMeta(prevPageMeta);

        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        closeMeta.setDisplayName(ChatColor.DARK_RED + "Close");
        closeButton.setItemMeta(closeMeta);

        if (page != 1) gui.setItem(this.prevPage, prevPage);
        gui.setItem(this.closeButton, closeButton);
        if (maxPages != page) gui.setItem(this.nextPage, nextPage);

        player.openInventory(gui);

        getTitle.put(player.getUniqueId(), title);
        getCurrentPage.put(player.getUniqueId(), page);
        getMaxPage.put(player.getUniqueId(), maxPages);
        uuids.add(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!uuids.contains(player.getUniqueId())) return;

        event.setCancelled(true);
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;

        ItemMeta meta = event.getCurrentItem().getItemMeta();
        String title = getTitle.get(player.getUniqueId());
        int currentPage = Integer.parseInt(title.split("/")[0].split(" ")[3]);

        int slot = event.getSlot();

        if (event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            String uuidString = container.get(new NamespacedKey(plugin, "playerUUID"), PersistentDataType.STRING);
            if (uuidString != null) {
                UUID clickedPlayerUUID = UUID.fromString(uuidString);

                removeItemFromPlayer(player, ItemData.loadItem(plugin, plugin.getConfig().getString("respawnItem.item").replace("item-", "")), 1);

                removePlayerFromDeathArena(clickedPlayerUUID);

                player.sendMessage(ChatColor.GREEN + "You have revived " + PlayerData.getPlayerDataConfig(plugin, clickedPlayerUUID).getString("name") + "!");
                player.closeInventory();
                return;
            }
        }

        // Pagination logic
        if (slot == nextPage) {
            if (event.getInventory().getItem(slot).getType() != Material.ARROW) return;
            if (getMaxPage.get(player.getUniqueId()) == getCurrentPage.get(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You are on the last page!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                player.closeInventory();
                return;
            }
            getTitle.remove(player.getUniqueId());
            getMaxPage.remove(player.getUniqueId());
            getCurrentPage.remove(player.getUniqueId());
            uuids.remove(player.getUniqueId());
            openDeathArenaGui(player, currentPage + 1);
        } else if (slot == prevPage) {
            if (event.getInventory().getItem(slot).getType() != Material.ARROW) return;
            if (getCurrentPage.get(player.getUniqueId()) == 1) {
                player.sendMessage(ChatColor.RED + "You are on the first page!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                player.closeInventory();
                return;
            }
            getTitle.remove(player.getUniqueId());
            getMaxPage.remove(player.getUniqueId());
            getCurrentPage.remove(player.getUniqueId());
            uuids.remove(player.getUniqueId());
            openDeathArenaGui(player, currentPage - 1);
        } else if (slot == closeButton) {
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (!uuids.contains(player.getUniqueId())) return;
        getTitle.remove(player.getUniqueId());
        getMaxPage.remove(player.getUniqueId());
        getCurrentPage.remove(player.getUniqueId());
        uuids.remove(player.getUniqueId());
    }

    private List<UUID> getDeathArenaPlayers() {
        List<UUID> playersInDeathArena = new ArrayList<>();
        for (String playerDataFiles : PlayerData.getAllPlayerDataFiles(plugin)) {
            UUID uuid = UUID.fromString(playerDataFiles);
            if (plugin.deathArenaManager.isPlayerInDeathArenaUUID(uuid)) {
                playersInDeathArena.add(uuid);
            }
        }
        return playersInDeathArena;
    }

    private void removeItemFromPlayer(Player player, ItemStack heartItem, int amountToRemove) {
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

    private void removePlayerFromDeathArena(UUID playerUUID) {
        plugin.deathArenaManager.removePlayerFromDeathArena(playerUUID);
    }
}
