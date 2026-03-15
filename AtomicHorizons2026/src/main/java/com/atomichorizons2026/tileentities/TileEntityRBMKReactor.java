package com.atomichorizons2026.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import com.atomichorizons2026.util.FluidTank;

import javax.annotation.Nullable;

/**
 * RBMK Reactor TileEntity.
 * High-Power Channel-Type Reactor - Реактор большой мощности канального типа.
 * Мощность: 1500 MWe
 * Потребление топлива: 1 fuel rod / 8000 тиков
 * Опасность: Высокая (требует постоянного охлаждения)
 */
public class TileEntityRBMKReactor extends TileEntity implements ITickable {
    
    private static final int MAX_ENERGY = 15_000_000; // 15M RF
    private static final int ENERGY_PER_TICK = 150; // 150 RF/t
    private static final int FUEL_CONSUMPTION_RATE = 8000; // 1 fuel rod per 8000 ticks
    
    private FluidTank coolantTank = new FluidTank(75000); // 75000 mB coolant
    private EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, 0, 512, 0);
    private int fuelLevel = 0; // 0-100
    private int tickCounter = 0;
    private int temperature = 0; // 0-100 (опасно при >80)
    private boolean isActive = false;
    private boolean isMeltdown = false;
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        // Проверяем топливо и охлаждение
        if (fuelLevel > 0 && coolantTank.getFluidAmount() > 2000) {
            isActive = true;
            
            // Генерируем энергию
            energyStorage.receiveEnergy(ENERGY_PER_TICK, false);
            
            // Потребляем топливо
            tickCounter++;
            if (tickCounter >= FUEL_CONSUMPTION_RATE) {
                fuelLevel = Math.max(0, fuelLevel - 1);
                tickCounter = 0;
            }
            
            // Потребляем охлаждение и повышаем температуру
            coolantTank.drain(8, true);
            temperature = Math.min(100, temperature + 1);
            
            // Проверяем перегрев
            if (temperature > 80 && coolantTank.getFluidAmount() < 5000) {
                isMeltdown = true;
            }
            
            markDirty();
        } else {
            isActive = false;
            // Охлаждение
            temperature = Math.max(0, temperature - 1);
        }
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || 
            capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }
    
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(coolantTank);
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }
        return super.getCapability(capability, facing);
    }
    
    public int getFuelLevel() {
        return fuelLevel;
    }
    
    public void setFuelLevel(int level) {
        this.fuelLevel = Math.min(100, level);
    }
    
    public int getTemperature() {
        return temperature;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public boolean isMeltdown() {
        return isMeltdown;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("coolant")) coolantTank.readFromNBT(compound.getCompoundTag("coolant"));
        if (compound.hasKey("energy")) CapabilityEnergy.ENERGY.readNBT(energyStorage, null, compound.getTag("energy"));
        if (compound.hasKey("fuel")) fuelLevel = compound.getInteger("fuel");
        if (compound.hasKey("ticks")) tickCounter = compound.getInteger("ticks");
        if (compound.hasKey("temp")) temperature = compound.getInteger("temp");
        if (compound.hasKey("meltdown")) isMeltdown = compound.getBoolean("meltdown");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("coolant", coolantTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("energy", CapabilityEnergy.ENERGY.writeNBT(energyStorage, null));
        compound.setInteger("fuel", fuelLevel);
        compound.setInteger("ticks", tickCounter);
        compound.setInteger("temp", temperature);
        compound.setBoolean("meltdown", isMeltdown);
        return compound;
    }
}
