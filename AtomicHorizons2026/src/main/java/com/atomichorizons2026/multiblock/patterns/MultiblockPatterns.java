package com.atomichorizons2026.multiblock.patterns;

import com.atomichorizons2026.handlers.RegistryHandler;
import com.atomichorizons2026.multiblock.MultiblockPattern;
import com.atomichorizons2026.multiblock.StructureTemplate;
import net.minecraft.block.Block;

/**
 * Predefined multiblock patterns for reactors and structures.
 */
public class MultiblockPatterns {
    
    /**
     * Small Modular Reactor (SMR) - 5x5x5 structure
     * 
     * Layout (Y=0, bottom layer):
     * C C C C C
     * C P P P C
     * C P X P C  (X = Controller)
     * C P P P C
     * C C C C C
     * 
     * Layout (Y=1-3, middle layers):
     * C C C C C
     * C G G G C
     * C G O G C  (O = Core)
     * C G G G C
     * C C C C C
     * 
     * Layout (Y=4, top layer):
     * C C C C C
     * C H H H C  (H = Housing)
     * C H H H C
     * C H H H C
     * C C C C C
     * 
     * Legend:
     * C = SMR Casing
     * P = SMR Port
     * X = SMR Controller
     * G = SMR Glass
     * O = SMR Core
     * H = SMR Housing
     */
    public static MultiblockPattern createSMRPattern() {
        Block CASING = RegistryHandler.SMR_CASING;
        Block PORT = RegistryHandler.SMR_PORT;
        Block CONTROLLER = RegistryHandler.SMR_CONTROLLER;
        Block GLASS = RegistryHandler.SMR_GLASS;
        Block CORE = RegistryHandler.SMR_CORE;
        Block HOUSING = RegistryHandler.SMR_HOUSING;
        
        return StructureTemplate
            .builder("smr_5x5x5")
            .size(5, 5, 5)
            .controllerAt(2, 0, 2) // Controller at center of bottom layer
            .create()
            
            // Layer 0 (bottom)
            .layer(0)
            .row(0, CASING, CASING, CASING, CASING, CASING)
            .row(1, CASING, PORT, PORT, PORT, CASING)
            .row(2, CASING, PORT, CONTROLLER, PORT, CASING)
            .row(3, CASING, PORT, PORT, PORT, CASING)
            .row(4, CASING, CASING, CASING, CASING, CASING)
            
            // Layer 1 (first middle)
            .layer(1)
            .row(0, CASING, CASING, CASING, CASING, CASING)
            .row(1, CASING, GLASS, GLASS, GLASS, CASING)
            .row(2, CASING, GLASS, CORE, GLASS, CASING)
            .row(3, CASING, GLASS, GLASS, GLASS, CASING)
            .row(4, CASING, CASING, CASING, CASING, CASING)
            
            // Layer 2 (second middle)
            .layer(2)
            .row(0, CASING, CASING, CASING, CASING, CASING)
            .row(1, CASING, GLASS, GLASS, GLASS, CASING)
            .row(2, CASING, GLASS, CORE, GLASS, CASING)
            .row(3, CASING, GLASS, GLASS, GLASS, CASING)
            .row(4, CASING, CASING, CASING, CASING, CASING)
            
            // Layer 3 (third middle)
            .layer(3)
            .row(0, CASING, CASING, CASING, CASING, CASING)
            .row(1, CASING, GLASS, GLASS, GLASS, CASING)
            .row(2, CASING, GLASS, CORE, GLASS, CASING)
            .row(3, CASING, GLASS, GLASS, GLASS, CASING)
            .row(4, CASING, CASING, CASING, CASING, CASING)
            
            // Layer 4 (top)
            .layer(4)
            .row(0, CASING, CASING, CASING, CASING, CASING)
            .row(1, CASING, HOUSING, HOUSING, HOUSING, CASING)
            .row(2, CASING, HOUSING, HOUSING, HOUSING, CASING)
            .row(3, CASING, HOUSING, HOUSING, HOUSING, CASING)
            .row(4, CASING, CASING, CASING, CASING, CASING)
            
            .build();
    }
    
    /**
     * Gets the SMR pattern (cached).
     */
    private static MultiblockPattern SMR_PATTERN = null;
    
    public static MultiblockPattern getSMRPattern() {
        if (SMR_PATTERN == null) {
            SMR_PATTERN = createSMRPattern();
        }
        return SMR_PATTERN;
    }
}
