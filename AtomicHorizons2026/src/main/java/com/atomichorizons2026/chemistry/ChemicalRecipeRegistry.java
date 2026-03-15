package com.atomichorizons2026.chemistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for chemical processing recipes.
 * Manages recipes for all chemical processing machines.
 */
public class ChemicalRecipeRegistry {
    
    private static final ChemicalRecipeRegistry INSTANCE = new ChemicalRecipeRegistry();
    
    private final Map<String, ChemicalRecipe> recipes = new HashMap<>();
    private final Map<String, List<ChemicalRecipe>> recipesByMachine = new HashMap<>();
    
    private ChemicalRecipeRegistry() {}
    
    public static ChemicalRecipeRegistry getInstance() {
        return INSTANCE;
    }
    
    /**
     * Registers a recipe for a specific machine type.
     * @param machineType Machine type (e.g., "chemical_reactor", "distillery")
     * @param recipe Recipe to register
     */
    public void registerRecipe(String machineType, ChemicalRecipe recipe) {
        recipes.put(recipe.getId(), recipe);
        recipesByMachine.computeIfAbsent(machineType, k -> new ArrayList<>()).add(recipe);
    }
    
    /**
     * Gets a recipe by ID.
     */
    @Nullable
    public ChemicalRecipe getRecipe(String id) {
        return recipes.get(id);
    }
    
    /**
     * Gets all recipes for a specific machine type.
     */
    public List<ChemicalRecipe> getRecipesForMachine(String machineType) {
        return new ArrayList<>(recipesByMachine.getOrDefault(machineType, new ArrayList<>()));
    }
    
    /**
     * Gets all registered recipes.
     */
    public List<ChemicalRecipe> getAllRecipes() {
        return new ArrayList<>(recipes.values());
    }
    
    /**
     * Initializes default recipes.
     * Called during mod initialization.
     */
    public void initDefaultRecipes() {
        // TODO: Add default recipes when items/fluids are ready
        // Example recipes will be added in future MRs
    }
}
