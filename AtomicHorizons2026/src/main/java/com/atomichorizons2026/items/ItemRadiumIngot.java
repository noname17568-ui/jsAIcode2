package com.atomichorizons2026.items;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Radium Ingot - слиток радия.
 * Используется для создания RTG и других радиоактивных предметов.
 */
public class ItemRadiumIngot extends ItemBase {
    
    public ItemRadiumIngot() {
        super("radium_ingot");
        setMaxStackSize(64);
        setCreativeTab(CreativeTabs.MATERIALS);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        tooltip.add(TextFormatting.RED + I18n.format("radium_ingot.radioactive"));
        tooltip.add(TextFormatting.GRAY + I18n.format("radium_ingot.desc"));
    }
}
