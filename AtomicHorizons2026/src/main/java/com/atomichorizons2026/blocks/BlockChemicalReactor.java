package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Chemical Reactor - химический реактор.
 * Объединяет две жидкости в одну.
 */
public class BlockChemicalReactor extends Block {
    
    public BlockChemicalReactor() {
        super(Material.IRON);
        setTranslationKey("chemical_reactor");
        setRegistryName(AtomicHorizons2026.MODID, "chemical_reactor");
        setHardness(5.0f);
        setResistance(10.0f);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new com.atomichorizons2026.tileentities.TileEntityChemicalReactor();
    }
}
