package com.atomichorizons2026.tileentities;

import com.atomichorizons2026.radiation.RadiationManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nullable;

/**
 * TileEntity для Scintillation Detector.
 * Требует энергию для работы и определяет тип радиации.
 */
public class TileEntityScintillationDetector extends TileEntity implements ITickable {
    
    private static final int MAX_ENERGY = 100_000; // 100k RF
    private static final int ENERGY_PER_TICK = 10; // 10 RF/t
    
    private EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, 1000, 0, 0);
    private int tickCounter = 0;
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        tickCounter++;
        
        // Проверяем энергию каждые 20 тиков
        if (tickCounter % 20 == 0) {
            if (energyStorage.getEnergyStored() >= ENERGY_PER_TICK) {
                energyStorage.extractEnergy(ENERGY_PER_TICK, false);
                markDirty();
            }
        }
    }
    
    /**
     * Определяет тип радиации на основе уровня
     */
    public String detectRadiationType() {
        ChunkPos chunkPos = new ChunkPos(pos);
        double radiation = RadiationManager.getInstance().getChunkRadiation(world, chunkPos);
        
        if (radiation == 0) {
            return "None";
        } else if (radiation < 100) {
            return "Alpha";
        } else if (radiation < 500) {
            return "Beta";
        } else {
            return "Gamma";
        }
    }
    
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }
    
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }
    
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }
        return super.getCapability(capability, facing);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("energy")) {
            CapabilityEnergy.ENERGY.readNBT(energyStorage, null, compound.getTag("energy"));
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("energy", CapabilityEnergy.ENERGY.writeNBT(energyStorage, null));
        return compound;
    }
}
