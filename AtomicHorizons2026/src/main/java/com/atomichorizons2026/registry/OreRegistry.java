package com.atomichorizons2026.registry;

import com.atomichorizons2026.blocks.BlockOre;
import com.atomichorizons2026.blocks.BlockRadioactiveOre;
import com.atomichorizons2026.config.OreConfig;
import com.atomichorizons2026.radiation.IRadiationSource;
import net.minecraft.block.Block;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for ore blocks.
 * Handles creation and registration of all ore types.
 */
public class OreRegistry {
    
    private static final Map<String, Block> ORES = new HashMap<>();
    private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("atomichorizons2026");
    
    // Public references for backward compatibility
    public static Block ORE_URANIUM;
    public static Block ORE_THORIUM;
    public static Block ORE_ZIRCONIUM;
    public static Block ORE_FLUORITE;
    public static Block ORE_SULFUR;
    public static Block ORE_LEAD;
    public static Block ORE_BERYLLIUM;
    public static Block ORE_BORON;
    public static Block ORE_GRAPHITE;
    public static Block ORE_RADIUM;
    public static Block ORE_MONAZITE;
    
    /**
     * Initialize all ore blocks.
     */
    public static void init() {
        try {
            // Radioactive ores
            ORE_URANIUM = createRadioactiveOre(OreConfig.URANIUM, IRadiationSource.RadiationType.GAMMA, 3.0);
            ORE_THORIUM = createRadioactiveOre(OreConfig.THORIUM, IRadiationSource.RadiationType.ALPHA, 2.0);
            ORE_RADIUM = createRadioactiveOre(OreConfig.RADIUM, IRadiationSource.RadiationType.GAMMA, 5.0, 15);
            
            // Standard ores
            ORE_ZIRCONIUM = createOre(OreConfig.ZIRCONIUM);
            ORE_FLUORITE = createOre(OreConfig.FLUORITE, 5); // Light value
            ORE_SULFUR = createOre(OreConfig.SULFUR);
            ORE_LEAD = createOre(OreConfig.LEAD);
            ORE_BERYLLIUM = createOre(OreConfig.BERYLLIUM);
            ORE_BORON = createOre(OreConfig.BORON);
            ORE_GRAPHITE = createOre(OreConfig.GRAPHITE);
            ORE_MONAZITE = createOre(OreConfig.MONAZITE);
            
            LOGGER.info("Ore registry initialized successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize ore registry", e);
            throw new RuntimeException("Ore registry initialization failed", e);
        }
    }
    
    /**
     * Create a standard ore block.
     */
    private static Block createOre(OreConfig.OreProperties props) {
        return createOre(props, 0);
    }
    
    /**
     * Create a standard ore block with light value.
     */
    private static Block createOre(OreConfig.OreProperties props, int lightValue) {
        BlockOre ore = new BlockOre(props.name, props.veinSize, props.hardness, props.harvestLevel);
        if (lightValue > 0) {
            ore.setLightLevel(lightValue / 15.0f);
        }
        ORES.put(props.name, ore);
        return ore;
    }
    
    /**
     * Create a radioactive ore block.
     */
    private static Block createRadioactiveOre(OreConfig.OreProperties props, 
                                             IRadiationSource.RadiationType type, 
                                             double radiationLevel) {
        return createRadioactiveOre(props, type, radiationLevel, 0);
    }
    
    /**
     * Create a radioactive ore block with light value.
     */
    private static Block createRadioactiveOre(OreConfig.OreProperties props,
                                             IRadiationSource.RadiationType type,
                                             double radiationLevel,
                                             int lightValue) {
        BlockRadioactiveOre ore = new BlockRadioactiveOre(props.name, props.veinSize, 
                                                          props.hardness, props.harvestLevel,
                                                          radiationLevel, type, radiationLevel);
        if (lightValue > 0) {
            ore.setLightLevel(lightValue / 15.0f);
        }
        ORES.put(props.name, ore);
        return ore;
    }
    
    /**
     * Get all registered ores.
     */
    public static Block[] getAllOres() {
        return ORES.values().toArray(new Block[0]);
    }
    
    /**
     * Get ore by name.
     */
    public static Block getOre(String name) {
        return ORES.get(name);
    }
}
