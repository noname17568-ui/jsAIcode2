package com.atomichorizons2026.items;

import com.atomichorizons2026.AtomicHorizons2026;
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
 * RTG (Radioisotope Thermoelectric Generator) - радиоизотопный генератор.
 * Преобразует радиацию в энергию (RF).
 * Разные типы RTG дают разное количество энергии.
 */
public class ItemRTG extends ItemBase {
    
    public enum RTGType {
        PLUTONIUM_238("rtg_plutonium_238", 500, 88), // 500 RF/t, 88 лет
        STRONTIUM_90("rtg_strontium_90", 300, 28.8), // 300 RF/t, 28.8 лет
        AMERICIUM_241("rtg_americium_241", 400, 432), // 400 RF/t, 432 года
        POLONIUM_210("rtg_polonium_210", 600, 0.38); // 600 RF/t, 0.38 года (очень нестабильный)
        
        private final String name;
        private final int energyPerTick; // RF/t
        private final double lifespan; // в годах
        
        RTGType(String name, int energyPerTick, double lifespan) {
            this.name = name;
            this.energyPerTick = energyPerTick;
            this.lifespan = lifespan;
        }
        
        public String getName() {
            return name;
        }
        
        public int getEnergyPerTick() {
            return energyPerTick;
        }
        
        public double getLifespan() {
            return lifespan;
        }
    }
    
    private final RTGType type;
    
    public ItemRTG(RTGType type) {
        super(type.getName());
        this.type = type;
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.MATERIALS);
    }
    
    public RTGType getType() {
        return type;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        tooltip.add(TextFormatting.AQUA + I18n.format("rtg.energy_per_tick", type.getEnergyPerTick()));
        tooltip.add(TextFormatting.GRAY + I18n.format("rtg.lifespan", 
            String.format("%.1f", type.getLifespan())));
        
        tooltip.add("");
        tooltip.add(TextFormatting.YELLOW + I18n.format("rtg.desc"));
    }
}
