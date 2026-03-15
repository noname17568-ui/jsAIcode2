package com.atomichorizons2026.chemistry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Recipe for chemical processing machines.
 * Simpler than ChemicalReaction - used for machine recipes.
 */
public class ChemicalRecipe {
    
    private final String id;
    private final List<ItemStack> itemInputs;
    private final List<FluidStack> fluidInputs;
    private final List<ItemStack> itemOutputs;
    private final List<FluidStack> fluidOutputs;
    
    private final int processingTime; // Ticks
    private final int energyCost; // RF per tick
    private final double minTemperature; // Kelvin (0 = no requirement)
    private final double maxTemperature; // Kelvin (0 = no limit)
    
    public ChemicalRecipe(String id, 
                         List<ItemStack> itemInputs,
                         List<FluidStack> fluidInputs,
                         List<ItemStack> itemOutputs,
                         List<FluidStack> fluidOutputs,
                         int processingTime,
                         int energyCost,
                         double minTemperature,
                         double maxTemperature) {
        this.id = id;
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.itemOutputs = itemOutputs;
        this.fluidOutputs = fluidOutputs;
        this.processingTime = processingTime;
        this.energyCost = energyCost;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
    }
    
    public String getId() {
        return id;
    }
    
    public List<ItemStack> getItemInputs() {
        return new ArrayList<>(itemInputs);
    }
    
    public List<FluidStack> getFluidInputs() {
        return new ArrayList<>(fluidInputs);
    }
    
    public List<ItemStack> getItemOutputs() {
        return new ArrayList<>(itemOutputs);
    }
    
    public List<FluidStack> getFluidOutputs() {
        return new ArrayList<>(fluidOutputs);
    }
    
    public int getProcessingTime() {
        return processingTime;
    }
    
    public int getEnergyCost() {
        return energyCost;
    }
    
    public double getMinTemperature() {
        return minTemperature;
    }
    
    public double getMaxTemperature() {
        return maxTemperature;
    }
    
    /**
     * Checks if the recipe can be processed at the given temperature.
     */
    public boolean isTemperatureValid(double temperature) {
        if (minTemperature > 0 && temperature < minTemperature) return false;
        if (maxTemperature > 0 && temperature > maxTemperature) return false;
        return true;
    }
    
    /**
     * Checks if the provided inputs match this recipe.
     */
    public boolean matches(List<ItemStack> items, List<FluidStack> fluids) {
        // Check item inputs
        for (ItemStack required : itemInputs) {
            boolean found = false;
            for (ItemStack provided : items) {
                if (ItemStack.areItemsEqual(required, provided) && 
                    provided.getCount() >= required.getCount()) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        
        // Check fluid inputs
        for (FluidStack required : fluidInputs) {
            boolean found = false;
            for (FluidStack provided : fluids) {
                if (provided != null && provided.containsFluid(required)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        
        return true;
    }
}
