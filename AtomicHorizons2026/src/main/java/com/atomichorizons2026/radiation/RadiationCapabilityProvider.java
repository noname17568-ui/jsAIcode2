package com.atomichorizons2026.radiation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Capability provider for attaching radiation capability to players.
 * This is attached to EntityPlayer via AttachCapabilitiesEvent.
 */
public class RadiationCapabilityProvider implements ICapabilitySerializable<NBTTagCompound> {
    
    private final PlayerRadiationCapability instance = new PlayerRadiationCapability();
    
    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == PlayerRadiationCapability.RADIATION_CAPABILITY;
    }
    
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == PlayerRadiationCapability.RADIATION_CAPABILITY) {
            return PlayerRadiationCapability.RADIATION_CAPABILITY.cast(instance);
        }
        return null;
    }
    
    @Override
    public NBTTagCompound serializeNBT() {
        return instance.serializeNBT();
    }
    
    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        instance.deserializeNBT(nbt);
    }
}
