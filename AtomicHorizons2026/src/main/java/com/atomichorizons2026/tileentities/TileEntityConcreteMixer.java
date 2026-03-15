package com.atomichorizons2026.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

/**
 * Concrete Mixer TileEntity.
 * Миксер для смешивания бетона.
 * Требует энергию и жидкости.
 */
public class TileEntityConcreteMixer extends TileEntity implements ITickable {
    
    private static final int MAX_ENERGY = 100_000; // 100k RF
    private static final int ENERGY_PER_TICK = 20; // 20 RF/t
    private static final int FLUID_CAPACITY = 4000; // 4000 mB
    
    private EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, 256, 0, 0);
    private FluidTank fluidTank = new FluidTank(FLUID_CAPACITY);
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        // Проверяем энергию и жидкость
        if (energyStorage.getEnergyStored() >= ENERGY_PER_TICK && fluidTank.getFluidAmount() > 0) {
            energyStorage.extractEnergy(ENERGY_PER_TICK, false);
            fluidTank.drain(10, true); // Каждый тик расходуем 10 mB
            markDirty();
        }
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }
    
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidTank);
        }
        return super.getCapability(capability, facing);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("energy")) {
            CapabilityEnergy.ENERGY.readNBT(energyStorage, null, compound.getTag("energy"));
        }
        if (compound.hasKey("fluid")) {
            fluidTank.readFromNBT(compound.getCompoundTag("fluid"));
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("energy", CapabilityEnergy.ENERGY.writeNBT(energyStorage, null));
        compound.setTag("fluid", fluidTank.writeToNBT(new NBTTagCompound()));
        return compound;
    }
}
