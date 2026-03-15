package com.atomichorizons2026.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import com.atomichorizons2026.util.FluidTank;

import javax.annotation.Nullable;

/**
 * Heat Exchanger TileEntity.
 * Передает тепло между двумя жидкостями.
 * Охлаждает горячую жидкость и нагревает холодную.
 */
public class TileEntityHeatExchanger extends TileEntity implements ITickable {
    
    private static final int FLUID_CAPACITY = 10000; // 10000 mB
    private static final double TRANSFER_RATE = 10.0; // 10 температуры в секунду
    
    private FluidTank hotFluidTank = new FluidTank(FLUID_CAPACITY);
    private FluidTank coldFluidTank = new FluidTank(FLUID_CAPACITY);
    private double hotTemperature = 100.0;
    private double coldTemperature = 20.0;
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        // Передаем тепло
        if (hotFluidTank.getFluidAmount() > 0 && coldFluidTank.getFluidAmount() > 0) {
            double tempDiff = hotTemperature - coldTemperature;
            if (tempDiff > 0) {
                double transfer = Math.min(TRANSFER_RATE / 20.0, tempDiff / 2.0);
                hotTemperature -= transfer;
                coldTemperature += transfer;
                markDirty();
            }
        }
    }
    
    public double getHotTemperature() {
        return hotTemperature;
    }
    
    public double getColdTemperature() {
        return coldTemperature;
    }
    
    public void setHotTemperature(double temp) {
        this.hotTemperature = temp;
    }
    
    public void setColdTemperature(double temp) {
        this.coldTemperature = temp;
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
            // Возвращаем разные танки в зависимости от стороны
            if (facing == net.minecraft.util.EnumFacing.NORTH || facing == net.minecraft.util.EnumFacing.SOUTH) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(hotFluidTank);
            } else {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(coldFluidTank);
            }
        }
        return super.getCapability(capability, facing);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("hotFluid")) {
            hotFluidTank.readFromNBT(compound.getCompoundTag("hotFluid"));
        }
        if (compound.hasKey("coldFluid")) {
            coldFluidTank.readFromNBT(compound.getCompoundTag("coldFluid"));
        }
        hotTemperature = compound.getDouble("hotTemperature");
        coldTemperature = compound.getDouble("coldTemperature");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("hotFluid", hotFluidTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("coldFluid", coldFluidTank.writeToNBT(new NBTTagCompound()));
        compound.setDouble("hotTemperature", hotTemperature);
        compound.setDouble("coldTemperature", coldTemperature);
        return compound;
    }
}
