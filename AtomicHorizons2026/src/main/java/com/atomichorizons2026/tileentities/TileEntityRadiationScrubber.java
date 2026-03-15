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
 * TileEntity для Radiation Scrubber.
 * Очищает чанк от радиации, потребляя энергию.
 */
public class TileEntityRadiationScrubber extends TileEntity implements ITickable {
    
    private static final int MAX_ENERGY = 100_000; // 100k RF
    private static final int ENERGY_PER_TICK = 50; // 50 RF/t
    private static final double CLEANUP_RATE = 0.1; // 10% за тик
    
    private EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, 1000, 0, 0);
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        // Проверяем энергию
        if (energyStorage.getEnergyStored() >= ENERGY_PER_TICK) {
            // Очищаем чанк
            ChunkPos chunkPos = new ChunkPos(pos);
            double currentRadiation = RadiationManager.getInstance().getChunkRadiation(world, chunkPos);
            
            if (currentRadiation > 0) {
                double reduction = currentRadiation * CLEANUP_RATE;
                RadiationManager.getInstance().setChunkRadiation(world, chunkPos, currentRadiation - reduction);
                
                // Потребляем энергию
                energyStorage.extractEnergy(ENERGY_PER_TICK, false);
                markDirty();
            }
        }
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
