package com.atomichorizons2026.radiation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Интерфейс для предметов, обеспечивающих защиту от радиации.
 * Реализуется броней и аксессуарами.
 */
public interface IRadiationProtection {
    
    /**
     * Получает коэффициент защиты от радиации.
     * @param stack Стек предмета
     * @param player Игрок
     * @return Значение от 0.0 (нет защиты) до 1.0 (полная защита)
     */
    double getRadiationProtection(ItemStack stack, EntityPlayer player);
    
    /**
     * Проверяет, активна ли защита (для экипировки с энергией)
     * @param stack Стек предмета
     * @param player Игрок
     * @return true если защита активна
     */
    default boolean isProtectionActive(ItemStack stack, EntityPlayer player) {
        return true;
    }
    
    /**
     * Вызывается при получении радиации игроком
     * @param stack Стек предмета
     * @param player Игрок
     * @param amount Количество радиации до применения защиты
     * @return Модифицированное количество радиации
     */
    default double onRadiationReceived(ItemStack stack, EntityPlayer player, double amount) {
        if (!isProtectionActive(stack, player)) {
            return amount;
        }
        double protection = getRadiationProtection(stack, player);
        return amount * (1.0 - protection);
    }
    
    /**
     * Получает тип защиты (для отображения)
     */
    default ProtectionType getProtectionType() {
        return ProtectionType.GENERAL;
    }
    
    /**
     * Типы защиты от радиации
     */
    enum ProtectionType {
        GENERAL("general"),         // Общая защита
        HAZMAT("hazmat"),           // Химзащита
        LEAD_LINED("lead_lined"),   // Свинцовая защита
        POWERED("powered"),         // Энергозависимая защита
        MAGICAL("magical");         // Магическая защита
        
        private final String name;
        
        ProtectionType(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
}
