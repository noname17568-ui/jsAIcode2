package com.atomichorizons2026.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import com.atomichorizons2026.util.FluidTank;

import javax.annotation.Nullable;

/**
 * Fluid Tank TileEntity.
 * Хранит до 100000 mB жидкости.
 */
public class TileEntityFluidTank extends TileEntity {
    
    private static final int FLUID_CAPACITY = 100_000; // 100000 mB
    
    private FluidTank fluidTank = new FluidTank(FLUID_CAPACITY);
    
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
        if (compound.hasKey("fluid")) fluidTank.readFromNBT(compound.getCompoundTag("fluid"));
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("fluid", fluidTank.writeToNBT(new NBTTagCompound()));
        return compound;
    }
}
