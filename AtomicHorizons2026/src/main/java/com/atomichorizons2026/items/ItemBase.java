package com.atomichorizons2026.items;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item {
    
    protected String name;
    
    public ItemBase(String name) {
        this.name = name;
        setTranslationKey(name);
        setRegistryName(AtomicHorizons2026.MODID, name);
        setCreativeTab(AtomicHorizons2026.CREATIVE_TAB);
    }
    
    public void registerItemModel() {
        AtomicHorizons2026.proxy.registerItemRenderer(this, 0, name);
    }
    
    @Override
    public ItemBase setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }
}
