package com.atomichorizons2026.config;

import net.minecraft.block.material.Material;

/**
 * Configuration for storage blocks.
 * Defines properties for metal and material storage blocks.
 */
public class BlockConfig {
    
    public static class BlockProperties {
        public final String name;
        public final Material material;
        public final float hardness;
        public final int harvestLevel;
        
        public BlockProperties(String name, Material material, float hardness, int harvestLevel) {
            this.name = name;
            this.material = material;
            this.hardness = hardness;
            this.harvestLevel = harvestLevel;
        }
    }
    
    // Storage block configurations
    public static final BlockProperties GRAPHITE = new BlockProperties("block_graphite", Material.IRON, 5.0f, 2);
    public static final BlockProperties LEAD = new BlockProperties("block_lead", Material.IRON, 8.0f, 2);
    public static final BlockProperties STEEL = new BlockProperties("block_steel", Material.IRON, 10.0f, 2);
    public static final BlockProperties CONCRETE = new BlockProperties("block_concrete", Material.ROCK, 50.0f, 2);
    public static final BlockProperties REINFORCED_CONCRETE = new BlockProperties("block_reinforced_concrete", Material.IRON, 100.0f, 3);
    public static final BlockProperties RADIUM = new BlockProperties("block_radium", Material.IRON, 20.0f, 3);
    
    // SMR (Small Modular Reactor) block configurations
    public static final BlockProperties SMR_CASING = new BlockProperties("smr_casing", Material.IRON, 15.0f, 2);
}
