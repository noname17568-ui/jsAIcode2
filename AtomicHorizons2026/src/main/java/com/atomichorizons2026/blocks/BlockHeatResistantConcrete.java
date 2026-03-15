package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

/**
 * Heat Resistant Concrete - теплостойкий бетон.
 * Остается целым в высоких температурах.
 */
public class BlockHeatResistantConcrete extends Block {
    
    public BlockHeatResistantConcrete() {
        super(Material.ROCK);
        setTranslationKey("heat_resistant_concrete");
        setRegistryName(AtomicHorizons2026.MODID, "heat_resistant_concrete");
        setHardness(55.0f);
        setResistance(55.0f);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
}
