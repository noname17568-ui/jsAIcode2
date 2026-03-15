package com.atomichorizons2026.heat;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

/**
 * Default implementation of IHeatHandler.
 * Stores temperature and handles heat transfer calculations.
 */
public class HeatStorage implements IHeatHandler {
    
    // Physical constants
    public static final double ABSOLUTE_ZERO = 0.0; // 0 K
    public static final double ROOM_TEMPERATURE = 293.15; // 20°C
    public static final double WATER_BOILING = 373.15; // 100°C
    
    protected double temperature; // Current temperature in Kelvin
    protected double heatCapacity; // J/K (Joules per Kelvin)
    protected double thermalConductivity; // W/m·K
    protected double maxTemperature; // Maximum safe temperature
    protected boolean canTransfer; // Can this transfer heat?
    
    /**
     * Creates a heat storage with default values.
     * @param heatCapacity Heat capacity in J/K
     * @param thermalConductivity Thermal conductivity in W/m·K
     * @param maxTemperature Maximum safe temperature in K
     */
    public HeatStorage(double heatCapacity, double thermalConductivity, double maxTemperature) {
        this.temperature = ROOM_TEMPERATURE;
        this.heatCapacity = heatCapacity;
        this.thermalConductivity = thermalConductivity;
        this.maxTemperature = maxTemperature;
        this.canTransfer = true;
    }
    
    @Override
    public double getTemperature() {
        return temperature;
    }
    
    @Override
    public void setTemperature(double temperature) {
        this.temperature = Math.max(ABSOLUTE_ZERO, Math.min(temperature, maxTemperature * 1.5));
        
        if (this.temperature > maxTemperature) {
            onOverheat();
        }
    }
    
    @Override
    public double getHeatCapacity() {
        return heatCapacity;
    }
    
    @Override
    public double getThermalConductivity() {
        return thermalConductivity;
    }
    
    @Override
    public double addHeat(double joules, boolean simulate) {
        if (joules <= 0) return 0;
        
        // Calculate temperature increase: ΔT = Q / C
        double deltaT = joules / heatCapacity;
        double newTemp = temperature + deltaT;
        
        // Clamp to maximum (allow 50% overheat before hard limit)
        double maxAllowed = maxTemperature * 1.5;
        if (newTemp > maxAllowed) {
            deltaT = maxAllowed - temperature;
            joules = deltaT * heatCapacity;
        }
        
        if (!simulate) {
            setTemperature(temperature + deltaT);
        }
        
        return joules;
    }
    
    @Override
    public double removeHeat(double joules, boolean simulate) {
        if (joules <= 0) return 0;
        
        // Calculate temperature decrease: ΔT = Q / C
        double deltaT = joules / heatCapacity;
        double newTemp = temperature - deltaT;
        
        // Can't go below absolute zero
        if (newTemp < ABSOLUTE_ZERO) {
            deltaT = temperature - ABSOLUTE_ZERO;
            joules = deltaT * heatCapacity;
        }
        
        if (!simulate) {
            setTemperature(temperature - deltaT);
        }
        
        return joules;
    }
    
    @Override
    public boolean canTransferHeat(@Nullable EnumFacing facing) {
        return canTransfer;
    }
    
    @Override
    public double getMaxTemperature() {
        return maxTemperature;
    }
    
    @Override
    public void onOverheat() {
        // Override in subclasses for custom behavior
    }
    
    /**
     * Gets temperature in Celsius.
     * @return Temperature in °C
     */
    public double getTemperatureCelsius() {
        return temperature - 273.15;
    }
    
    /**
     * Sets temperature in Celsius.
     * @param celsius Temperature in °C
     */
    public void setTemperatureCelsius(double celsius) {
        setTemperature(celsius + 273.15);
    }
    
    /**
     * Checks if this storage is overheating.
     * @return True if temperature exceeds maximum
     */
    public boolean isOverheating() {
        return temperature > maxTemperature;
    }
    
    /**
     * Gets the overheat percentage (0.0 to 1.0+).
     * @return Overheat ratio
     */
    public double getOverheatRatio() {
        return temperature / maxTemperature;
    }
    
    /**
     * Serializes to NBT.
     * @param nbt NBT tag to write to
     */
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setDouble("temperature", temperature);
        nbt.setDouble("heatCapacity", heatCapacity);
        nbt.setDouble("thermalConductivity", thermalConductivity);
        nbt.setDouble("maxTemperature", maxTemperature);
        nbt.setBoolean("canTransfer", canTransfer);
    }
    
    /**
     * Deserializes from NBT.
     * @param nbt NBT tag to read from
     */
    public void readFromNBT(NBTTagCompound nbt) {
        this.temperature = nbt.getDouble("temperature");
        this.heatCapacity = nbt.getDouble("heatCapacity");
        this.thermalConductivity = nbt.getDouble("thermalConductivity");
        this.maxTemperature = nbt.getDouble("maxTemperature");
        this.canTransfer = nbt.getBoolean("canTransfer");
    }
}
