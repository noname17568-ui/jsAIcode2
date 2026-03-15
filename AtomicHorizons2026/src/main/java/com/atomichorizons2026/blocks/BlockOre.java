package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Base class for ore blocks.
 * Ores drop raw materials (not the block itself) for processing.
 */
public class BlockOre extends BlockBase {
    
    private int minExp;
    private int maxExp;
    private Item dropItem; // What item this ore drops
    
    public BlockOre(String name, int harvestLevel, float hardness, int minExp, int maxExp) {
        super(name, Material.ROCK, hardness, harvestLevel);
        this.minExp = minExp;
        this.maxExp = maxExp;
        this.dropItem = null; // Will be set via setDropItem()
        setCreativeTab(AtomicHorizons2026.CREATIVE_TAB);
    }
    
    public BlockOre(String name, int harvestLevel, float hardness, int exp) {
        this(name, harvestLevel, hardness, exp, exp);
    }
    
    /**
     * Sets the item that this ore drops when mined.
     * @param item The raw material item
     * @return This ore block for chaining
     */
    public BlockOre setDropItem(Item item) {
        this.dropItem = item;
        return this;
    }
    
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        // If a drop item is set, drop that; otherwise drop the block itself
        return dropItem != null ? dropItem : Item.getItemFromBlock(this);
    }
    
    @Override
    public int quantityDropped(Random random) {
        return 1;
    }
    
    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0 && dropItem != null) {
            // Fortune increases raw material drops
            int bonus = random.nextInt(fortune + 2) - 1;
            if (bonus < 0) bonus = 0;
            return quantityDropped(random) + bonus;
        }
        return quantityDropped(random);
    }
    
    @Override
    public int getExpDrop(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
        // Only drop XP if we're dropping a raw material (not the block itself)
        if (dropItem != null) {
            Random rand = world instanceof World ? ((World)world).rand : new Random();
            return MathHelper.getInt(rand, minExp, maxExp);
        }
        return 0;
    }
}
