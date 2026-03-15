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
 * Radiation Shielding Upgrade - апгрейд для брони.
 * Устанавливается в броню и добавляет +10% защиты от радиации за уровень.
 * Максимум 5 уровней = +50% защиты.
 */
public class ItemRadiationShieldingUpgrade extends ItemBase {
    
    public static final double PROTECTION_PER_LEVEL = 0.1; // 10%
    public static final int MAX_LEVELS = 5;
    public static final double MAX_PROTECTION = PROTECTION_PER_LEVEL * MAX_LEVELS; // 50%
    
    public ItemRadiationShieldingUpgrade() {
        super("radiation_shielding_upgrade");
        setMaxStackSize(64);
        setCreativeTab(CreativeTabs.MATERIALS);
    }
    
    /**
     * Получает уровень апгрейда из NBT данных брони
     */
    public static int getUpgradeLevel(ItemStack armorStack) {
        if (!armorStack.hasTagCompound()) {
            return 0;
        }
        return armorStack.getTagCompound().getInteger("RadiationShieldLevel");
    }
    
    /**
     * Устанавливает уровень апгрейда
     */
    public static void setUpgradeLevel(ItemStack armorStack, int level) {
        if (!armorStack.hasTagCompound()) {
            armorStack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
        }
        int clamped = Math.max(0, Math.min(MAX_LEVELS, level));
        armorStack.getTagCompound().setInteger("RadiationShieldLevel", clamped);
    }
    
    /**
     * Добавляет один уровень апгрейда
     */
    public static boolean addUpgradeLevel(ItemStack armorStack) {
        int current = getUpgradeLevel(armorStack);
        if (current < MAX_LEVELS) {
            setUpgradeLevel(armorStack, current + 1);
            return true;
        }
        return false;
    }
    
    /**
     * Получает защиту от радиации в процентах
     */
    public static double getRadiationProtection(ItemStack armorStack) {
        int level = getUpgradeLevel(armorStack);
        return level * PROTECTION_PER_LEVEL;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        tooltip.add(TextFormatting.AQUA + I18n.format("shield_upgrade.protection", 
            (int)(PROTECTION_PER_LEVEL * 100)));
        
        tooltip.add(TextFormatting.GRAY + I18n.format("shield_upgrade.max_levels", MAX_LEVELS));
        
        tooltip.add("");
        tooltip.add(TextFormatting.YELLOW + I18n.format("shield_upgrade.how_to_use"));
    }
}
