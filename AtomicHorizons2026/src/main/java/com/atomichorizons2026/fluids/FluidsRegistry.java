package com.atomichorizons2026.fluids;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;

/**
 * Реестр всех жидкостей мода.
 * Содержит кислоты, теплоносители и ядерные материалы.
 */
public class FluidsRegistry {
    
    // ==================== КИСЛОТЫ ====================
    public static FluidBase SULFURIC_ACID;      // H2SO4 - Серная кислота
    public static FluidBase HYDROFLUORIC_ACID;  // HF - Плавиковая кислота
    public static FluidBase NITRIC_ACID;        // HNO3 - Азотная кислота
    
    // ==================== ТЕПЛОНОСИТЕЛИ ====================
    public static FluidBase LIQUID_SODIUM;          // Жидкий натрий
    public static FluidBase LIQUID_LEAD_BISMUTH;    // Жидкий свинцово-висмутовый сплав
    public static FluidBase MOLTEN_SALT;            // Расплавленная соль (для MSR)
    
    // ==================== ЯДЕРНЫЕ МАТЕРИАЛЫ ====================
    public static FluidBase URANIUM_HEXAFLUORIDE;   // UF6 - Гексафторид урана
    public static FluidBase MOLTEN_URANIUM;         // Жидкий уран
    public static FluidBase MOLTEN_THORIUM;         // Жидкий торий
    public static FluidBase NUCLEAR_WASTE;          // Жидкие радиоактивные отходы
    
    // ==================== БЛОКИ ЖИДКОСТЕЙ ====================
    public static BlockFluidClassic BLOCK_SULFURIC_ACID;
    public static BlockFluidClassic BLOCK_HYDROFLUORIC_ACID;
    public static BlockFluidClassic BLOCK_NITRIC_ACID;
    public static BlockFluidClassic BLOCK_LIQUID_SODIUM;
    public static BlockFluidClassic BLOCK_LIQUID_LEAD_BISMUTH;
    public static BlockFluidClassic BLOCK_MOLTEN_SALT;
    public static BlockFluidClassic BLOCK_URANIUM_HEXAFLUORIDE;
    public static BlockFluidClassic BLOCK_MOLTEN_URANIUM;
    public static BlockFluidClassic BLOCK_MOLTEN_THORIUM;
    public static BlockFluidClassic BLOCK_NUCLEAR_WASTE;
    
