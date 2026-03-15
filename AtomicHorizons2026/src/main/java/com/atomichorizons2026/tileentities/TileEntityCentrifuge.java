package com.atomichorizons2026.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import com.atomichorizons2026.util.FluidTank;

import javax.annotation.Nullable;

/**
 * Centrifuge TileEntity.
 * Отделяет частицы от жидкости.
 * Потребление: 120 RF/t
 */
public class TileEntityCentrifuge extends TileEntity implements ITickable {
    
    private static final int FLUID_CAPACITY = 10000; // 10000 mB
    private static final int MAX_ENERGY = 100_000; // 100k RF
    private static final int ENERGY_PER_TICK = 120; // 120 RF/t
    
    private FluidTank inputTank = new FluidTank(FLUID_CAPACITY);
    private FluidTank outputTank1 = new FluidTank(FLUID_CAPACITY);
    private FluidTank outputTank2 = new FluidTank(FLUID_CAPACITY);
    private EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, 256, 0, 0);
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        // Проверяем энергию и жидкость
        if (energyStorage.getEnergyStored() >= ENERGY_PER_TICK && 
            inputTank.getFluidAmount() > 0 &&
            outputTank1.getFluidAmount() < outputTank1.getCapacity() &&
            outputTank2.getFluidAmount() < outputTank2.getCapacity()) {
            
            // Отделяем жидкость: берём тип из inputTank, делим поровну между двумя выходами
            FluidStack drained = inputTank.drain(20, false);
            if (drained != null) {
                FluidStack half = new FluidStack(drained.getFluid(), drained.amount / 2);
                int filled1 = outputTank1.fill(half, false);
                int filled2 = outputTank2.fill(half, false);
                if (filled1 > 0 && filled2 > 0) {
                    inputTank.drain(20, true);
                    outputTank1.fill(half, true);
                    outputTank2.fill(half, true);
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
            } else if (facing == net.minecraft.util.EnumFacing.SOUTH) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(outputTank1);
            } else {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(outputTank2);
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
        if (compound.hasKey("output1")) outputTank1.readFromNBT(compound.getCompoundTag("output1"));
        if (compound.hasKey("output2")) outputTank2.readFromNBT(compound.getCompoundTag("output2"));
        if (compound.hasKey("energy")) CapabilityEnergy.ENERGY.readNBT(energyStorage, null, compound.getTag("energy"));
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("input", inputTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("output1", outputTank1.writeToNBT(new NBTTagCompound()));
        compound.setTag("output2", outputTank2.writeToNBT(new NBTTagCompound()));
        compound.setTag("energy", CapabilityEnergy.ENERGY.writeNBT(energyStorage, null));
        return compound;
    }
}
