package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Pressure Vessel - сосуд высокого давления.
 * Хранит газы и жидкости под давлением.
 * Максимальное давление: 100 атм
 */
public class BlockPressureVessel extends Block {
    
    public BlockPressureVessel() {
        super(Material.IRON);
        setTranslationKey("pressure_vessel");
        setRegistryName(AtomicHorizons2026.MODID, "pressure_vessel");
        setHardness(6.0f);
        setResistance(12.0f);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new com.atomichorizons2026.tileentities.TileEntityPressureVessel();
    }
}
