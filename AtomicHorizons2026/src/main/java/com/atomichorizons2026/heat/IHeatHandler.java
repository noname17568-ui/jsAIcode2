package com.atomichorizons2026.heat;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

/**
 * Interface for blocks/TileEntities that can handle heat transfer.
 * Similar to IEnergyStorage but for thermal energy.
 */
public interface IHeatHandler {
    
    /**
     * Gets the current temperature in Kelvin.
     * @return Temperature in K (273.15 = 0°C)
     */
    double getTemperature();
    
    /**
     * Sets the temperature in Kelvin.
     * @param temperature Temperature in K
     */
    void setTemperature(double temperature);
    
    /**
     * Gets the heat capacity in J/K (Joules per Kelvin).
     * Higher capacity = slower temperature changes.
     * @return Heat capacity in J/K
     */
    double getHeatCapacity();
    
    /**
     * Gets the thermal conductivity (W/m·K).
     * Higher conductivity = faster heat transfer.
     * @return Thermal conductivity
     */
    double getThermalConductivity();
    
    /**
     * Adds heat energy to this handler.
     * @param joules Amount of heat in Joules
     * @param simulate If true, only simulate the transfer
     * @return Actual amount of heat added
     */
    double addHeat(double joules, boolean simulate);
    
    /**
     * Removes heat energy from this handler.
     * @param joules Amount of heat in Joules
     * @param simulate If true, only simulate the transfer
     * @return Actual amount of heat removed
     */
    double removeHeat(double joules, boolean simulate);
    
    /**
     * Checks if this handler can transfer heat on the given side.
     * @param facing The side to check (null = any side)
     * @return True if heat can be transferred
     */
    boolean canTransferHeat(@Nullable EnumFacing facing);
    
    /**
     * Gets the maximum safe temperature in Kelvin.
     * Exceeding this may cause damage or meltdown.
     * @return Max safe temperature in K
     */
    double getMaxTemperature();
    
    /**
     * Called when temperature exceeds maximum.
     * Can be used for meltdown mechanics.
     */
    void onOverheat();
}
