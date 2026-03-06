package com.atomichorizons2026.tileentities;

import com.atomichorizons2026.handlers.RegistryHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileEntitySMRCore extends TileEntity implements ITickable {
    
    // Constants
    public static final int MAX_ENERGY = 1000000; // 1M RF
    public static final int MAX_EXTRACT = 10000;  // 10k RF/t
    public static final int GENERATION_RATE = 5000; // 5k RF/t when active
    public static final int FUEL_BURN_TIME = 720000; // 10 hours of real time (20 ticks/sec * 3600 sec/hour * 10 hours)
    
    // Energy storage
    private EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, 0, MAX_EXTRACT);
    
    // Fuel inventory (1 slot for fuel rod)
    private ItemStackHandler fuelHandler = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() == RegistryHandler.FUEL_ROD_HALEU;
        }
    };
    
    // State
    private int burnTime = 0;
    private boolean isActive = false;
    private float temperature = 20.0f; // Celsius
    private static final float MAX_TEMPERATURE = 350.0f; // SMR passive safety limit
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        // Check if we have fuel
        ItemStack fuel = fuelHandler.getStackInSlot(0);
        
        if (burnTime > 0) {
            // Generate energy
            int generated = energyStorage.receiveEnergy(GENERATION_RATE, false);
            burnTime--;
            
            // Increase temperature
            if (temperature < 300.0f) {
                temperature += 0.1f;
            }
            
            isActive = true;
            
            // If fuel is depleted, convert to spent fuel
            if (burnTime <= 0 && !fuel.isEmpty()) {
                fuelHandler.setStackInSlot(0, new ItemStack(RegistryHandler.SPENT_FUEL_ROD));
            }
        } else if (!fuel.isEmpty() && fuel.getItem() == RegistryHandler.FUEL_ROD_HALEU && energyStorage.getEnergyStored() < MAX_ENERGY) {
            // Start burning new fuel
            burnTime = FUEL_BURN_TIME;
            fuel.shrink(1);
        } else {
            // Cool down
            if (temperature > 20.0f) {
                temperature -= 0.05f;
            }
            isActive = false;
        }
        
        // Passive safety: if temperature exceeds limit, SCRAM (emergency shutdown)
        if (temperature >= MAX_TEMPERATURE) {
            burnTime = 0;
            isActive = false;
            temperature = MAX_TEMPERATURE;
        }
        
        // Push energy to adjacent blocks
        pushEnergy();
        
        markDirty();
    }
    
    private void pushEnergy() {
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity te = world.getTileEntity(pos.offset(facing));
            if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                IEnergyStorage receiver = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                if (receiver != null && receiver.canReceive()) {
                    int extractable = energyStorage.extractEnergy(MAX_EXTRACT, true);
                    int received = receiver.receiveEnergy(extractable, false);
                    energyStorage.extractEnergy(received, false);
                }
            }
        }
    }
    
    // Getters for GUI
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }
    
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }
    
    public float getTemperature() {
        return temperature;
    }
    
    public int getBurnTime() {
        return burnTime;
    }
    
    public int getMaxBurnTime() {
        return FUEL_BURN_TIME;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public ItemStackHandler getFuelHandler() {
        return fuelHandler;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        energyStorage = new EnergyStorage(MAX_ENERGY, 0, MAX_EXTRACT);
        if (compound.hasKey("energy")) {
            CapabilityEnergy.ENERGY.readNBT(energyStorage, null, compound.getTag("energy"));
        }
        fuelHandler.deserializeNBT(compound.getCompoundTag("fuel"));
        burnTime = compound.getInteger("burnTime");
        temperature = compound.getFloat("temperature");
        isActive = compound.getBoolean("isActive");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("energy", CapabilityEnergy.ENERGY.writeNBT(energyStorage, null));
        compound.setTag("fuel", fuelHandler.serializeNBT());
        compound.setInteger("burnTime", burnTime);
        compound.setFloat("temperature", temperature);
        compound.setBoolean("isActive", isActive);
        return compound;
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || 
               capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
               super.hasCapability(capability, facing);
    }
    
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(fuelHandler);
        }
        return super.getCapability(capability, facing);
    }
}
