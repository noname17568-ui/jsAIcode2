package com.atomichorizons2026.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    
    public void preInit(FMLPreInitializationEvent event) {
        // Server-side pre-initialization
    }
    
    public void init(FMLInitializationEvent event) {
        // Server-side initialization
    }
    
    public void postInit(FMLPostInitializationEvent event) {
        // Server-side post-initialization
    }
    
    public void registerItemRenderer(Item item, int meta, String id) {
        // Server-side: do nothing, client will override
    }
}
