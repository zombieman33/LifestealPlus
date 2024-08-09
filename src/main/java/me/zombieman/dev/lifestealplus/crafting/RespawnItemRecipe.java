package me.zombieman.dev.lifestealplus.crafting;

import me.zombieman.dev.lifestealplus.LifestealPlus;
import me.zombieman.dev.lifestealplus.data.ItemData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

public class RespawnItemRecipe implements Listener {

    private final LifestealPlus plugin;
    private final NamespacedKey recipeKey;
    private Map<Character, ItemStack> ingredientMap;
    private String[] pattern;

    public RespawnItemRecipe(LifestealPlus plugin) {
        this.plugin = plugin;
        this.recipeKey = new NamespacedKey(plugin, "respawn_item_recipe");
        this.ingredientMap = new HashMap<>();
        createRespawnRecipe();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void createRespawnRecipe() {
        // Remove existing recipe if it exists
        if (Bukkit.getRecipe(recipeKey) != null) {
            Bukkit.removeRecipe(recipeKey);
            plugin.getLogger().info("Old respawn recipe unregistered successfully");
        }

        // Check if crafting is enabled in settings
        if (!plugin.getSettingsManager().isRespawnItemCrafting()) {
            plugin.getLogger().info("Respawn recipe was not updated since setting: 'Respawn Item Crafting' = false");
            return; // Exit if crafting is disabled
        }

        FileConfiguration config = plugin.getConfig();
        ConfigurationSection respawnSection = config.getConfigurationSection("respawnItem");
        if (respawnSection == null) {
            plugin.getLogger().warning("No respawn section found in config.yml");
            return;
        }

        String resultItemId = respawnSection.getString("item");
        if (resultItemId == null || resultItemId.isEmpty()) {
            plugin.getLogger().warning("No result item defined for respawn recipe");
            return;
        }

        ItemStack resultItem = ItemData.loadItem(plugin, resultItemId.replace("item-", ""));
        if (resultItem == null) {
            plugin.getLogger().warning("Result item " + resultItemId + " could not be found in item data");
            return;
        }

        // Get the crafting pattern
        ConfigurationSection craftingSection = respawnSection.getConfigurationSection("crafting");
        if (craftingSection == null) {
            plugin.getLogger().warning("No crafting section found for respawn recipe");
            return;
        }

        // Correctly assign to the class-level pattern field
        this.pattern = craftingSection.getStringList("pattern").toArray(new String[0]);

        // Get the ingredients
        ConfigurationSection ingredientsSection = craftingSection.getConfigurationSection("ingredients");
        if (ingredientsSection == null) {
            plugin.getLogger().warning("No ingredients section found for respawn recipe");
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
        plugin.getLogger().info("Respawn recipe ingredients:");
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

        Bukkit.addRecipe(recipe);
        plugin.getLogger().info("Respawn recipe registered successfully");
    }

    public void reloadRespawnRecipe() {
        if (Bukkit.getRecipe(recipeKey) != null) {
            Bukkit.removeRecipe(recipeKey);
            plugin.getLogger().info("Respawn recipe unregistered successfully");
        }

        createRespawnRecipe();
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe)) return;
        ShapedRecipe recipe = (ShapedRecipe) event.getRecipe();
        if (!recipe.getKey().equals(recipeKey)) return;

        CraftingInventory inventory = event.getInventory();
        ItemStack[] matrix = inventory.getMatrix();

        for (int row = 0; row < pattern.length; row++) {
            String rowPattern = pattern[row];
            for (int col = 0; col < rowPattern.length(); col++) {
                char symbol = rowPattern.charAt(col);
                ItemStack expectedItem = ingredientMap.get(symbol);

                int index = row * 3 + col;

                ItemStack actualItem = matrix[index];

                if (expectedItem == null) {
                    if (actualItem != null) {
                        continue;
                    }
                } else {
                    if (actualItem == null || actualItem.getType() != expectedItem.getType()) {
                        inventory.setResult(null);
                        return;
                    }
                }
            }
        }

        ItemStack resultItem = recipe.getResult();
        inventory.setResult(resultItem);
    }
}
