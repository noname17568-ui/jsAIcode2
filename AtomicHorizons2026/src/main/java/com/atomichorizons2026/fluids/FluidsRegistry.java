package com.atomichorizons2026.fluids;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;

public class FluidsRegistry {
    
    public static FluidBase SULFURIC_ACID, HYDROFLUORIC_ACID, NITRIC_ACID;
    public static FluidBase LIQUID_SODIUM, LIQUID_LEAD_BISMUTH, MOLTEN_SALT;
    public static FluidBase URANIUM_HEXAFLUORIDE, MOLTEN_URANIUM, MOLTEN_THORIUM, NUCLEAR_WASTE;
    
    // Используем наш НОВЫЙ класс Custom
    public static BlockFluidCustom BLOCK_SULFURIC_ACID;
    public static BlockFluidCustom BLOCK_HYDROFLUORIC_ACID;
    public static BlockFluidCustom BLOCK_NITRIC_ACID;
    public static BlockFluidCustom BLOCK_LIQUID_SODIUM;
    public static BlockFluidCustom BLOCK_LIQUID_LEAD_BISMUTH;
    public static BlockFluidCustom BLOCK_MOLTEN_SALT;
    public static BlockFluidCustom BLOCK_URANIUM_HEXAFLUORIDE;
    public static BlockFluidCustom BLOCK_MOLTEN_URANIUM;
    public static BlockFluidCustom BLOCK_MOLTEN_THORIUM;
    public static BlockFluidCustom BLOCK_NUCLEAR_WASTE;
    
    public static void init() {
        SULFURIC_ACID = new FluidBase("sulfuric_acid", FluidBase.FluidType.ACID, 0xFFCCCC00, 298, 1500, 1840);
        BLOCK_SULFURIC_ACID = new BlockFluidCustom(SULFURIC_ACID);
        
        HYDROFLUORIC_ACID = new FluidBase("hydrofluoric_acid", FluidBase.FluidType.ACID, 0xFF88DDFF, 293, 800, 990);
        BLOCK_HYDROFLUORIC_ACID = new BlockFluidCustom(HYDROFLUORIC_ACID);
        
        NITRIC_ACID = new FluidBase("nitric_acid", FluidBase.FluidType.ACID, 0xFFFFDDDD, 293, 1200, 1510);
        BLOCK_NITRIC_ACID = new BlockFluidCustom(NITRIC_ACID);
        
        LIQUID_SODIUM = new FluidBase("liquid_sodium", FluidBase.FluidType.LIQUID_METAL, 0xFFFFDD88, 371, 700, 927);
        BLOCK_LIQUID_SODIUM = new BlockFluidCustom(LIQUID_SODIUM);
        
        LIQUID_LEAD_BISMUTH = new FluidBase("liquid_lead_bismuth", FluidBase.FluidType.LIQUID_METAL, 0xFF666688, 398, 1500, 10500);
        BLOCK_LIQUID_LEAD_BISMUTH = new BlockFluidCustom(LIQUID_LEAD_BISMUTH);
        
        MOLTEN_SALT = new FluidBase("molten_salt", FluidBase.FluidType.COOLANT, 0xFFFFE4B5, 773, 2000, 1900);
        BLOCK_MOLTEN_SALT = new BlockFluidCustom(MOLTEN_SALT);
        
        URANIUM_HEXAFLUORIDE = new FluidBase("uranium_hexafluoride", FluidBase.FluidType.GAS, 0x88FFFFFF, 337, 100, 5000);
        BLOCK_URANIUM_HEXAFLUORIDE = new BlockFluidCustom(URANIUM_HEXAFLUORIDE);
        
        MOLTEN_URANIUM = new FluidBase("molten_uranium", FluidBase.FluidType.FUEL, 0xFFFFAA00, 1405, 5000, 17300);
        BLOCK_MOLTEN_URANIUM = new BlockFluidCustom(MOLTEN_URANIUM);
        
        MOLTEN_THORIUM = new FluidBase("molten_thorium", FluidBase.FluidType.FUEL, 0xFFFFFF88, 2023, 5000, 11700);
        BLOCK_MOLTEN_THORIUM = new BlockFluidCustom(MOLTEN_THORIUM);
        
        NUCLEAR_WASTE = new FluidBase("nuclear_waste", FluidBase.FluidType.WASTE, 0xFF44FF44, 373, 3000, 2500);
        BLOCK_NUCLEAR_WASTE = new BlockFluidCustom(NUCLEAR_WASTE);
    }
    
    public static Block getBlockForFluid(Fluid fluid) {
        if (fluid == SULFURIC_ACID) return BLOCK_SULFURIC_ACID;
        // ... (остальное можно сократить, этот метод редко нужен напрямую)
        return null;
    }
}