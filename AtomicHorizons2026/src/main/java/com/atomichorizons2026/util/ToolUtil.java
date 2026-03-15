package com.atomichorizons2026.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Utility class for tool-related operations.
 * Provides helper methods for Geiger Counter and Dosimeter functionality.
 */
public class ToolUtil {
    
    private static final String RADIATION_READING_KEY = "radiationReading";
    private static final String LAST_UPDATE_KEY = "lastUpdate";
    private static final String BATTERY_KEY = "battery";
    
    /**
     * Get radiation reading from Geiger Counter.
     * @param tool Tool ItemStack
     * @return Radiation reading (0-100)
     */
    public static float getRadiationReading(ItemStack tool) {
        if (tool.isEmpty()) {
            return 0.0f;
        }
        
        NBTTagCompound tag = tool.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            tool.setTagCompound(tag);
        }
        
        return tag.getFloat(RADIATION_READING_KEY);
    }
    
    /**
     * Set radiation reading for Geiger Counter.
     * @param tool Tool ItemStack
     * @param reading Radiation reading (0-100)
     */
    public static void setRadiationReading(ItemStack tool, float reading) {
        if (tool.isEmpty()) {
            return;
        }
        
        NBTTagCompound tag = tool.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            tool.setTagCompound(tag);
        }
        
        tag.setFloat(RADIATION_READING_KEY, Math.max(0, reading));
        tag.setLong(LAST_UPDATE_KEY, System.currentTimeMillis());
    }
    
    /**
     * Get battery level from tool.
     * @param tool Tool ItemStack
     * @return Battery level (0-100)
     */
    public static float getBatteryLevel(ItemStack tool) {
        if (tool.isEmpty()) {
            return 0.0f;
        }
        
        NBTTagCompound tag = tool.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            tool.setTagCompound(tag);
        }
        
        return tag.getFloat(BATTERY_KEY);
    }
    
    /**
     * Set battery level for tool.
     * @param tool Tool ItemStack
     * @param level Battery level (0-100)
     */
    public static void setBatteryLevel(ItemStack tool, float level) {
        if (tool.isEmpty()) {
            return;
        }
        
        NBTTagCompound tag = tool.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            tool.setTagCompound(tag);
        }
        
        tag.setFloat(BATTERY_KEY, Math.max(0, Math.min(100, level)));
    }
    
    /**
     * Drain battery from tool.
     * @param tool Tool ItemStack
     * @param amount Amount to drain
     * @return True if tool has enough battery
     */
    public static boolean drainBattery(ItemStack tool, float amount) {
        float current = getBatteryLevel(tool);
        if (current < amount) {
            return false;
        }
        setBatteryLevel(tool, current - amount);
        return true;
    }
}
