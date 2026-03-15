package com.atomichorizons2026.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

/**
 * Fusion Reactor TileEntity.
 * Thermonuclear Fusion Reactor - Термоядерный реактор синтеза.
 * Мощность: 2000 MWe
 * Потребление топлива: 1 fuel rod / 5000 тиков
 * Преимущества: Чистая энергия, минимум отходов
 */
public class TileEntityFusionReactor extends TileEntity implements ITickable {
    
    private static final int MAX_ENERGY = 20_000_000; // 20M RF
    private static final int ENERGY_PER_TICK = 200; // 200 RF/t
    private static final int FUEL_CONSUMPTION_RATE = 5000; // 1 fuel rod per 5000 ticks
    
    private FluidTank coolantTank = new FluidTank(100000); // 100000 mB coolant
    private EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, 0, 512, 0);
    private int fuelLevel = 0; // 0-100
    private int tickCounter = 0;
    private int plasmaDensity = 0; // 0-100 (требуется >50 для работы)
    private boolean isActive = false;
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        // Проверяем топливо, охлаждение и плазму
        if (fuelLevel > 0 && coolantTank.getFluidAmount() > 3000 && plasmaDensity > 50) {
            isActive = true;
            
            // Генерируем энергию
            energyStorage.receiveEnergy(ENERGY_PER_TICK, false);
            
            // Потребляем топливо
            tickCounter++;
            if (tickCounter >= FUEL_CONSUMPTION_RATE) {
                fuelLevel = Math.max(0, fuelLevel - 1);
                tickCounter = 0;
            }
            
            // Потребляем охлаждение
            coolantTank.drain(10, true);
            
            // Поддерживаем плазму
            plasmaDensity = Math.min(100, plasmaDensity + 2);
            
            markDirty();
        } else {
            isActive = false;
            // Плазма остывает
            plasmaDensity = Math.max(0, plasmaDensity - 1);
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
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(coolantTank);
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }
        return super.getCapability(capability, facing);
    }
    
    public int getFuelLevel() {
        return fuelLevel;
    }
    
    public void setFuelLevel(int level) {
        this.fuelLevel = Math.min(100, level);
    }
    
    public int getPlasmaDensity() {
        return plasmaDensity;
    }
    
    public void setPlasmaDensity(int density) {
        this.plasmaDensity = Math.min(100, density);
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("coolant")) coolantTank.readFromNBT(compound.getCompoundTag("coolant"));
        if (compound.hasKey("energy")) CapabilityEnergy.ENERGY.readNBT(energyStorage, null, compound.getTag("energy"));
        if (compound.hasKey("fuel")) fuelLevel = compound.getInteger("fuel");
        if (compound.hasKey("ticks")) tickCounter = compound.getInteger("ticks");
        if (compound.hasKey("plasma")) plasmaDensity = compound.getInteger("plasma");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("coolant", coolantTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("energy", CapabilityEnergy.ENERGY.writeNBT(energyStorage, null));
        compound.setInteger("fuel", fuelLevel);
        compound.setInteger("ticks", tickCounter);
        compound.setInteger("plasma", plasmaDensity);
        return compound;
    }
}
