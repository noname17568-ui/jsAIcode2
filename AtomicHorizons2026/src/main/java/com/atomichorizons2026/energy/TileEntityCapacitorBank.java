package com.atomichorizons2026.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

/**
 * Base TileEntity for capacitor banks / energy storage blocks.
 * Per TZ Section 3.4: Basic (1M RF), Advanced (10M RF), Industrial (100M RF)
 */
public class TileEntityCapacitorBank extends TileEntity implements ITickable {

    private EnergyStorage storage;
    private VoltageTier tier;

    public TileEntityCapacitorBank() {
        this(VoltageTier.LV, 1000000);
    }

    public TileEntityCapacitorBank(VoltageTier tier, int capacity) {
        this.tier = tier;
        this.storage = new EnergyStorage(capacity, tier.getMaxRFPerTick(), tier.getMaxRFPerTick());
    }

    @Override
    public void update() {
        if (world.isRemote) return;

        // Push energy to adjacent machines
        if (storage.getEnergyStored() > 0) {
            for (EnumFacing facing : EnumFacing.values()) {
                TileEntity neighbor = world.getTileEntity(pos.offset(facing));
                if (neighbor != null && neighbor.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                    net.minecraftforge.energy.IEnergyStorage neighborStorage = neighbor.getCapability(
                            CapabilityEnergy.ENERGY, facing.getOpposite());
                    if (neighborStorage != null && neighborStorage.canReceive()) {
                        int toSend = storage.extractEnergy(tier.getMaxRFPerTick() / 6, false);
                        int accepted = neighborStorage.receiveEnergy(toSend, false);
                        if (accepted < toSend) {
                            storage.receiveEnergy(toSend - accepted, false);
                        }
                    }
                }
            }
        }
    }

    public EnergyStorage getStorage() { return storage; }
    public VoltageTier getTier() { return tier; }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(storage);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("Tier")) {
            this.tier = VoltageTier.values()[compound.getInteger("Tier")];
        }
        storage.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Tier", tier.ordinal());
        storage.writeToNBT(compound);
        return compound;
    }
}
