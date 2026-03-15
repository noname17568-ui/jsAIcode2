package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

/**
 * Radiation Shielding Concrete - бетон с радиационной защитой.
 * Поглощает радиацию и снижает её в соседних блоках.
 */
public class BlockRadiationShieldingConcrete extends Block {
    
    public BlockRadiationShieldingConcrete() {
        super(Material.ROCK);
        setTranslationKey("radiation_shielding_concrete");
        setRegistryName(AtomicHorizons2026.MODID, "radiation_shielding_concrete");
        setHardness(60.0f);
        setResistance(60.0f);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setLightLevel(0.1f);
    }
}
