package com.atomichorizons2026.tileentities;

import com.atomichorizons2026.chemistry.ChemicalRecipe;
import com.atomichorizons2026.chemistry.ChemicalRecipeRegistry;
import com.atomichorizons2026.chemistry.IChemicalProcessor;
import com.atomichorizons2026.heat.HeatCapability;
import com.atomichorizons2026.heat.IHeatHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * TileEntity for Chemical Dissolution Chamber.
 * Dissolves raw materials in acids to create chemical solutions.
 */
public class TileEntityChemicalDissolutionChamber extends TileEntity implements ITickable, IChemicalProcessor {
    
    private static final int INPUT_SLOTS = 2;
    private static final int OUTPUT_SLOTS = 1;
    private static final int ENERGY_CAPACITY = 100000; // 100k RF
    private static final int ENERGY_PER_TICK = 100; // RF/t
    
    // Inventory
    private final ItemStackHandler inventory = new ItemStackHandler(INPUT_SLOTS + OUTPUT_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };
    
    // Energy storage
    private final com.atomichorizons2026.energy.EnergyStorage energy = 
        new com.atomichorizons2026.energy.EnergyStorage(ENERGY_CAPACITY);
    
    // Heat storage (from MR2 heat system)
    private final com.atomichorizons2026.heat.HeatStorage heat = 
        new com.atomichorizons2026.heat.HeatStorage(5000.0, 50.0, 1000.0); // 5kJ/K capacity, 50 W/m·K conductivity, 1000K max
    
    // Processing state
    private ChemicalRecipe currentRecipe;
    private int progress;
    private int maxProgress;
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        // Update processing
        updateProcessing();
        
        // Apply ambient cooling
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
        // Try to find a recipe if we don't have one
        if (currentRecipe == null) {
            List<ChemicalRecipe> recipes = ChemicalRecipeRegistry.getInstance()
                .getRecipesForMachine("chemical_dissolution_chamber");
            
            for (ChemicalRecipe recipe : recipes) {
                if (recipe.matches(getItemInputs(), getFluidInputs())) {
                    startProcessing(recipe);
                    break;
                }
            }
        }
        
        // Process current recipe
        if (currentRecipe != null && canProcess()) {
            progress++;
            energy.extractEnergy(ENERGY_PER_TICK, false);
            
            // Add heat from energy consumption
            heat.addHeat(ENERGY_PER_TICK * 0.1, false); // 10% of energy becomes heat
            
            if (progress >= maxProgress) {
                completeProcessing();
            }
        } else if (currentRecipe != null && !canProcess()) {
            // Can't process - reset
            cancelProcessing();
        }
    }
    
    @Override
    public void completeProcessing() {
        if (currentRecipe == null) return;
        
        // Consume inputs
        for (ItemStack input : currentRecipe.getItemInputs()) {
            // TODO: Consume from inventory
        }
        
        // Produce outputs
        for (FluidStack output : currentRecipe.getFluidOutputs()) {
            // TODO: Add to fluid tank
        }
        
        // Reset
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
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                inputs.add(stack);
            }
        }
        return inputs;
    }
    
    @Override
    public List<FluidStack> getFluidInputs() {
        // TODO: Implement fluid tank
        return new ArrayList<>();
    }
    
    @Override
    public boolean hasSpaceForOutputs(ChemicalRecipe recipe) {
        // TODO: Check output slots and fluid tanks
        return true;
    }
    
    @Override
    public String getMachineType() {
        return "chemical_dissolution_chamber";
    }
    
    // NBT
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
    
    // Capabilities
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
