package com.atomichorizons2026.items;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Lead-Lined Toolbelt - поясная сумка со свинцовой подкладкой.
 * Снижает радиацию от предметов в инвентаре на 50%.
 * Носится в слоте ног (как броня).
 */
public class ItemLeadLinedToolbelt extends ItemArmor {
    
    public static final ArmorMaterial TOOLBELT_MATERIAL = EnumHelper.addArmorMaterial(
        "TOOLBELT",
        AtomicHorizons2026.MODID + ":toolbelt",
        5,
        new int[]{0, 0, 1, 0},
        15,
        net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
        0.0f
    );
    
    public static final double RADIATION_REDUCTION = 0.5; // 50%
    
    public ItemLeadLinedToolbelt() {
        super(TOOLBELT_MATERIAL, 0, EntityEquipmentSlot.LEGS);
        
        setTranslationKey("lead_lined_toolbelt");
        setRegistryName(AtomicHorizons2026.MODID, "lead_lined_toolbelt");
        setCreativeTab(AtomicHorizons2026.CREATIVE_TAB);
        setMaxStackSize(1);
    }
    
    /**
     * Проверяет, надет ли пояс
     */
    public static boolean isWorn(EntityPlayer player) {
        ItemStack legs = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        return legs.getItem() instanceof ItemLeadLinedToolbelt;
    }
    
    /**
     * Получает коэффициент снижения радиации
     */
    public static double getRadiationReduction(ItemStack stack) {
        if (stack.getItemDamage() >= stack.getMaxDamage()) {
            return 0.0; // Сломан
        }
        return RADIATION_REDUCTION;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        tooltip.add(TextFormatting.AQUA + I18n.format("toolbelt.radiation_reduction", 
            (int)(RADIATION_REDUCTION * 100)));
        
        tooltip.add(TextFormatting.GRAY + I18n.format("toolbelt.desc"));
        
        if (stack.getItemDamage() > stack.getMaxDamage() * 0.8) {
            tooltip.add(TextFormatting.RED + I18n.format("toolbelt.worn"));
        }
    }
}
