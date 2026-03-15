package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Step-Up Transformer - повышающий трансформатор.
 * Преобразует низкое напряжение в высокое (256 RF/t → 1024 RF/t).
 */
public class BlockStepUpTransformer extends Block {
    
    public BlockStepUpTransformer() {
        super(Material.IRON);
        setTranslationKey("step_up_transformer");
        setRegistryName(AtomicHorizons2026.MODID, "step_up_transformer");
        setHardness(5.0f);
        setResistance(10.0f);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new com.atomichorizons2026.tileentities.TileEntityStepUpTransformer();
    }
}
