package com.atomichorizons2026.chemistry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Interface for TileEntities that can process chemical reactions.
 * Provides a standard API for chemical processing machines.
 */
public interface IChemicalProcessor {
    
    /**
     * Gets the current processing progress (0 to max).
     */
    int getProgress();
    
    /**
     * Gets the maximum progress for the current recipe.
     */
    int getMaxProgress();
    
    /**
     * Gets the current temperature in Kelvin.
     */
    double getTemperature();
    
    /**
     * Gets the current recipe being processed.
     */
    @Nullable
    ChemicalRecipe getCurrentRecipe();
    
    /**
     * Checks if the processor can start a new recipe.
     */
    boolean canProcess();
    
    /**
     * Starts processing a recipe.
     * @param recipe Recipe to process
     * @return True if started successfully
     */
    boolean startProcessing(ChemicalRecipe recipe);
    
    /**
     * Updates the processing state.
     * Called every tick.
     */
    void updateProcessing();
    
    /**
     * Completes the current recipe and produces outputs.
     */
    void completeProcessing();
    
    /**
     * Cancels the current processing.
     */
    void cancelProcessing();
    
    /**
     * Gets the item inputs for recipe matching.
     */
    List<ItemStack> getItemInputs();
    
    /**
     * Gets the fluid inputs for recipe matching.
     */
    List<FluidStack> getFluidInputs();
    
    /**
     * Checks if the processor has space for outputs.
     */
    boolean hasSpaceForOutputs(ChemicalRecipe recipe);
    
    /**
     * Gets the machine type for recipe lookup.
     */
    String getMachineType();
}
