package com.atomichorizons2026.items;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.radiation.PlayerRadiationCapability;
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
 * Rad-X - профилактическая таблетка от радиации.
 * Даёт +50% сопротивления радиации на 10 минут.
 * НЕ снимает накопленную дозу, только защищает от новой.
 */
public class ItemRadX extends ItemBase {
    
    public static final int DURATION = 12000; // 10 минут (600 сек * 20 тиков)
    public static final double RESISTANCE_BOOST = 0.5; // +50%
    
    public ItemRadX() {
        super("radx");
        setMaxStackSize(16);
        setCreativeTab(CreativeTabs.BREWING);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (world.isRemote) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        
        // Применяем эффект
        player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, DURATION, 0, false, false));
        
        // Увеличиваем сопротивление радиации
        PlayerRadiationCapability cap = player.getCapability(
            PlayerRadiationCapability.RADIATION_CAPABILITY, null);
        
        if (cap != null) {
            double currentResistance = cap.getRadiationResistance();
            cap.setRadiationResistance(Math.min(1.0, currentResistance + RESISTANCE_BOOST));
            
            player.sendMessage(new TextComponentString(
                TextFormatting.AQUA + I18n.format("radx.activated", DURATION / 20)));
        }
        
        // Уменьшаем стек
        stack.shrink(1);
        
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        tooltip.add(TextFormatting.AQUA + I18n.format("radx.protection", (int)(RESISTANCE_BOOST * 100)));
        tooltip.add(TextFormatting.GRAY + I18n.format("radx.duration", DURATION / 20));
        tooltip.add("");
        tooltip.add(TextFormatting.YELLOW + I18n.format("radx.note"));
    }
}
