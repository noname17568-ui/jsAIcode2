package com.atomichorizons2026.crafting;

import com.atomichorizons2026.handlers.RegistryHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

/**
 * Smelting recipes for Atomic Horizons: 2026.
 * Tier 0: Primitive processing (1 ore = 1 ingot)
 * Per TZ Section 2.1
 */
public class SmeltingRecipes {

    private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("atomichorizons2026");

    /**
     * Register all smelting recipes.
     * Called during init phase.
     */
    public static void register() {
        try {
            LOGGER.info("Registering smelting recipes...");

            // === RAW MATERIAL -> INGOT (Tier 0: 1:1 ratio) ===
            addSmelting(RegistryHandler.RAW_URANIUM, RegistryHandler.INGOT_URANIUM, 1.0f);
            addSmelting(RegistryHandler.RAW_THORIUM, RegistryHandler.INGOT_THORIUM, 0.8f);
            addSmelting(RegistryHandler.RAW_ZIRCONIUM, RegistryHandler.INGOT_ZIRCONIUM, 0.7f);
            addSmelting(RegistryHandler.RAW_LEAD, RegistryHandler.INGOT_LEAD, 0.5f);
            addSmelting(RegistryHandler.RAW_BERYLLIUM, RegistryHandler.INGOT_BERYLLIUM, 0.9f);
            addSmelting(RegistryHandler.RAW_BORON, RegistryHandler.INGOT_BORON, 0.6f);
            addSmelting(RegistryHandler.RAW_GRAPHITE, RegistryHandler.INGOT_GRAPHITE, 0.3f);
            addSmelting(RegistryHandler.RAW_RADIUM, RegistryHandler.INGOT_RADIUM, 1.5f);

            // === ORE BLOCK -> INGOT (direct smelting, Tier 0: 1 ore = 1 ingot) ===
            addSmeltingBlock(RegistryHandler.ORE_URANIUM, RegistryHandler.INGOT_URANIUM, 1.0f);
            addSmeltingBlock(RegistryHandler.ORE_THORIUM, RegistryHandler.INGOT_THORIUM, 0.8f);
            addSmeltingBlock(RegistryHandler.ORE_ZIRCONIUM, RegistryHandler.INGOT_ZIRCONIUM, 0.7f);
            addSmeltingBlock(RegistryHandler.ORE_LEAD, RegistryHandler.INGOT_LEAD, 0.5f);
            addSmeltingBlock(RegistryHandler.ORE_BERYLLIUM, RegistryHandler.INGOT_BERYLLIUM, 0.9f);
            addSmeltingBlock(RegistryHandler.ORE_BORON, RegistryHandler.INGOT_BORON, 0.6f);
            addSmeltingBlock(RegistryHandler.ORE_GRAPHITE, RegistryHandler.INGOT_GRAPHITE, 0.3f);
            addSmeltingBlock(RegistryHandler.ORE_RADIUM, RegistryHandler.INGOT_RADIUM, 1.5f);

            // NOTE: Yellowcake is produced via Chemical Oxidizer (Tier 3), not furnace.
            // RAW_URANIUM -> INGOT_URANIUM is the only furnace path.

            LOGGER.info("Smelting recipes registered successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to register smelting recipes", e);
        }
    }

    /**
     * Helper: register item -> item smelting.
     */
    private static void addSmelting(Item input, Item output, float xp) {
        if (input != null && output != null) {
            GameRegistry.addSmelting(input, new ItemStack(output), xp);
        }
    }

    /**
     * Helper: register block -> item smelting.
     */
    private static void addSmeltingBlock(net.minecraft.block.Block input, Item output, float xp) {
        if (input != null && output != null) {
            GameRegistry.addSmelting(input, new ItemStack(output), xp);
        }
    }
}
