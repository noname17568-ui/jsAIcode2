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
 * Chemical Reactor TileEntity.
 * Объединяет две жидкости в одну.
 * Потребление: 100 RF/t
 */
public class TileEntityChemicalReactor extends TileEntity implements ITickable {
    
    private static final int FLUID_CAPACITY = 10000; // 10000 mB
    private static final int MAX_ENERGY = 100_000; // 100k RF
    private static final int ENERGY_PER_TICK = 100; // 100 RF/t
    
    private FluidTank inputTank1 = new FluidTank(FLUID_CAPACITY);
    private FluidTank inputTank2 = new FluidTank(FLUID_CAPACITY);
    private FluidTank outputTank = new FluidTank(FLUID_CAPACITY);
    private EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, 256, 0, 0);
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        // Проверяем энергию и жидкости
        if (energyStorage.getEnergyStored() >= ENERGY_PER_TICK && 
            inputTank1.getFluidAmount() > 0 && inputTank2.getFluidAmount() > 0 &&
            outputTank.getFluidAmount() < outputTank.getCapacity()) {
            
            // Объединяем жидкости: берём тип жидкости из первого танка для вывода
            FluidStack fluid1 = inputTank1.drain(10, false);
            FluidStack fluid2 = inputTank2.drain(10, false);
            if (fluid1 != null && fluid2 != null) {
                FluidStack output = new FluidStack(fluid1.getFluid(), fluid1.amount + fluid2.amount);
                int filled = outputTank.fill(output, false);
                if (filled > 0) {
                    inputTank1.drain(10, true);
                    inputTank2.drain(10, true);
                    outputTank.fill(output, true);
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
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(inputTank1);
            } else if (facing == net.minecraft.util.EnumFacing.SOUTH) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(inputTank2);
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
        if (compound.hasKey("input1")) inputTank1.readFromNBT(compound.getCompoundTag("input1"));
        if (compound.hasKey("input2")) inputTank2.readFromNBT(compound.getCompoundTag("input2"));
        if (compound.hasKey("output")) outputTank.readFromNBT(compound.getCompoundTag("output"));
        if (compound.hasKey("energy")) CapabilityEnergy.ENERGY.readNBT(energyStorage, null, compound.getTag("energy"));
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("input1", inputTank1.writeToNBT(new NBTTagCompound()));
        compound.setTag("input2", inputTank2.writeToNBT(new NBTTagCompound()));
        compound.setTag("output", outputTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("energy", CapabilityEnergy.ENERGY.writeNBT(energyStorage, null));
        return compound;
    }
}
