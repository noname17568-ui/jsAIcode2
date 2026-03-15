package com.atomichorizons2026.registry;

import com.atomichorizons2026.blocks.BlockBase;
import com.atomichorizons2026.blocks.BlockCorium;
import com.atomichorizons2026.blocks.BlockRadioactiveOre;
import com.atomichorizons2026.blocks.BlockSMRController;
import com.atomichorizons2026.blocks.BlockSMRCore;
import com.atomichorizons2026.blocks.BlockSMRGlass;
import com.atomichorizons2026.blocks.BlockSMRHousing;
import com.atomichorizons2026.blocks.BlockSMRPort;
import com.atomichorizons2026.config.BlockConfig;
import com.atomichorizons2026.radiation.IRadiationSource;
import net.minecraft.block.Block;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for storage and special blocks.
 * Handles creation and registration of non-ore blocks.
 */
public class BlockRegistry {
    
    private static final Map<String, Block> BLOCKS = new HashMap<>();
    private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("atomichorizons2026");
    
    // Storage blocks
    public static Block BLOCK_GRAPHITE;
    public static Block BLOCK_LEAD;
    public static Block BLOCK_STEEL;
    public static Block BLOCK_CONCRETE;
    public static Block BLOCK_REINFORCED_CONCRETE;
    public static Block BLOCK_RADIUM;
    
    // SMR (Small Modular Reactor) blocks
    public static Block SMR_CORE;
    public static Block SMR_CASING;
    public static Block SMR_CONTROLLER;
    public static Block SMR_HOUSING;
    public static Block SMR_PORT;
    public static Block SMR_GLASS;
    public static Block CORIUM;
    
    /**
     * Initialize all storage and special blocks.
     */
    public static void init() {
        try {
            // Storage blocks
            BLOCK_GRAPHITE = createBlock(BlockConfig.GRAPHITE);
            BLOCK_LEAD = createBlock(BlockConfig.LEAD);
            BLOCK_STEEL = createBlock(BlockConfig.STEEL);
            BLOCK_CONCRETE = createBlock(BlockConfig.CONCRETE);
            BLOCK_REINFORCED_CONCRETE = createBlock(BlockConfig.REINFORCED_CONCRETE);
            
            // Radioactive storage block
            BLOCK_RADIUM = new BlockRadioactiveOre("block_radium", 3, 20.0f, 0, 
                                                   200.0, IRadiationSource.RadiationType.GAMMA, 10.0);
            BLOCK_RADIUM.setLightLevel(1.0f);
            BLOCKS.put("block_radium", BLOCK_RADIUM);
            
            // SMR blocks
            SMR_CORE = new BlockSMRCore("smr_core");
            SMR_CASING = createBlock(BlockConfig.SMR_CASING);
            SMR_CONTROLLER = new BlockSMRController("smr_controller");
            SMR_HOUSING = new BlockSMRHousing("smr_housing");
            SMR_PORT = new BlockSMRPort("smr_port");
            SMR_GLASS = new BlockSMRGlass("smr_glass");
            CORIUM = new BlockCorium("corium");
            
            BLOCKS.put("smr_core", SMR_CORE);
            BLOCKS.put("smr_casing", SMR_CASING);
            BLOCKS.put("smr_controller", SMR_CONTROLLER);
            BLOCKS.put("smr_housing", SMR_HOUSING);
            BLOCKS.put("smr_port", SMR_PORT);
            BLOCKS.put("smr_glass", SMR_GLASS);
            BLOCKS.put("corium", CORIUM);
            
            LOGGER.info("Block registry initialized successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize block registry", e);
            throw new RuntimeException("Block registry initialization failed", e);
        }
    }
    
    /**
     * Create a storage block from configuration.
     */
    private static Block createBlock(BlockConfig.BlockProperties props) {
        BlockBase block = new BlockBase(props.name, props.material, props.hardness, props.harvestLevel);
        BLOCKS.put(props.name, block);
        return block;
    }
    
    /**
     * Get all registered blocks.
     */
    public static Block[] getAllBlocks() {
        return BLOCKS.values().toArray(new Block[0]);
    }
    
    /**
     * Get block by name.
     */
    public static Block getBlock(String name) {
        return BLOCKS.get(name);
    }
}
