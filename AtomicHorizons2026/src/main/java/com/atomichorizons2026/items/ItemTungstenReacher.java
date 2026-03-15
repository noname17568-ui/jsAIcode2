package com.atomichorizons2026.items;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Tungsten Reacher - щипцы для безопасного обращения с радиоактивными предметами.
 * Снижает радиацию от предметов в инвентаре на sqrt(исходная).
 * Например: 50 RAD/t → ~7 RAD/t
 */
public class ItemTungstenReacher extends ItemBase {
    
    public ItemTungstenReacher() {
        super("tungsten_reacher");
        setMaxStackSize(1);
        setMaxDamage(256);
        setCreativeTab(CreativeTabs.TOOLS);
    }
    
    /**
     * Получает коэффициент снижения радиации.
     * Чем больше урон инструмента, тем хуже эффект.
     */
    public double getRadiationReductionFactor(ItemStack stack) {
        // При полной прочности: sqrt(0.5) ≈ 0.707 (снижение на 29%)
        // При половинной прочности: sqrt(0.75) ≈ 0.866 (снижение на 13%)
        double durabilityFactor = 1.0 - ((double) stack.getItemDamage() / stack.getMaxDamage());
        return Math.sqrt(durabilityFactor);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        double factor = getRadiationReductionFactor(stack);
        double reduction = (1.0 - factor) * 100;
        
        tooltip.add(TextFormatting.AQUA + I18n.format("tungsten_reacher.reduction", 
            String.format("%.1f%%", reduction)));
        
        tooltip.add(TextFormatting.GRAY + I18n.format("tungsten_reacher.desc"));
        
        if (stack.getItemDamage() > stack.getMaxDamage() * 0.8) {
            tooltip.add(TextFormatting.RED + I18n.format("tungsten_reacher.worn"));
        }
    }
}
