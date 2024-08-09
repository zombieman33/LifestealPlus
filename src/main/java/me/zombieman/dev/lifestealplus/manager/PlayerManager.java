package me.zombieman.dev.lifestealplus.manager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerManager {

    public static boolean hasSpaceForItem(Player player, ItemStack itemStack) {
        int amountToAdd = itemStack.getAmount();

        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);

            if (item == null) {
                amountToAdd -= itemStack.getMaxStackSize();
            } else if (item.isSimilar(itemStack)) {
                int spaceInSlot = item.getMaxStackSize() - item.getAmount();
                amountToAdd -= spaceInSlot;
            }

            if (amountToAdd <= 0) {
                return true;
            }
        }

        return amountToAdd <= 0;
    }
}
