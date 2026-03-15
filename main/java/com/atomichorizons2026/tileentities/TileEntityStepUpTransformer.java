package com.atomichorizons2026.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nullable;

/**
 * Step-Up Transformer TileEntity.
 * Входит: 256 RF/t, Выходит: 1024 RF/t (1:4 ratio)
 */
public class TileEntityStepUpTransformer extends TileEntity implements ITickable {
    
    private static final int INPUT_CAPACITY = 2500; // Входной буфер
    private static final int OUTPUT_CAPACITY = 10000; // Выходной буфер
    private static final int CONVERSION_RATIO = 4; // 1:4 (256 → 1024)
    
    private EnergyStorage inputStorage = new EnergyStorage(INPUT_CAPACITY, 256, 0, 0);
    private EnergyStorage outputStorage = new EnergyStorage(OUTPUT_CAPACITY, 0, 1024, 0);
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        // Преобразуем энергию
        int inputEnergy = inputStorage.getEnergyStored();
        int outputSpace = outputStorage.getMaxEnergyStored() - outputStorage.getEnergyStored();
        
        if (inputEnergy > 0 && outputSpace > 0) {
            int toConvert = Math.min(inputEnergy, outputSpace / CONVERSION_RATIO);
            
            inputStorage.extractEnergy(toConvert, false);
            outputStorage.receiveEnergy(toConvert * CONVERSION_RATIO, false);
            
            markDirty();
        }
        
        // Отправляем энергию соседям
        for (EnumFacing facing : EnumFacing.VALUES) {
            TileEntity neighbor = world.getTileEntity(pos.offset(facing));
            if (neighbor != null && neighbor.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                int sent = neighbor.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite())
                    .receiveEnergy(outputStorage.extractEnergy(1024, false), false);
                outputStorage.extractEnergy(sent, true);
            }
        }
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return facing == EnumFacing.DOWN || facing == EnumFacing.NORTH; // Вход снизу/сзади
        }
        return super.hasCapability(capability, facing);
    }
    
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            if (facing == EnumFacing.DOWN || facing == EnumFacing.NORTH) {
                return CapabilityEnergy.ENERGY.cast(inputStorage);
            } else {
                return CapabilityEnergy.ENERGY.cast(outputStorage);
            }
        }
        return super.getCapability(capability, facing);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("input")) {
            CapabilityEnergy.ENERGY.readNBT(inputStorage, null, compound.getTag("input"));
        }
        if (compound.hasKey("output")) {
            CapabilityEnergy.ENERGY.readNBT(outputStorage, null, compound.getTag("output"));
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("input", CapabilityEnergy.ENERGY.writeNBT(inputStorage, null));
        compound.setTag("output", CapabilityEnergy.ENERGY.writeNBT(outputStorage, null));
        return compound;
    }
}
