package com.atomichorizons2026.chemistry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Реестр всех химических реакций.
 * Позволяет находить реакции по входным материалам.
 */
public class ChemistryRegistry {
    
    private static final ChemistryRegistry INSTANCE = new ChemistryRegistry();
    
    private final Map<String, ChemicalReaction> reactions = new HashMap<>();
    private final List<ChemicalReaction> reactionList = new ArrayList<>();
    
    private ChemistryRegistry() {}
    
    public static ChemistryRegistry getInstance() {
        return INSTANCE;
    }
    
    /**
     * Регистрирует реакцию
     */
    public void registerReaction(ChemicalReaction reaction) {
        reactions.put(reaction.getId(), reaction);
        reactionList.add(reaction);
    }
    
    /**
     * Получает реакцию по ID
     */
    @Nullable
    public ChemicalReaction getReaction(String id) {
        return reactions.get(id);
    }
    
    /**
     * Находит реакции по входным предметам
     */
    public List<ChemicalReaction> findReactionsByInputs(List<ItemStack> items, List<FluidStack> fluids) {
        List<ChemicalReaction> results = new ArrayList<>();
        
        for (ChemicalReaction reaction : reactionList) {
            if (matchesInputs(reaction, items, fluids)) {
                results.add(reaction);
            }
        }
        
        return results;
    }
    
    /**
     * Проверяет, соответствуют ли входы реакции
     */
    private boolean matchesInputs(ChemicalReaction reaction, List<ItemStack> items, List<FluidStack> fluids) {
        // Проверяем предметы
        for (ItemStack required : reaction.getItemInputs()) {
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
        
        // Проверяем жидкости
        for (FluidStack required : reaction.getFluidInputs()) {
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
    
    /**
     * Получает все зарегистрированные реакции
     */
    public List<ChemicalReaction> getAllReactions() {
        return new ArrayList<>(reactionList);
    }
    
    /**
     * Инициализация стандартных реакций
     */
    public void initDefaultReactions() {
        // Реакция 1: Урановая руда + H2SO4 = Урановый шлам
        // TODO: Добавить реальные предметы когда они будут созданы
        
        // Реакция 2: Железо + Кислород = Оксид железа (ржавчина)
        // registerReaction(new ChemicalReaction.Builder("iron_oxidation", "Iron Oxidation")
        //     .addItemInput(new ItemStack(net.minecraft.init.Items.IRON_INGOT))
        //     .addGasInput("oxygen", 100)
        //     .setEnthalpy(-824000) // Дж/моль
        //     .setActivationEnergy(75000)
        //     .build());
    }
}
