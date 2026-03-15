package com.atomichorizons2026.items;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Йодид калия (KI) - защита щитовидной железы от радиоактивного йода.
 * Даёт эффект Resistance на 5 минут.
 * Используется при аварийных ситуациях.
 */
public class ItemPotassiumIodide extends ItemBase {
    
    public static final int DURATION = 6000; // 5 минут
    
    public ItemPotassiumIodide() {
        super("potassium_iodide");
        setMaxStackSize(32);
        setCreativeTab(CreativeTabs.BREWING);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (world.isRemote) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        
        // Даём эффект Resistance (защита от урона)
        player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, DURATION, 0, false, false));
        
        player.sendMessage(new TextComponentString(
            TextFormatting.GREEN + I18n.format("potassium_iodide.used")));
        
        stack.shrink(1);
        
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        tooltip.add(TextFormatting.AQUA + I18n.format("potassium_iodide.protection"));
        tooltip.add(TextFormatting.GRAY + I18n.format("potassium_iodide.duration", DURATION / 20));
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_GRAY + I18n.format("potassium_iodide.note"));
    }
}
