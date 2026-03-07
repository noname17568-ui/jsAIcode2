package com.atomichorizons2026.fluids;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Базовый класс для жидкостей мода.
 * Поддерживает различные типы жидкостей: кислоты, теплоносители, топливо.
 */
public class FluidBase extends Fluid {
    
    protected final FluidType fluidType;
    protected final int color;
    
    public enum FluidType {
        ACID,           // Кислота (агрессивная, токсичная)
        COOLANT,        // Теплоноситель
        FUEL,           // Ядерное топливо
        WASTE,          // Радиоактивные отходы
        GAS,            // Газ
        LIQUID_METAL    // Жидкий металл
    }
    
    /**
     * @param name Имя жидкости
     * @param fluidType Тип жидкости
     * @param color Цвет в формате ARGB
     * @param temperature Температура в Кельвинах
     * @param viscosity Вязкость (вода = 1000)
     * @param density Плотность (вода = 1000)
     */
    public FluidBase(String name, FluidType fluidType, int color, int temperature, int viscosity, int density) {
        super(name, 
              new ResourceLocation(AtomicHorizons2026.MODID, "blocks/fluids/" + name + "_still"),
              new ResourceLocation(AtomicHorizons2026.MODID, "blocks/fluids/" + name + "_flowing"),
              color);
        
        this.fluidType = fluidType;
        this.color = color;
        
        setTemperature(temperature);
        setViscosity(viscosity);
        setDensity(density);
        
        // Настройки в зависимости от типа
        switch (fluidType) {
            case ACID:
                setGaseous(false);
                break;
            case COOLANT:
                setGaseous(false);
                break;
            case GAS:
                setGaseous(true);
                setDensity(-density); // Газы имеют отрицательную плотность
                break;
            case LIQUID_METAL:
                setGaseous(false);
                setLuminosity(10); // Жидкие металлы светятся
                break;
            case FUEL:
            case WASTE:
                setGaseous(false);
                setLuminosity(5); // Слабое свечение
                break;
        }
        
        // Регистрация жидкости
        FluidRegistry.registerFluid(this);
        FluidRegistry.addBucketForFluid(this);
    }
    
    public FluidType getFluidType() {
        return fluidType;
    }
    
    public boolean isAcid() {
        return fluidType == FluidType.ACID;
    }
    
    public boolean isRadioactive() {
        return fluidType == FluidType.FUEL || fluidType == FluidType.WASTE;
    }
    
    public boolean isGas() {
        return fluidType == FluidType.GAS;
    }
    
    /**
     * Получает уровень радиации жидкости (для радиоактивных)
     * @return Уровень радиации в RAD/tick при контакте
     */
    public double getRadiationLevel() {
        if (fluidType == FluidType.FUEL) return 5.0;
        if (fluidType == FluidType.WASTE) return 20.0;
        return 0.0;
    }
    
    /**
     * Получает урон от кислоты
     * @return Урон в сердцах при контакте
     */
    public float getAcidDamage() {
        if (fluidType == FluidType.ACID) return 4.0f; // 2 сердца
        return 0.0f;
    }
}
