package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.tileentities.TileEntityIndustrialSmelter;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockIndustrialSmelter extends Block implements ITileEntityProvider {

    public BlockIndustrialSmelter(String name) {
        super(Material.IRON);
        setTranslationKey("tile." + name);
        setRegistryName(AtomicHorizons2026.MODID, name);
        setHardness(5.0f);
        setResistance(10.0f);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(AtomicHorizons2026.CREATIVE_TAB);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityIndustrialSmelter();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
}
