package com.atomichorizons2026.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Passive Cooling Tower TileEntity.
 * Охлаждает жидкость без энергии.
 * Охлаждение: 5 температуры в секунду.
 */
public class TileEntityPassiveCoolingTower extends TileEntity implements ITickable {
    
    private static final int FLUID_CAPACITY = 10000; // 10000 mB
    private static final double COOLING_RATE = 5.0; // 5 температуры/сек
    
    private FluidTank fluidTank = new FluidTank(FLUID_CAPACITY);
    private double temperature = 20.0; // Комнатная температура
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        // Охлаждаем жидкость
        if (fluidTank.getFluidAmount() > 0) {
            temperature = Math.max(20.0, temperature - COOLING_RATE / 20.0);
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
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
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
        return super.getCapability(capability, facing);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("fluid")) {
            fluidTank.readFromNBT(compound.getCompoundTag("fluid"));
        }
        temperature = compound.getDouble("temperature");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("fluid", fluidTank.writeToNBT(new NBTTagCompound()));
        compound.setDouble("temperature", temperature);
        return compound;
    }
}
