package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * PWR Reactor - Pressurized Water Reactor (Реактор с водяным охлаждением под давлением).
 * Мощность: 1000 MWe
 * Тип охлаждения: Вода под давлением
 * Потребление топлива: 1 fuel rod / 10000 тиков
 */
public class BlockPWRReactor extends Block {
    
    public BlockPWRReactor() {
        super(Material.IRON);
        setTranslationKey("pwr_reactor");
        setRegistryName(AtomicHorizons2026.MODID, "pwr_reactor");
        setHardness(8.0f);
        setResistance(15.0f);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new com.atomichorizons2026.tileentities.TileEntityPWRReactor();
    }
}
