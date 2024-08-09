package me.zombieman.dev.lifestealplus.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayerDataChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final FileConfiguration oldData;
    private final FileConfiguration newData;
    private boolean cancelled;

    public PlayerDataChangeEvent(Player player, FileConfiguration oldData, FileConfiguration newData) {
        this.player = player;
        this.oldData = oldData;
        this.newData = newData;
    }

    public Player getPlayer() {
        return player;
    }

    public FileConfiguration getOldData() {
        return oldData;
    }

    public FileConfiguration getNewData() {
        return newData;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}