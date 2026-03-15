package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.tileentities.TileEntityRadiationScrubber;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Radiation Scrubber - машина для очистки чанка от радиации.
 * Требует энергию (RF) и работает автоматически.
 */
public class BlockRadiationScrubber extends Block {
    
    public BlockRadiationScrubber() {
        super(Material.IRON);
        setTranslationKey("radiation_scrubber");
        setRegistryName(AtomicHorizons2026.MODID, "radiation_scrubber");
        setHardness(5.0f);
        setResistance(10.0f);
        setLightLevel(0.3f);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityRadiationScrubber();
    }
}
