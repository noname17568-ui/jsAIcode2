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
 * Mechanical Draft Tower TileEntity.
 * Охлаждает жидкость с помощью энергии.
 * Охлаждение: 15 температуры в секунду.
 * Потребление: 50 RF/t
 */
public class TileEntityMechanicalDraftTower extends TileEntity implements ITickable {
    
    private static final int FLUID_CAPACITY = 10000; // 10000 mB
    private static final int MAX_ENERGY = 100_000; // 100k RF
    private static final int ENERGY_PER_TICK = 50; // 50 RF/t
    private static final double COOLING_RATE = 15.0; // 15 температуры/сек
    
    private FluidTank fluidTank = new FluidTank(FLUID_CAPACITY);
    private EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, 256, 0, 0);
    private double temperature = 20.0;
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        // Охлаждаем жидкость если есть энергия
        if (fluidTank.getFluidAmount() > 0 && energyStorage.getEnergyStored() >= ENERGY_PER_TICK) {
            temperature = Math.max(20.0, temperature - COOLING_RATE / 20.0);
            energyStorage.extractEnergy(ENERGY_PER_TICK, false);
            markDirty();
        }
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(double temp) {
        this.temperature = temp;
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
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidTank);
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }
        return super.getCapability(capability, facing);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("fluid")) {
            fluidTank.readFromNBT(compound.getCompoundTag("fluid"));
        }
        if (compound.hasKey("energy")) {
            CapabilityEnergy.ENERGY.readNBT(energyStorage, null, compound.getTag("energy"));
        }
        temperature = compound.getDouble("temperature");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("fluid", fluidTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("energy", CapabilityEnergy.ENERGY.writeNBT(energyStorage, null));
        compound.setDouble("temperature", temperature);
        return compound;
    }
}
