package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Fluid Tank - резервуар для жидкостей.
 * Хранит до 100000 mB жидкости.
 */
public class BlockFluidTank extends Block {
    
    public BlockFluidTank() {
        super(Material.IRON);
        setTranslationKey("fluid_tank");
        setRegistryName(AtomicHorizons2026.MODID, "fluid_tank");
        setHardness(4.0f);
        setResistance(8.0f);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new com.atomichorizons2026.tileentities.TileEntityFluidTank();
    }
}
