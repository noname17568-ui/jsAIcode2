package com.atomichorizons2026.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class BlockOre extends BlockBase {
    
    private int minExp;
    private int maxExp;
    
    public BlockOre(String name, int harvestLevel, float hardness, int minExp, int maxExp) {
        super(name, Material.ROCK, hardness, harvestLevel);
        this.minExp = minExp;
        this.maxExp = maxExp;
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
    
    public BlockOre(String name, int harvestLevel, float hardness, int exp) {
        this(name, harvestLevel, hardness, exp, exp);
    }
    
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        // Return the block itself by default
        return Item.getItemFromBlock(this);
    }
    
    @Override
    public int quantityDropped(Random random) {
        return 1;
    }
    
    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0) {
            int bonus = random.nextInt(fortune + 2) - 1;
            if (bonus < 0) bonus = 0;
            return quantityDropped(random) * (bonus + 1);
        }
        return quantityDropped(random);
    }
    
    @Override
    public int getExpDrop(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        return MathHelper.getInt(rand, minExp, maxExp);
    }
}
