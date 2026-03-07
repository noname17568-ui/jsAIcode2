package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBase extends Block {
    
    protected String name;
    
    public BlockBase(String name, Material material, float hardness, int harvestLevel) {
        super(material);
        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(AtomicHorizons2026.MODID, name);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setHardness(hardness);
        setHarvestLevel("pickaxe", harvestLevel);
    }
    
    public BlockBase(String name, Material material) {
        this(name, material, 3.0f, 1);
    }
    
    public void registerItemModel(Item itemBlock) {
        AtomicHorizons2026.proxy.registerItemRenderer(itemBlock, 0, name);
    }
    
    public Item createItemBlock() {
        return new ItemBlock(this).setRegistryName(getRegistryName());
    }
    
    @Override
    public BlockBase setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }
}
