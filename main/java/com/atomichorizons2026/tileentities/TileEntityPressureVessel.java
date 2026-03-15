package com.atomichorizons2026.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

/**
 * Pressure Vessel TileEntity.
 * Хранит газы и жидкости под давлением.
 * Максимальное давление: 100 атм
 */
public class TileEntityPressureVessel extends TileEntity {
    
    private static final int FLUID_CAPACITY = 50_000; // 50000 mB
    private static final int MAX_PRESSURE = 100; // 100 атм
    
    private FluidTank fluidTank = new FluidTank(FLUID_CAPACITY);
    private int currentPressure = 0;
    
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
    
    public int getPressure() {
        return currentPressure;
    }
    
    public void setPressure(int pressure) {
        this.currentPressure = Math.min(pressure, MAX_PRESSURE);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("fluid")) fluidTank.readFromNBT(compound.getCompoundTag("fluid"));
        if (compound.hasKey("pressure")) currentPressure = compound.getInteger("pressure");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("fluid", fluidTank.writeToNBT(new NBTTagCompound()));
        compound.setInteger("pressure", currentPressure);
        return compound;
    }
}
