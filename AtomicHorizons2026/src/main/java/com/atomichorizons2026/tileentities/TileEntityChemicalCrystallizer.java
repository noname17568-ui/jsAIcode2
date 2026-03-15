package com.atomichorizons2026.tileentities;

import com.atomichorizons2026.chemistry.ChemicalRecipe;
import com.atomichorizons2026.chemistry.ChemicalRecipeRegistry;
import com.atomichorizons2026.chemistry.IChemicalProcessor;
import com.atomichorizons2026.heat.HeatCapability;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * TileEntity for Chemical Crystallizer.
 * Crystallizes dissolved materials from solutions through evaporation.
 */
public class TileEntityChemicalCrystallizer extends TileEntity implements ITickable, IChemicalProcessor {
    
    private static final int OUTPUT_SLOTS = 2;
    private static final int ENERGY_CAPACITY = 75000;
    private static final int ENERGY_PER_TICK = 75;
    
    private final ItemStackHandler inventory = new ItemStackHandler(OUTPUT_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };
    
    private final com.atomichorizons2026.energy.EnergyStorage energy = 
        new com.atomichorizons2026.energy.EnergyStorage(ENERGY_CAPACITY);
    
    private final com.atomichorizons2026.heat.HeatStorage heat = 
        new com.atomichorizons2026.heat.HeatStorage(4000.0, 45.0, 900.0);
    
    private ChemicalRecipe currentRecipe;
    private int progress;
    private int maxProgress;
    
    @Override
    public void update() {
        if (world.isRemote) return;
        updateProcessing();
        if (world.getTotalWorldTime() % 20 == 0) {
            com.atomichorizons2026.heat.HeatTransferManager.applyAmbientCooling(heat, 1.0);
        }
    }
    
    @Override
    public boolean canProcess() {
        if (currentRecipe == null) return false;
        if (!currentRecipe.isTemperatureValid(heat.getTemperature())) return false;
        if (energy.getEnergyStored() < ENERGY_PER_TICK) return false;
        return hasSpaceForOutputs(currentRecipe);
    }
    
    @Override
    public boolean startProcessing(ChemicalRecipe recipe) {
        if (recipe == null) return false;
        this.currentRecipe = recipe;
        this.progress = 0;
        this.maxProgress = recipe.getProcessingTime();
        return true;
    }
    
    @Override
    public void updateProcessing() {
        if (currentRecipe == null) {
            List<ChemicalRecipe> recipes = ChemicalRecipeRegistry.getInstance()
                .getRecipesForMachine("chemical_crystallizer");
            for (ChemicalRecipe recipe : recipes) {
                if (recipe.matches(getItemInputs(), getFluidInputs())) {
                    startProcessing(recipe);
                    break;
                }
            }
        }
        
        if (currentRecipe != null && canProcess()) {
            progress++;
            energy.extractEnergy(ENERGY_PER_TICK, false);
            heat.addHeat(ENERGY_PER_TICK * 0.15, false); // More heat for evaporation
            
            if (progress >= maxProgress) {
                completeProcessing();
            }
        } else if (currentRecipe != null && !canProcess()) {
            cancelProcessing();
        }
    }
    
    @Override
    public void completeProcessing() {
        if (currentRecipe == null) return;
        // TODO: Consume fluid inputs and produce crystal outputs
        currentRecipe = null;
        progress = 0;
        maxProgress = 0;
        markDirty();
    }
    
    @Override
    public void cancelProcessing() {
        currentRecipe = null;
        progress = 0;
        maxProgress = 0;
    }
    
    @Override
    public int getProgress() {
        return progress;
    }
    
    @Override
    public int getMaxProgress() {
        return maxProgress;
    }
    
    @Override
    public double getTemperature() {
        return heat.getTemperature();
    }
    
    @Nullable
    @Override
    public ChemicalRecipe getCurrentRecipe() {
        return currentRecipe;
    }
    
    @Override
    public List<ItemStack> getItemInputs() {
        return new ArrayList<>(); // Crystallizer only has fluid inputs
    }
    
    @Override
    public List<FluidStack> getFluidInputs() {
        return new ArrayList<>();
    }
    
    @Override
    public boolean hasSpaceForOutputs(ChemicalRecipe recipe) {
        return true;
    }
    
    @Override
    public String getMachineType() {
        return "chemical_crystallizer";
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inventory", inventory.serializeNBT());
        compound.setInteger("energy", energy.getEnergyStored());
        compound.setInteger("progress", progress);
        compound.setInteger("maxProgress", maxProgress);
        NBTTagCompound heatTag = new NBTTagCompound();
        heat.writeToNBT(heatTag);
        compound.setTag("heat", heatTag);
        return compound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        energy.setEnergy(compound.getInteger("energy"));
        progress = compound.getInteger("progress");
        maxProgress = compound.getInteger("maxProgress");
        if (compound.hasKey("heat")) {
            heat.readFromNBT(compound.getCompoundTag("heat"));
        }
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
        if (capability == CapabilityEnergy.ENERGY) return true;
        if (capability == HeatCapability.HEAT_HANDLER_CAPABILITY) return true;
        return super.hasCapability(capability, facing);
    }
    
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energy);
        }
        if (capability == HeatCapability.HEAT_HANDLER_CAPABILITY) {
            return HeatCapability.HEAT_HANDLER_CAPABILITY.cast(heat);
        }
        return super.getCapability(capability, facing);
    }
}
