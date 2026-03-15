package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Centrifuge - центрифуга.
 * Отделяет частицы от жидкости.
 */
public class BlockCentrifuge extends Block {
    
    public BlockCentrifuge() {
        super(Material.IRON);
        setTranslationKey("centrifuge");
        setRegistryName(AtomicHorizons2026.MODID, "centrifuge");
        setHardness(5.0f);
        setResistance(10.0f);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new com.atomichorizons2026.tileentities.TileEntityCentrifuge();
    }
}
