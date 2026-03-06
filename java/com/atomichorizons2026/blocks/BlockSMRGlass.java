package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Укрепленное стекло для SMR реактора.
 * Позволяет видеть внутреннее пространство реактора (стержни, свечение).
 */
public class BlockSMRGlass extends Block {
    
    public BlockSMRGlass(String name) {
        super(Material.GLASS);
        this.setUnlocalizedName(name);
        this.setRegistryName(AtomicHorizons2026.MODID, name);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        
        // Прочность стекла (укрепленное)
        this.setHardness(15.0F);
        this.setResistance(50.0F);
        
        // Прозрачность
        this.setLightOpacity(0);
    }
    
    /**
     * Стекло прозрачное
     */
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    /**
     * Не скрывает соседние блоки
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        BlockPos neighborPos = pos.offset(side);
        IBlockState neighborState = blockAccess.getBlockState(neighborPos);
        
        // Не рендерим сторону, если сосед — такое же стекло
        if (neighborState.getBlock() instanceof BlockSMRGlass) {
            return false;
        }
        
        return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
    
    /**
     * Рендеринг в TRANSLUCENT слое
     */
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    /**
     * Стекло не пропускает свет (но прозрачное)
     */
    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 0;
    }
    
    /**
     * Стекло не горит
     */
    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 0;
    }
    
    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 0;
    }
    
    /**
     * Может ли стекло соединяться с другими блоками
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isTranslucent(IBlockState state) {
        return true;
    }
}
