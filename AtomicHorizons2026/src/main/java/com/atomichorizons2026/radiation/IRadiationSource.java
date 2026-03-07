package com.atomichorizons2026.radiation;

/**
 * Интерфейс для объектов, излучающих радиацию.
 * Реализуется блоками, предметами, сущностями.
 */
public interface IRadiationSource {
    
    /**
     * Получает уровень радиации в RAD/tick
     */
    double getRadiationLevel();
    
    /**
     * Получает тип радиации (ALPHA, BETA, GAMMA, NEUTRON)
     */
    RadiationType getRadiationType();
    
    /**
     * Получает радиус распространения в блоках
     */
    double getRadiationRadius();
    
    /**
     * Проверяет, активен ли источник радиации
     */
    boolean isRadioactive();
    
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
