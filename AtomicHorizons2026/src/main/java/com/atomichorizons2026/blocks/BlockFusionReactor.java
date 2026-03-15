package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Fusion Reactor - Thermonuclear Fusion Reactor (Термоядерный реактор синтеза).
 * Мощность: 2000 MWe
 * Тип топлива: Deuterium + Tritium
 * Потребление топлива: 1 fuel rod / 5000 тиков
 * Преимущества: Чистая энергия, минимум отходов
 */
public class BlockFusionReactor extends Block {
    
    public BlockFusionReactor() {
        super(Material.IRON);
        setTranslationKey("fusion_reactor");
        setRegistryName(AtomicHorizons2026.MODID, "fusion_reactor");
        setHardness(9.0f);
        setResistance(20.0f);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new com.atomichorizons2026.tileentities.TileEntityFusionReactor();
    }
}
