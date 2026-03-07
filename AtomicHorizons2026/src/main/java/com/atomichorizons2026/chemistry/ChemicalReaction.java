package com.atomichorizons2026.chemistry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Химическая реакция с энтальпией и катализаторами.
 * Как в Mekanism, но с более реалистичной физикой.
 */
public class ChemicalReaction {
    
    private final String id;
    private final String name;
    
    // Входы
    private final List<ItemStack> itemInputs;
    private final List<FluidStack> fluidInputs;
    private final List<GasInput> gasInputs;
    
    // Выходы
    private final List<ItemStack> itemOutputs;
    private final List<FluidStack> fluidOutputs;
    private final List<GasOutput> gasOutputs;
    
    // Энергетика
    private final double enthalpy; // Дж/моль (отрицательная = экзотермическая)
    private final double activationEnergy; // Энергия активации
    private final double baseRate; // Базовая скорость (моль/тик)
    
    // Катализатор
    private final Catalyst catalyst;
    private final double catalystEfficiency;
    
    // Условия
    private final double minTemperature; // Минимальная температура в Кельвинах
    private final double maxTemperature; // Максимальная температура
    private final double minPressure; // Минимальное давление в Па
    
    private ChemicalReaction(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.itemInputs = builder.itemInputs;
        this.fluidInputs = builder.fluidInputs;
        this.gasInputs = builder.gasInputs;
        this.itemOutputs = builder.itemOutputs;
        this.fluidOutputs = builder.fluidOutputs;
        this.gasOutputs = builder.gasOutputs;
        this.enthalpy = builder.enthalpy;
        this.activationEnergy = builder.activationEnergy;
        this.baseRate = builder.baseRate;
        this.catalyst = builder.catalyst;
        this.catalystEfficiency = builder.catalystEfficiency;
        this.minTemperature = builder.minTemperature;
        this.maxTemperature = builder.maxTemperature;
        this.minPressure = builder.minPressure;
    }
    
    // ==================== ГЕТТЕРЫ ====================
    
    public String getId() { return id; }
    public String getName() { return name; }
    public List<ItemStack> getItemInputs() { return itemInputs; }
    public List<FluidStack> getFluidInputs() { return fluidInputs; }
    public List<ItemStack> getItemOutputs() { return itemOutputs; }
    public List<FluidStack> getFluidOutputs() { return fluidOutputs; }
    public double getEnthalpy() { return enthalpy; }
    public double getActivationEnergy() { return activationEnergy; }
    public double getBaseRate() { return baseRate; }
    public double getMinTemperature() { return minTemperature; }
    public double getMaxTemperature() { return maxTemperature; }
    public double getMinPressure() { return minPressure; }
    
    /**
     * Проверяет, является ли реакция экзотермической
     */
    public boolean isExothermic() {
        return enthalpy < 0;
    }
    
    /**
     * Проверяет, является ли реакция эндотермической
     */
    public boolean isEndothermic() {
        return enthalpy > 0;
    }
    
    /**
     * Рассчитывает скорость реакции при заданных условиях
     * Использует уравнение Аррениуса
     */
    public double calculateRate(double temperature, double pressure, @Nullable Catalyst presentCatalyst) {
        // Проверка условий
        if (temperature < minTemperature || temperature > maxTemperature) return 0;
        if (pressure < minPressure) return 0;
        
        // Константа Аррениуса: k = A * exp(-Ea / RT)
        double R = 8.314; // Дж/(моль·К)
        double rate = baseRate * Math.exp(-activationEnergy / (R * temperature));
        
        // Учет катализатора
        if (presentCatalyst != null && catalyst != null && presentCatalyst.equals(catalyst)) {
            rate *= catalystEfficiency;
        }
        
        // Учет давления (для газов)
        rate *= (pressure / 101325.0); // Нормируем к атмосферному
        
        return rate;
    }
    
    /**
     * Рассчитывает тепловой эффект при заданном количестве
     */
    public double calculateHeat(double moles) {
        return enthalpy * moles;
    }
    
    // ==================== BUILDER ====================
    
    public static class Builder {
        private String id;
        private String name;
        private List<ItemStack> itemInputs = new ArrayList<>();
        private List<FluidStack> fluidInputs = new ArrayList<>();
        private List<GasInput> gasInputs = new ArrayList<>();
        private List<ItemStack> itemOutputs = new ArrayList<>();
        private List<FluidStack> fluidOutputs = new ArrayList<>();
        private List<GasOutput> gasOutputs = new ArrayList<>();
        private double enthalpy = 0;
        private double activationEnergy = 50000; // 50 кДж/моль по умолчанию
        private double baseRate = 0.001;
        private Catalyst catalyst = null;
        private double catalystEfficiency = 2.0;
        private double minTemperature = 273; // 0°C
        private double maxTemperature = 5000;
        private double minPressure = 0;
        
        public Builder(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public Builder addItemInput(ItemStack input) {
            itemInputs.add(input);
            return this;
        }
        
        public Builder addFluidInput(FluidStack input) {
            fluidInputs.add(input);
            return this;
        }
        
        public Builder addGasInput(String gasName, int amount) {
            gasInputs.add(new GasInput(gasName, amount));
            return this;
        }
        
        public Builder addItemOutput(ItemStack output) {
            itemOutputs.add(output);
            return this;
        }
        
        public Builder addFluidOutput(FluidStack output) {
            fluidOutputs.add(output);
            return this;
        }
        
        public Builder addGasOutput(String gasName, int amount) {
            gasOutputs.add(new GasOutput(gasName, amount));
            return this;
        }
        
        public Builder setEnthalpy(double enthalpy) {
            this.enthalpy = enthalpy;
            return this;
        }
        
        public Builder setActivationEnergy(double energy) {
            this.activationEnergy = energy;
            return this;
        }
        
        public Builder setBaseRate(double rate) {
            this.baseRate = rate;
            return this;
        }
        
        public Builder setCatalyst(Catalyst catalyst, double efficiency) {
            this.catalyst = catalyst;
            this.catalystEfficiency = efficiency;
            return this;
        }
        
        public Builder setTemperatureRange(double min, double max) {
            this.minTemperature = min;
            this.maxTemperature = max;
            return this;
        }
        
        public Builder setMinPressure(double pressure) {
            this.minPressure = pressure;
            return this;
        }
        
        public ChemicalReaction build() {
            return new ChemicalReaction(this);
        }
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ====================
    
    public static class GasInput {
        public final String gasName;
        public final int amount;
        
        public GasInput(String gasName, int amount) {
            this.gasName = gasName;
            this.amount = amount;
        }
    }
    
    public static class GasOutput {
        public final String gasName;
        public final int amount;
        
        public GasOutput(String gasName, int amount) {
            this.gasName = gasName;
            this.amount = amount;
        }
    }
    
    /**
     * Катализатор для ускорения реакции
     */
    public static class Catalyst {
        private final String name;
        private final double efficiencyBonus;
        
        public Catalyst(String name, double efficiencyBonus) {
            this.name = name;
            this.efficiencyBonus = efficiencyBonus;
        }
        
        public String getName() { return name; }
        public double getEfficiencyBonus() { return efficiencyBonus; }
    }
}
