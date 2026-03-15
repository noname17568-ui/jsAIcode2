package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

/**
 * Heavy Concrete - тяжёлый бетон.
 * Основной строительный материал.
 */
public class BlockHeavyConcrete extends Block {
    
    public BlockHeavyConcrete() {
        super(Material.ROCK);
        setTranslationKey("heavy_concrete");
        setRegistryName(AtomicHorizons2026.MODID, "heavy_concrete");
        setHardness(50.0f);
        setResistance(50.0f);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
}
