package com.atomichorizons2026.energy;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Energy storage implementation for Atomic Horizons machines.
 * Uses RF (Redstone Flux) as the energy unit.
 * Per TZ Section 3.1: Voltage tiers LV/MV/HV/EV/UV
 */
public class EnergyStorage implements net.minecraftforge.energy.IEnergyStorage {

    private int energy;
    private int capacity;
    private int maxReceive;
    private int maxExtract;

    public EnergyStorage(int capacity) {
        this(capacity, capacity, capacity);
    }

    public EnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            energy += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            energy -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() { return energy; }

    @Override
    public int getMaxEnergyStored() { return capacity; }

    @Override
    public boolean canExtract() { return maxExtract > 0; }

    @Override
    public boolean canReceive() { return maxReceive > 0; }

    public boolean hasEnergy(int amount) {
        return energy >= amount;
    }

    public void consumeEnergy(int amount) {
        energy = Math.max(0, energy - amount);
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    public int getMaxReceive() { return maxReceive; }
    public int getMaxExtract() { return maxExtract; }

    public float getEnergyPercent() {
        return capacity > 0 ? (float) energy / capacity : 0;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.energy = nbt.getInteger("Energy");
        if (nbt.hasKey("Capacity")) {
            this.capacity = nbt.getInteger("Capacity");
            this.maxReceive = nbt.getInteger("MaxReceive");
            this.maxExtract = nbt.getInteger("MaxExtract");
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("Energy", energy);
        nbt.setInteger("Capacity", capacity);
        nbt.setInteger("MaxReceive", maxReceive);
        nbt.setInteger("MaxExtract", maxExtract);
    }
}
