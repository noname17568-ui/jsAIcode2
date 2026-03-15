package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Distillery - дистилляционная колонна.
 * Очищает и разделяет жидкости.
 */
public class BlockDistillery extends Block {
    
    public BlockDistillery() {
        super(Material.IRON);
        setTranslationKey("distillery");
        setRegistryName(AtomicHorizons2026.MODID, "distillery");
        setHardness(5.0f);
        setResistance(10.0f);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new com.atomichorizons2026.tileentities.TileEntityDistillery();
    }
}
