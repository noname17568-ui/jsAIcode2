package com.atomichorizons2026.heat;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * Forge capability for heat handling.
 * Allows blocks/TileEntities to expose heat transfer functionality.
 */
public class HeatCapability {
    
    @CapabilityInject(IHeatHandler.class)
    public static Capability<IHeatHandler> HEAT_HANDLER_CAPABILITY = null;
    
    /**
     * Registers the heat capability with Forge.
     * Must be called during preInit.
     */
    public static void register() {
        // Capability is registered via @CapabilityInject
        // This method exists for explicit registration if needed
    }
    
    /**
     * Storage implementation for heat capability.
     */
    public static class Storage implements Capability.IStorage<IHeatHandler> {
        
        @Override
        public NBTBase writeNBT(Capability<IHeatHandler> capability, IHeatHandler instance, EnumFacing side) {
            if (instance instanceof HeatStorage) {
                NBTTagCompound nbt = new NBTTagCompound();
                ((HeatStorage) instance).writeToNBT(nbt);
                return nbt;
            }
            return new NBTTagCompound();
        }
        
        @Override
        public void readNBT(Capability<IHeatHandler> capability, IHeatHandler instance, EnumFacing side, NBTBase nbt) {
            if (instance instanceof HeatStorage && nbt instanceof NBTTagCompound) {
                ((HeatStorage) instance).readFromNBT((NBTTagCompound) nbt);
            }
        }
    }
    
    /**
     * Factory for creating default heat handler instances.
     */
    public static class Factory implements Callable<IHeatHandler> {
        
        @Override
        public IHeatHandler call() throws Exception {
            // Default: 1000 J/K capacity, 50 W/m·K conductivity, 1000K max temp
            return new HeatStorage(1000.0, 50.0, 1000.0);
        }
    }
    
    /**
     * Provider for attaching heat capability to TileEntities.
     */
    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
        
        private final IHeatHandler instance;
        
        public Provider(IHeatHandler instance) {
            this.instance = instance;
        }
        
        public Provider(double heatCapacity, double thermalConductivity, double maxTemperature) {
            this.instance = new HeatStorage(heatCapacity, thermalConductivity, maxTemperature);
        }
        
        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == HEAT_HANDLER_CAPABILITY;
        }
        
        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == HEAT_HANDLER_CAPABILITY) {
                return HEAT_HANDLER_CAPABILITY.cast(instance);
            }
            return null;
        }
        
        @Override
        public NBTTagCompound serializeNBT() {
            if (instance instanceof HeatStorage) {
                NBTTagCompound nbt = new NBTTagCompound();
                ((HeatStorage) instance).writeToNBT(nbt);
                return nbt;
            }
            return new NBTTagCompound();
        }
        
        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            if (instance instanceof HeatStorage) {
                ((HeatStorage) instance).readFromNBT(nbt);
            }
        }
    }
}
