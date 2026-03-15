package com.atomichorizons2026.config;

/**
 * Configuration for ore blocks.
 * Centralized constants for ore properties to avoid magic numbers.
 */
public class OreConfig {
    
    // Ore properties: {name, veinSize, hardness, harvestLevel, minY, maxY}
    public static class OreProperties {
        public final String name;
        public final int veinSize;
        public final float hardness;
        public final int harvestLevel;
        public final int minY;
        public final int maxY;
        
        public OreProperties(String name, int veinSize, float hardness, int harvestLevel, int minY, int maxY) {
            this.name = name;
            this.veinSize = veinSize;
            this.hardness = hardness;
            this.harvestLevel = harvestLevel;
            this.minY = minY;
            this.maxY = maxY;
        }
    }
    
    // Standard ore configurations
    public static final OreProperties URANIUM = new OreProperties("ore_uranium", 3, 15.0f, 5, 5, 64);
    public static final OreProperties THORIUM = new OreProperties("ore_thorium", 3, 12.0f, 3, 10, 50);
    public static final OreProperties ZIRCONIUM = new OreProperties("ore_zirconium", 2, 10.0f, 2, 20, 60);
    public static final OreProperties FLUORITE = new OreProperties("ore_fluorite", 2, 8.0f, 3, 30, 70);
    public static final OreProperties SULFUR = new OreProperties("ore_sulfur", 1, 6.0f, 2, 40, 80);
    public static final OreProperties LEAD = new OreProperties("ore_lead", 2, 8.0f, 2, 15, 55);
    public static final OreProperties BERYLLIUM = new OreProperties("ore_beryllium", 3, 12.0f, 4, 5, 40);
    public static final OreProperties BORON = new OreProperties("ore_boron", 2, 9.0f, 2, 25, 65);
    public static final OreProperties GRAPHITE = new OreProperties("ore_graphite", 1, 5.0f, 1, 50, 100);
    public static final OreProperties RADIUM = new OreProperties("ore_radium", 3, 20.0f, 10, 1, 30);
    public static final OreProperties MONAZITE = new OreProperties("ore_monazite", 2, 7.0f, 3, 35, 75);
}
