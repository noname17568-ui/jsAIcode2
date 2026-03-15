package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Mechanical Draft Cooling Tower - механическая градирня.
 * Охлаждает жидкость с помощью энергии.
 */
public class BlockMechanicalDraftTower extends Block {
    
    public BlockMechanicalDraftTower() {
        super(Material.IRON);
        setTranslationKey("mechanical_draft_tower");
        setRegistryName(AtomicHorizons2026.MODID, "mechanical_draft_tower");
        setHardness(5.0f);
        setResistance(10.0f);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new com.atomichorizons2026.tileentities.TileEntityMechanicalDraftTower();
    }
}
