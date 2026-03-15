package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Heat Exchanger - теплообменник.
 * Передает тепло между двумя жидкостями.
 */
public class BlockHeatExchanger extends Block {
    
    public BlockHeatExchanger() {
        super(Material.IRON);
        setTranslationKey("heat_exchanger");
        setRegistryName(AtomicHorizons2026.MODID, "heat_exchanger");
        setHardness(5.0f);
        setResistance(10.0f);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new com.atomichorizons2026.tileentities.TileEntityHeatExchanger();
    }
}
