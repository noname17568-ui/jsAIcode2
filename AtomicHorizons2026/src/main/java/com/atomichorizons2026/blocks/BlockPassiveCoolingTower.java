package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Passive Cooling Tower - пассивная градирня.
 * Охлаждает жидкость без энергии.
 */
public class BlockPassiveCoolingTower extends Block {
    
    public BlockPassiveCoolingTower() {
        super(Material.IRON);
        setTranslationKey("passive_cooling_tower");
        setRegistryName(AtomicHorizons2026.MODID, "passive_cooling_tower");
        setHardness(5.0f);
        setResistance(10.0f);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new com.atomichorizons2026.tileentities.TileEntityPassiveCoolingTower();
    }
}
