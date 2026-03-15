package com.atomichorizons2026.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

/**
 * Pumping Station TileEntity.
 * Перекачивает жидкости между резервуарами.
 * Потребление: 60 RF/t
 */
public class TileEntityPumpingStation extends TileEntity implements ITickable {
    
    private static final int FLUID_CAPACITY = 10000; // 10000 mB
    private static final int MAX_ENERGY = 100_000; // 100k RF
    private static final int ENERGY_PER_TICK = 60; // 60 RF/t
    
    private FluidTank inputTank = new FluidTank(FLUID_CAPACITY);
    private FluidTank outputTank = new FluidTank(FLUID_CAPACITY);
    private EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, 256, 0, 0);
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        // Проверяем энергию и жидкость
        if (energyStorage.getEnergyStored() >= ENERGY_PER_TICK && 
            inputTank.getFluidAmount() > 0 &&
            outputTank.getFluidAmount() < outputTank.getCapacity()) {
            
            // Перекачиваем жидкость: сохраняем тип жидкости из inputTank
            FluidStack drained = inputTank.drain(25, false);
            if (drained != null) {
                int filled = outputTank.fill(drained, false);
                if (filled > 0) {
                    inputTank.drain(filled, true);
                    outputTank.fill(new FluidStack(drained.getFluid(), filled), true);
                    energyStorage.extractEnergy(ENERGY_PER_TICK, false);
                }
            }
            markDirty();
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
            if (facing == net.minecraft.util.EnumFacing.NORTH) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(inputTank);
            } else {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(outputTank);
            }
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }
        return super.getCapability(capability, facing);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("input")) inputTank.readFromNBT(compound.getCompoundTag("input"));
        if (compound.hasKey("output")) outputTank.readFromNBT(compound.getCompoundTag("output"));
        if (compound.hasKey("energy")) CapabilityEnergy.ENERGY.readNBT(energyStorage, null, compound.getTag("energy"));
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("input", inputTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("output", outputTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("energy", CapabilityEnergy.ENERGY.writeNBT(energyStorage, null));
        return compound;
    }
}
