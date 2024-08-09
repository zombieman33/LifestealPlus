package me.zombieman.dev.lifestealplus.crafting;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.data.ItemData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

public class HeartCrafting implements Listener {

    private final LifestealPlus plugin;
    private final NamespacedKey recipeKey;
    private Map<Character, ItemStack> ingredientMap;
    private String[] pattern;

    public HeartCrafting(LifestealPlus plugin) {
        this.plugin = plugin;
        this.recipeKey = new NamespacedKey(plugin, "heart_recipe");
        this.ingredientMap = new HashMap<>();
        createHeartRecipe();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void createHeartRecipe() {
        // Remove existing recipe if it exists
        if (Bukkit.getRecipe(recipeKey) != null) {
            Bukkit.removeRecipe(recipeKey);
            plugin.getLogger().info("Old heart recipe unregistered successfully");
        }

        // Check if crafting is enabled in settings
        if (!plugin.getSettingsManager().isHeartItemCrafting()) {
            plugin.getLogger().info("Heart recipe was not updated since setting: 'Heart Item Crafting' = false");
            return; // Exit if crafting is disabled
        }

        FileConfiguration config = plugin.getConfig();
        ConfigurationSection heartSection = config.getConfigurationSection("heart");
        if (heartSection == null) {
            plugin.getLogger().warning("No heart section found in config.yml");
            return;
        }

        String resultItemId = heartSection.getString("item");
        if (resultItemId == null || resultItemId.isEmpty()) {
            plugin.getLogger().warning("No result item defined for heart recipe");
            return;
        }

        ItemStack resultItem = ItemData.loadItem(plugin, resultItemId.replace("item-", ""));
        if (resultItem == null) {
            plugin.getLogger().warning("Result item " + resultItemId + " could not be found in item data");
            return;
        }

        // Get the crafting pattern
        ConfigurationSection craftingSection = heartSection.getConfigurationSection("crafting");
        if (craftingSection == null) {
            plugin.getLogger().warning("No crafting section found for heart recipe");
            return;
        }

        // Correctly assign to the class-level pattern field
        this.pattern = craftingSection.getStringList("pattern").toArray(new String[0]);

        // Get the ingredients
        ConfigurationSection ingredientsSection = craftingSection.getConfigurationSection("ingredients");
        if (ingredientsSection == null) {
            plugin.getLogger().warning("No ingredients section found for heart recipe");
            return;
        }

        ingredientMap.clear();
        for (String key : ingredientsSection.getKeys(false)) {
            char symbol = key.charAt(0);
            String itemId = ingredientsSection.getString(key);
            ItemStack itemStack = null;

            if (itemId != null && !itemId.equalsIgnoreCase("null")) {
                itemStack = ItemData.loadItem(plugin, itemId.replace("item-", ""));
                if (itemStack == null) {
                    plugin.getLogger().warning("Ingredient item " + itemId + " could not be found in item data");
                }
            }

            // Add to ingredient map
            ingredientMap.put(symbol, itemStack);
        }

        // Log the ingredients
        plugin.getLogger().info("Heart recipe ingredients:");
        for (Map.Entry<Character, ItemStack> entry : ingredientMap.entrySet()) {
            ItemStack item = entry.getValue();
            if (item != null) {
                plugin.getLogger().info("Symbol: " + entry.getKey() + " - Item: " + item.getType());
            }
        }

        // Create the shaped recipe using NamespacedKey
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, resultItem);
        recipe.shape(this.pattern);

        // Set ingredients, treating nulls as air
        for (Map.Entry<Character, ItemStack> entry : ingredientMap.entrySet()) {
            if (entry.getValue() != null) {
                recipe.setIngredient(entry.getKey(), entry.getValue());
            } else {
                recipe.setIngredient(entry.getKey(), Material.AIR);
            }
        }

        // Register the new recipe
        Bukkit.addRecipe(recipe);
        plugin.getLogger().info("Heart recipe registered successfully");
    }

    public void reloadHeartRecipe() {
        // Unregister the existing recipe
        if (Bukkit.getRecipe(recipeKey) != null) {
            Bukkit.removeRecipe(recipeKey);
            plugin.getLogger().info("Heart recipe unregistered successfully");
        }

        // Register the new recipe
        createHeartRecipe();
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe)) return;
        ShapedRecipe recipe = (ShapedRecipe) event.getRecipe();
        if (!recipe.getKey().equals(recipeKey)) return;

        CraftingInventory inventory = event.getInventory();
        ItemStack[] matrix = inventory.getMatrix();

        // Check if the crafting grid has the correct items in the correct positions
        for (int row = 0; row < pattern.length; row++) {
            String rowPattern = pattern[row];
            for (int col = 0; col < rowPattern.length(); col++) {
                char symbol = rowPattern.charAt(col);
                ItemStack expectedItem = ingredientMap.get(symbol);

                // Calculate the index in the matrix
                int index = row * 3 + col; // Crafting grid is 3x3

                ItemStack actualItem = matrix[index];

                // If expected item is null, treat as air (no requirement for that slot)
                if (expectedItem == null) {
                    if (actualItem != null) {
                        // If there's an item present, we shouldn't require it
                        continue;
                    }
                } else {
                    // If the expected item is not null, check if the item in the grid matches
                    if (actualItem == null || actualItem.getType() != expectedItem.getType()) {
                        // If the actual item does not match the expected item
                        inventory.setResult(null);
                        return;
                    }
                }
            }
        }

        // Set the result item if the crafting is valid
        ItemStack resultItem = recipe.getResult();
        inventory.setResult(resultItem);
    }
}
