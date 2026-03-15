package com.atomichorizons2026.radiation;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

/**
 * Calculates radiation shielding effectiveness for different materials.
 * Based on real-world physics (mass attenuation coefficients).
 */
public class RadiationShielding {
    
    /**
     * Calculates shielding effectiveness for a block against a radiation type.
     * Returns a value from 0.0 (no shielding) to 1.0 (complete blocking).
     * 
     * @param state Block state
     * @param radiationType Type of radiation
     * @return Shielding effectiveness (0.0 to 1.0)
     */
    public static double calculateShielding(IBlockState state, RadiationType radiationType) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        
        // Get base shielding from material
        double baseShielding = getBaseShielding(block, material, radiationType);
        
        // Apply radiation type modifiers
        return Math.min(1.0, baseShielding);
    }
    
    /**
     * Gets base shielding value for a block/material combination.
     */
    private static double getBaseShielding(Block block, Material material, RadiationType radiationType) {
        String blockName = block.getRegistryName() != null ? 
            block.getRegistryName().getPath() : "";
        
        switch (radiationType) {
            case ALPHA:
                // Alpha blocked by anything solid
                if (material.isSolid()) return 1.0;
                return 0.0;
                
            case BETA:
                // Beta blocked by metals, glass, plastic
                if (blockName.contains("glass")) return 0.9;
                if (blockName.contains("aluminum") || blockName.contains("steel")) return 0.95;
                if (material == Material.IRON) return 0.8;
                if (material == Material.ROCK) return 0.6;
                if (material.isSolid()) return 0.4;
                return 0.0;
                
            case GAMMA:
                // Gamma requires dense materials
                if (blockName.contains("lead")) return 0.95;
                if (blockName.contains("concrete")) return 0.7;
                if (blockName.contains("reinforced_concrete")) return 0.85;
                if (blockName.contains("steel")) return 0.6;
                if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) return 0.3;
                if (material == Material.IRON) return 0.5;
                if (material == Material.ROCK) return 0.4;
                if (material.isSolid()) return 0.2;
                return 0.0;
                
            case NEUTRON:
                // Neutron blocked by hydrogen-rich materials
                if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) return 0.8;
                if (blockName.contains("concrete")) return 0.7;
                if (blockName.contains("boron")) return 0.9;
                if (blockName.contains("graphite")) return 0.5;
                if (material == Material.ROCK) return 0.4;
                if (material.isSolid()) return 0.2;
                return 0.0;
                
            default:
                return 0.0;
        }
    }
    
    /**
     * Calculates total shielding through multiple blocks.
     * Uses exponential attenuation model.
     * 
     * @param blocks Array of block states
     * @param radiationType Type of radiation
     * @param initialIntensity Initial radiation intensity
     * @return Remaining intensity after shielding
     */
    public static double calculateMultiBlockShielding(IBlockState[] blocks, 
                                                      RadiationType radiationType, 
                                                      double initialIntensity) {
        double intensity = initialIntensity;
        
        for (IBlockState state : blocks) {
            if (state == null) continue;
            
            double shielding = calculateShielding(state, radiationType);
            // Each block reduces intensity by its shielding factor
            intensity *= (1.0 - shielding);
            
            // If intensity drops below 0.01, consider it fully blocked
            if (intensity < 0.01) return 0.0;
        }
        
        return intensity;
    }
    
    /**
     * Calculates the number of blocks needed to reduce radiation to safe levels.
     * 
     * @param radiationType Type of radiation
     * @param material Shielding material
     * @param reductionFactor Desired reduction (e.g., 0.01 for 99% reduction)
     * @return Number of blocks needed
     */
    public static int calculateRequiredThickness(RadiationType radiationType, 
                                                Material material, 
                                                double reductionFactor) {
        // Simplified calculation
        double shieldingPerBlock = getBaseShielding(Blocks.STONE, material, radiationType);
        
        if (shieldingPerBlock <= 0) return Integer.MAX_VALUE;
        
        // Calculate using exponential decay
        int blocks = (int) Math.ceil(Math.log(reductionFactor) / Math.log(1.0 - shieldingPerBlock));
        return Math.max(1, blocks);
    }
}
