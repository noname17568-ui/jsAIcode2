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
 * PWR Reactor TileEntity.
 * Pressurized Water Reactor - Реактор с водяным охлаждением под давлением.
 * Мощность: 1000 MWe
 * Потребление топлива: 1 fuel rod / 10000 тиков
 */
public class TileEntityPWRReactor extends TileEntity implements ITickable {
    
    private static final int MAX_ENERGY = 10_000_000; // 10M RF
    private static final int ENERGY_PER_TICK = 100; // 100 RF/t
    private static final int FUEL_CONSUMPTION_RATE = 10000; // 1 fuel rod per 10000 ticks
    
    private FluidTank coolantTank = new FluidTank(50000); // 50000 mB coolant
    private EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, 0, 512, 0);
    private int fuelLevel = 0; // 0-100
    private int tickCounter = 0;
    private boolean isActive = false;
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        // Проверяем топливо и охлаждение
        if (fuelLevel > 0 && coolantTank.getFluidAmount() > 1000) {
            isActive = true;
            
            // Генерируем энергию
            energyStorage.receiveEnergy(ENERGY_PER_TICK, false);
            
            // Потребляем топливо
            tickCounter++;
            if (tickCounter >= FUEL_CONSUMPTION_RATE) {
                fuelLevel = Math.max(0, fuelLevel - 1);
                tickCounter = 0;
            }
            
            // Потребляем охлаждение
            coolantTank.drain(5, true);
            
            markDirty();
        } else {
            isActive = false;
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
    
    public boolean isActive() {
        return isActive;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("coolant")) coolantTank.readFromNBT(compound.getCompoundTag("coolant"));
        if (compound.hasKey("energy")) CapabilityEnergy.ENERGY.readNBT(energyStorage, null, compound.getTag("energy"));
        if (compound.hasKey("fuel")) fuelLevel = compound.getInteger("fuel");
        if (compound.hasKey("ticks")) tickCounter = compound.getInteger("ticks");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("coolant", coolantTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("energy", CapabilityEnergy.ENERGY.writeNBT(energyStorage, null));
        compound.setInteger("fuel", fuelLevel);
        compound.setInteger("ticks", tickCounter);
        return compound;
    }
}
