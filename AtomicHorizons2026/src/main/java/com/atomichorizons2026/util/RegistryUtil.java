package com.atomichorizons2026.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for registry operations.
 * Provides helper methods for batch registration and validation.
 */
public class RegistryUtil {
    
    private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("atomichorizons2026");
    
    /**
     * Validate that all items in array are non-null.
     * @throws IllegalArgumentException if any item is null
     */
    public static void validateItems(Item[] items, String category) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                throw new IllegalArgumentException("Null item at index " + i + " in category: " + category);
            }
        }
    }
    
    /**
     * Validate that all blocks in array are non-null.
     * @throws IllegalArgumentException if any block is null
     */
    public static void validateBlocks(Block[] blocks, String category) {
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i] == null) {
                throw new IllegalArgumentException("Null block at index " + i + " in category: " + category);
            }
        }
    }
    
    /**
     * Log registration summary.
     */
    public static void logRegistrationSummary(String category, int count) {
        LOGGER.info("Registered " + count + " items in category: " + category);
    }
    
    /**
     * Create ItemBlock array from Block array.
     */
    public static Item[] createItemBlocks(Block[] blocks) {
        List<Item> itemBlocks = new ArrayList<>();
        for (Block block : blocks) {
            if (block != null) {
                itemBlocks.add(new net.minecraft.item.ItemBlock(block).setRegistryName(block.getRegistryName()));
            }
        }
        return itemBlocks.toArray(new Item[0]);
    }
}
