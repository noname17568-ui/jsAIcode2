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
 * Укрепленная обшивка SMR реактора.
 * Сделана из стали со свинцовой прослойкой для защиты от радиации.
 */
public class BlockSMRHousing extends Block {
    
    public BlockSMRHousing(String name) {
        super(Material.IRON);
        this.setTranslationKey(name);
        this.setRegistryName(AtomicHorizons2026.MODID, name);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        
        // Очень прочный блок
        this.setHardness(25.0F);
        this.setResistance(200.0F); // Устойчив к взрывам
        this.setHarvestLevel("pickaxe", 2);
    }
    
    /**
     * Блок непрозрачный
     */
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }
    
    /**
     * Блок не пропускает свет
     */
    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 15;
    }
    
    /**
     * Рендеринг в SOLID слое
     */
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.SOLID;
    }
    
    /**
     * Блок не горит
     */
    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 0;
    }
    
    /**
     * Не воспламеняется
     */
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 0;
    }
}
