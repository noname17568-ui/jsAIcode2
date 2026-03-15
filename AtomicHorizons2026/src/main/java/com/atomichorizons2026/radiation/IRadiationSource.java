package com.atomichorizons2026.radiation;

import net.minecraft.item.ItemStack;

/**
 * Интерфейс для объектов, излучающих радиацию.
 * Реализуется блоками, предметами, сущностями.
 */
public interface IRadiationSource {
    
    /**
     * Получает уровень радиации в RAD/tick для предмета
     */
    default double getRadiationLevel() {
        return 0.0;
    }
    
    /**
     * Получает уровень радиации в RAD/tick для стека предметов
     */
    default double getRadiationPerTick(ItemStack stack) {
        return getRadiationLevel();
    }
    
    /**
     * Получает тип радиации (ALPHA, BETA, GAMMA, NEUTRON)
     */
    default RadiationType getRadiationType() {
        return RadiationType.GAMMA;
    }
    
    /**
     * Получает тип радиации для стека предметов
     */
    default RadiationType getRadiationType(ItemStack stack) {
        return getRadiationType();
    }
    
    /**
     * Получает радиус распространения в блоках
     */
    default double getRadiationRadius() {
        return 5.0;
    }
    
    /**
     * Получает радиус эффекта для стека предметов
     */
    default double getRadiusOfEffect(ItemStack stack) {
        return getRadiationRadius();
    }
    
    /**
     * Проверяет, активен ли источник радиации
     */
    default boolean isRadioactive() {
        return true;
    }
    
    /**
     * Типы радиации с разной проникающей способностью
     */
    enum RadiationType {
        ALPHA(0.05, "alpha"),      // 5 см воздуха, останавливается бумагой/кожей
        BETA(0.3, "beta"),         // 1-2 метра, останавливается алюминием
        GAMMA(1.0, "gamma"),       // Десятки метров, требует свинец/бетон
        NEUTRON(1.5, "neutron");   // Очень глубоко, требует воду/парафин/бор
        
        private final double penetrationFactor;
        private final String name;
        
        RadiationType(double penetrationFactor, String name) {
            this.penetrationFactor = penetrationFactor;
            this.name = name;
        }
        
        public double getPenetrationFactor() {
            return penetrationFactor;
        }
        
        public String getName() {
            return name;
        }
    }
}
