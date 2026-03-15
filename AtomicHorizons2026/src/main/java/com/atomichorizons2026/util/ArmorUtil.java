package com.atomichorizons2026.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Utility class for armor-related operations.
 * Provides helper methods for armor effects and protection calculations.
 */
public class ArmorUtil {
    
    private static final String RADIATION_PROTECTION_KEY = "radiationProtection";
    private static final String DURABILITY_KEY = "armorDurability";
    
    /**
     * Get radiation protection level from armor piece.
     * @param armor Armor ItemStack
     * @return Protection level (0-100)
     */
    public static float getRadiationProtection(ItemStack armor) {
        if (armor.isEmpty()) {
            return 0.0f;
        }
        
        NBTTagCompound tag = armor.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            armor.setTagCompound(tag);
        }
        
        return tag.getFloat(RADIATION_PROTECTION_KEY);
    }
    
    /**
     * Set radiation protection level for armor piece.
     * @param armor Armor ItemStack
     * @param protection Protection level (0-100)
     */
    public static void setRadiationProtection(ItemStack armor, float protection) {
        if (armor.isEmpty()) {
            return;
        }
        
        NBTTagCompound tag = armor.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            armor.setTagCompound(tag);
        }
        
        tag.setFloat(RADIATION_PROTECTION_KEY, Math.max(0, Math.min(100, protection)));
    }
    
    /**
     * Calculate total radiation protection from full armor set.
     * @param helmet Helmet
     * @param chestplate Chestplate
     * @param leggings Leggings
     * @param boots Boots
     * @return Total protection (0-100)
     */
    public static float calculateTotalProtection(ItemStack helmet, ItemStack chestplate, 
                                                 ItemStack leggings, ItemStack boots) {
        float total = 0.0f;
        total += getRadiationProtection(helmet) * 0.1f;      // 10%
        total += getRadiationProtection(chestplate) * 0.4f;  // 40%
        total += getRadiationProtection(leggings) * 0.3f;    // 30%
        total += getRadiationProtection(boots) * 0.2f;       // 20%
        return Math.min(100, total);
    }
    
    /**
     * Reduce armor durability due to radiation exposure.
     * @param armor Armor ItemStack
     * @param damage Damage amount
     */
    public static void damageArmorFromRadiation(ItemStack armor, int damage) {
        if (armor.isEmpty()) {
            return;
        }
        armor.damageItem(damage, null);
    }
}
