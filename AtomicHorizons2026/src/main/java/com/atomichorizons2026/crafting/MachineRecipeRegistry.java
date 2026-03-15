package com.atomichorizons2026.crafting;

import com.atomichorizons2026.handlers.RegistryHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for machine recipes (Crusher, Enrichment Chamber, Smelter).
 * Per TZ Section 2.2: Tier 1 chemical processing
 */
public class MachineRecipeRegistry {

    // Crusher recipes: Ore -> 2x Crushed Ore
    private static final Map<Item, ItemStack> CRUSHER_RECIPES = new HashMap<>();
    
    // Enrichment recipes: Crushed -> Enriched
    private static final Map<Item, ItemStack> ENRICHMENT_RECIPES = new HashMap<>();
    
    // Smelter recipes: Enriched/Raw -> Ingot
    private static final Map<Item, ItemStack> SMELTER_RECIPES = new HashMap<>();

    public static void init() {
        // === Crusher: Ore Block -> 2x Raw (Tier 1: doubles output) ===
        addCrusherRecipe(RegistryHandler.ORE_URANIUM, new ItemStack(RegistryHandler.RAW_URANIUM, 2));
        addCrusherRecipe(RegistryHandler.ORE_THORIUM, new ItemStack(RegistryHandler.RAW_THORIUM, 2));
        addCrusherRecipe(RegistryHandler.ORE_ZIRCONIUM, new ItemStack(RegistryHandler.RAW_ZIRCONIUM, 2));
        addCrusherRecipe(RegistryHandler.ORE_FLUORITE, new ItemStack(RegistryHandler.RAW_FLUORITE, 2));
        addCrusherRecipe(RegistryHandler.ORE_SULFUR, new ItemStack(RegistryHandler.RAW_SULFUR, 2));
        addCrusherRecipe(RegistryHandler.ORE_LEAD, new ItemStack(RegistryHandler.RAW_LEAD, 2));
        addCrusherRecipe(RegistryHandler.ORE_BERYLLIUM, new ItemStack(RegistryHandler.RAW_BERYLLIUM, 2));
        addCrusherRecipe(RegistryHandler.ORE_BORON, new ItemStack(RegistryHandler.RAW_BORON, 2));
        addCrusherRecipe(RegistryHandler.ORE_GRAPHITE, new ItemStack(RegistryHandler.RAW_GRAPHITE, 2));
        
        // === Enrichment: Raw -> Enriched (prepares for smelter) ===
        addEnrichmentRecipe(RegistryHandler.RAW_URANIUM, new ItemStack(RegistryHandler.ENRICHED_URANIUM));
        addEnrichmentRecipe(RegistryHandler.RAW_THORIUM, new ItemStack(RegistryHandler.ENRICHED_THORIUM));
        addEnrichmentRecipe(RegistryHandler.RAW_ZIRCONIUM, new ItemStack(RegistryHandler.ENRICHED_ZIRCONIUM));
        addEnrichmentRecipe(RegistryHandler.RAW_LEAD, new ItemStack(RegistryHandler.ENRICHED_LEAD));
        addEnrichmentRecipe(RegistryHandler.RAW_BERYLLIUM, new ItemStack(RegistryHandler.ENRICHED_BERYLLIUM));
        addEnrichmentRecipe(RegistryHandler.RAW_BORON, new ItemStack(RegistryHandler.ENRICHED_BORON));
        
        // === Smelter: Raw/Enriched -> Ingot ===
        // Raw -> Ingot (basic path)
        addSmelterRecipe(RegistryHandler.RAW_URANIUM, new ItemStack(RegistryHandler.INGOT_URANIUM));
        addSmelterRecipe(RegistryHandler.RAW_THORIUM, new ItemStack(RegistryHandler.INGOT_THORIUM));
        addSmelterRecipe(RegistryHandler.RAW_ZIRCONIUM, new ItemStack(RegistryHandler.INGOT_ZIRCONIUM));
        addSmelterRecipe(RegistryHandler.RAW_LEAD, new ItemStack(RegistryHandler.INGOT_LEAD));
        addSmelterRecipe(RegistryHandler.RAW_BERYLLIUM, new ItemStack(RegistryHandler.INGOT_BERYLLIUM));
        addSmelterRecipe(RegistryHandler.RAW_BORON, new ItemStack(RegistryHandler.INGOT_BORON));
        addSmelterRecipe(RegistryHandler.RAW_GRAPHITE, new ItemStack(RegistryHandler.INGOT_GRAPHITE));
        addSmelterRecipe(RegistryHandler.RAW_RADIUM, new ItemStack(RegistryHandler.INGOT_RADIUM));
        // Enriched -> Ingot (better path, same output but enriched is from 2x raw)
        addSmelterRecipe(RegistryHandler.ENRICHED_URANIUM, new ItemStack(RegistryHandler.INGOT_URANIUM));
        addSmelterRecipe(RegistryHandler.ENRICHED_THORIUM, new ItemStack(RegistryHandler.INGOT_THORIUM));
        addSmelterRecipe(RegistryHandler.ENRICHED_ZIRCONIUM, new ItemStack(RegistryHandler.INGOT_ZIRCONIUM));
        addSmelterRecipe(RegistryHandler.ENRICHED_LEAD, new ItemStack(RegistryHandler.INGOT_LEAD));
        addSmelterRecipe(RegistryHandler.ENRICHED_BERYLLIUM, new ItemStack(RegistryHandler.INGOT_BERYLLIUM));
        addSmelterRecipe(RegistryHandler.ENRICHED_BORON, new ItemStack(RegistryHandler.INGOT_BORON));
    }

    private static void addCrusherRecipe(net.minecraft.block.Block input, ItemStack output) {
        CRUSHER_RECIPES.put(Item.getItemFromBlock(input), output);
    }

    private static void addEnrichmentRecipe(Item input, ItemStack output) {
        ENRICHMENT_RECIPES.put(input, output);
    }

    private static void addSmelterRecipe(Item input, ItemStack output) {
        SMELTER_RECIPES.put(input, output);
    }

    @Nullable
    public static ItemStack getCrusherOutput(ItemStack input) {
        if (input.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = CRUSHER_RECIPES.get(input.getItem());
        return result != null ? result.copy() : ItemStack.EMPTY;
    }

    @Nullable
    public static ItemStack getEnrichmentOutput(ItemStack input) {
        if (input.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = ENRICHMENT_RECIPES.get(input.getItem());
        return result != null ? result.copy() : ItemStack.EMPTY;
    }

    @Nullable
    public static ItemStack getSmelterOutput(ItemStack input) {
        if (input.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = SMELTER_RECIPES.get(input.getItem());
        return result != null ? result.copy() : ItemStack.EMPTY;
    }
}
