package com.atomichorizons2026.items;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.radiation.IRadiationSource;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Радиоактивный изотоп - базовый класс для всех изотопов.
 * Каждый изотоп имеет свой уровень радиации и тип.
 */
public class ItemRadioactiveIsotope extends ItemBase implements IRadiationSource {
    
    public enum IsotopeType {
        // Альфа-излучатели
        RADIUM_226("radium_226", RadiationType.ALPHA, 50.0, 1600),
        POLONIUM_210("polonium_210", RadiationType.ALPHA, 100.0, 138),
        AMERICIUM_241("americium_241", RadiationType.ALPHA, 80.0, 432),
        
        // Бета-излучатели
        COBALT_60("cobalt_60", RadiationType.BETA, 120.0, 5L),
        CESIUM_137("cesium_137", RadiationType.BETA, 90.0, 30L),
        IODINE_131("iodine_131", RadiationType.BETA, 110.0, 8L),
        
        // Гамма-излучатели
        PLUTONIUM_239("plutonium_239", RadiationType.GAMMA, 150.0, 24110),
        URANIUM_235("uranium_235", RadiationType.GAMMA, 70.0, 703800000),
        THORIUM_232("thorium_232", RadiationType.GAMMA, 40.0, 14050000000L);
        
        private final String name;
        private final RadiationType type;
        private final double radiationPerTick; // RAD/t
        private final long halfLife; // в годах
        
        IsotopeType(String name, RadiationType type, double radiationPerTick, long halfLife) {
            this.name = name;
            this.type = type;
            this.radiationPerTick = radiationPerTick;
            this.halfLife = halfLife;
        }
        
        public String getName() {
            return name;
        }
        
        public RadiationType getType() {
            return type;
        }
        
        public double getRadiationPerTick() {
            return radiationPerTick;
        }
        
        public long getHalfLife() {
            return halfLife;
        }
    }
    
    private final IsotopeType isotope;
    
    public ItemRadioactiveIsotope(IsotopeType isotope) {
        super(isotope.getName());
        this.isotope = isotope;
        setMaxStackSize(64);
        // Creative tab already set in ItemBase constructor
    }
    
    @Override
    public boolean isRadioactive() {
        return true;
    }
    
    @Override
    public double getRadiationPerTick(ItemStack stack) {
        return isotope.getRadiationPerTick();
    }
    
    @Override
    public RadiationType getRadiationType(ItemStack stack) {
        return isotope.getType();
    }
    
    @Override
    public double getRadiusOfEffect(ItemStack stack) {
        // Радиус эффекта зависит от типа излучения
        switch (isotope.getType()) {
            case ALPHA:
                return 2.0; // Альфа - короткий диапазон
            case BETA:
                return 5.0; // Бета - средний диапазон
            case GAMMA:
                return 15.0; // Гамма - длинный диапазон
            default:
                return 5.0;
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        tooltip.add(TextFormatting.DARK_RED + I18n.format("isotope.radiation_type") + ": " + 
            isotope.getType().name());
        
        tooltip.add(TextFormatting.RED + I18n.format("isotope.radiation_per_tick", 
            String.format("%.1f", isotope.getRadiationPerTick())));
        
        tooltip.add(TextFormatting.GRAY + I18n.format("isotope.half_life", isotope.getHalfLife()));
        
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_GRAY + I18n.format("isotope.warning"));
    }
    
    public IsotopeType getIsotope() {
        return isotope;
    }
}
