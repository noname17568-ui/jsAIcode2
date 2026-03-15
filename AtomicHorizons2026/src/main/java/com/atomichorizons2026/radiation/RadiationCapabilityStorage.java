package com.atomichorizons2026.radiation;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

/**
 * Storage implementation for PlayerRadiationCapability.
 * Handles NBT serialization/deserialization for the capability.
 */
public class RadiationCapabilityStorage implements IStorage<PlayerRadiationCapability> {
    
    @Override
    public NBTBase writeNBT(Capability<PlayerRadiationCapability> capability, PlayerRadiationCapability instance, EnumFacing side) {
        return instance.serializeNBT();
    }
    
    @Override
    public void readNBT(Capability<PlayerRadiationCapability> capability, PlayerRadiationCapability instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            instance.deserializeNBT((NBTTagCompound) nbt);
        }
    }
}