    /**
     * Инициализация всех жидкостей
     */
    public static void init() {
        // ==================== КИСЛОТЫ ====================
        // Серная кислота H2SO4 - используется в обработке урана
        SULFURIC_ACID = new FluidBase(
            "sulfuric_acid",
            FluidBase.FluidType.ACID,
            0xFFCCCC00,     // Желтоватый цвет
            298,            // Комнатная температура
            1500,           // Более вязкая чем вода
            1840            // Плотность выше воды
        );
        BLOCK_SULFURIC_ACID = new BlockFluidClassic(SULFURIC_ACID);
        
        // Плавиковая кислота HF - очень опасная, используется для обогащения
        HYDROFLUORIC_ACID = new FluidBase(
            "hydrofluoric_acid",
            FluidBase.FluidType.ACID,
            0xFF88DDFF,     // Светло-голубой
            293,
            800,
            990
        );
        BLOCK_HYDROFLUORIC_ACID = new BlockFluidClassic(HYDROFLUORIC_ACID);
        
        // Азотная кислота HNO3 - используется в обработке
        NITRIC_ACID = new FluidBase(
            "nitric_acid",
            FluidBase.FluidType.ACID,
            0xFFFFDDDD,     // Бледно-красноватый
            293,
            1200,
            1510
        );
        BLOCK_NITRIC_ACID = new BlockFluidClassic(NITRIC_ACID);
        
        // ==================== ТЕПЛОНОСИТЕЛИ ====================
        // Жидкий натрий - для быстрых реакторов
        LIQUID_SODIUM = new FluidBase(
            "liquid_sodium",
            FluidBase.FluidType.LIQUID_METAL,
            0xFFFFDD88,     // Золотистый
            371,            // 98°C - точка плавления
            700,
            927
        );
        BLOCK_LIQUID_SODIUM = new BlockFluidClassic(LIQUID_SODIUM);
        
        // Свинцово-висмутовый сплав - для свинцовых реакторов
        LIQUID_LEAD_BISMUTH = new FluidBase(
            "liquid_lead_bismuth",
            FluidBase.FluidType.LIQUID_METAL,
            0xFF666688,     // Серебристо-серый
            398,            // 125°C
            1500,
            10500           // Очень плотный
        );
        BLOCK_LIQUID_LEAD_BISMUTH = new BlockFluidClassic(LIQUID_LEAD_BISMUTH);
        
        // Расплавленная соль - для MSR
        MOLTEN_SALT = new FluidBase(
            "molten_salt",
            FluidBase.FluidType.COOLANT,
            0xFFFFE4B5,     // Теплый бежевый
            773,            // 500°C
            2000,
            1900
        );
        BLOCK_MOLTEN_SALT = new BlockFluidClassic(MOLTEN_SALT);
        
        // ==================== ЯДЕРНЫЕ МАТЕРИАЛЫ ====================
        // Гексафторид урана - для газовых центрифуг
        URANIUM_HEXAFLUORIDE = new FluidBase(
            "uranium_hexafluoride",
            FluidBase.FluidType.GAS,
            0x88FFFFFF,     // Полупрозрачный белый
            337,            // Сублимирует при комнатной температуре
            100,
            5000
        );
        BLOCK_URANIUM_HEXAFLUORIDE = new BlockFluidClassic(URANIUM_HEXAFLUORIDE);
        
        // Жидкий уран - для металлического топлива
        MOLTEN_URANIUM = new FluidBase(
            "molten_uranium",
            FluidBase.FluidType.FUEL,
            0xFFFFAA00,     // Ярко-оранжевый
            1405,           // 1132°C
            5000,
            17300
        );
        BLOCK_MOLTEN_URANIUM = new BlockFluidClassic(MOLTEN_URANIUM);
        
        // Жидкий торий
        MOLTEN_THORIUM = new FluidBase(
            "molten_thorium",
            FluidBase.FluidType.FUEL,
            0xFFFFFF88,     // Светло-желтый
            2023,           // 1750°C
            5000,
            11700
        );
        BLOCK_MOLTEN_THORIUM = new BlockFluidClassic(MOLTEN_THORIUM);
        
        // Ядерные отходы
        NUCLEAR_WASTE = new FluidBase(
            "nuclear_waste",
            FluidBase.FluidType.WASTE,
            0xFF44FF44,     // Зеленое свечение
            373,
            3000,
            2500
        );
        BLOCK_NUCLEAR_WASTE = new BlockFluidClassic(NUCLEAR_WASTE);
    }
    
    /**
     * Получает блок жидкости по соответствующей жидкости
     */
    public static Block getBlockForFluid(Fluid fluid) {
        if (fluid == SULFURIC_ACID) return BLOCK_SULFURIC_ACID;
        if (fluid == HYDROFLUORIC_ACID) return BLOCK_HYDROFLUORIC_ACID;
        if (fluid == NITRIC_ACID) return BLOCK_NITRIC_ACID;
        if (fluid == LIQUID_SODIUM) return BLOCK_LIQUID_SODIUM;
        if (fluid == LIQUID_LEAD_BISMUTH) return BLOCK_LIQUID_LEAD_BISMUTH;
        if (fluid == MOLTEN_SALT) return BLOCK_MOLTEN_SALT;
        if (fluid == URANIUM_HEXAFLUORIDE) return BLOCK_URANIUM_HEXAFLUORIDE;
        if (fluid == MOLTEN_URANIUM) return BLOCK_MOLTEN_URANIUM;
        if (fluid == MOLTEN_THORIUM) return BLOCK_MOLTEN_THORIUM;
        if (fluid == NUCLEAR_WASTE) return BLOCK_NUCLEAR_WASTE;
        return null;
    }
}
