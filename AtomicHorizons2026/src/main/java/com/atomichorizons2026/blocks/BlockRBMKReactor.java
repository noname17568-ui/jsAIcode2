package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * RBMK Reactor - High-Power Channel-Type Reactor (Реактор большой мощности канального типа).
 * Мощность: 1500 MWe
 * Тип охлаждения: Кипящая вода
 * Потребление топлива: 1 fuel rod / 8000 тиков
 * Опасность: Высокая (требует постоянного охлаждения)
 */
public class BlockRBMKReactor extends Block {
    
    public BlockRBMKReactor() {
        super(Material.IRON);
        setTranslationKey("rbmk_reactor");
        setRegistryName(AtomicHorizons2026.MODID, "rbmk_reactor");
        setHardness(8.0f);
        setResistance(15.0f);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new com.atomichorizons2026.tileentities.TileEntityRBMKReactor();
    }
}
