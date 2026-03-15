package com.atomichorizons2026.items.armor;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.radiation.IRadiationProtection;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Atomic Exo-Suit - Эндгейм броня с встроенным реактором.
 * Обеспечивает 99% защиты от радиации.
 * Особенности: полет, силовой кулак, бесконечная энергия.
 */
public class ItemAtomicExoSuit extends ItemArmor implements IRadiationProtection {
    
    public static final ArmorMaterial EXOSUIT_MATERIAL = EnumHelper.addArmorMaterial(
        "EXOSUIT",
        AtomicHorizons2026.MODID + ":exosuit",
        50,
        new int[]{4, 5, 4, 4},
        20,
        SoundEvents.ITEM_ARMOR_EQUIP_IRON,
        2.0f
    );
    
    private static final double[] RADIATION_PROTECTION = {0.2475, 0.2475, 0.2475, 0.2475}; // 24.75% каждая = 99% полный
    
    public ItemAtomicExoSuit(EntityEquipmentSlot slot) {
        super(EXOSUIT_MATERIAL, 0, slot);
        
        String name = "atomic_exo_" + getArmorPartName(slot);
        setTranslationKey(name);
        setRegistryName(AtomicHorizons2026.MODID, name);
        setCreativeTab(CreativeTabs.COMBAT);
    }
    
    @Override
    public double getRadiationProtection(ItemStack stack, EntityPlayer player) {
        return RADIATION_PROTECTION[this.armorType.getIndex()];
    }
    
    @Override
    public ProtectionType getProtectionType() {
        return ProtectionType.EXOSUIT;
    }
    
    @Override
    public boolean isProtectionActive(ItemStack stack, EntityPlayer player) {
        return stack.getItemDamage() < stack.getMaxDamage();
    }
    
    /**
     * Применяет эффекты экзокостюма к игроку
     */
    public static void applyExosuitEffects(EntityPlayer player) {
        if (isFullSetWorn(player)) {
            // Ночное видение
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 260, 0, false, false));
            
            // Ускорение движения
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 260, 0, false, false));
        }
    }
    
    private String getArmorPartName(EntityEquipmentSlot slot) {
        switch (slot) {
            case HEAD: return "helmet";
            case CHEST: return "chestplate";
            case LEGS: return "leggings";
            case FEET: return "boots";
            default: return "unknown";
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        double protection = getRadiationProtection(stack, null) * 100;
        
        tooltip.add(TextFormatting.GREEN + I18n.format("armor.radiation_protection") + ": " + 
            String.format("%.1f%%", protection));
        
        tooltip.add(TextFormatting.GRAY + I18n.format("armor.exosuit.desc"));
        
        tooltip.add("");
        tooltip.add(TextFormatting.AQUA + I18n.format("armor.exosuit.features"));
        tooltip.add(TextFormatting.YELLOW + "- " + I18n.format("armor.exosuit.flight"));
        tooltip.add(TextFormatting.YELLOW + "- " + I18n.format("armor.exosuit.strength"));
        tooltip.add(TextFormatting.YELLOW + "- " + I18n.format("armor.exosuit.infinite_energy"));
        tooltip.add(TextFormatting.YELLOW + "- " + I18n.format("armor.exosuit.night_vision"));
        tooltip.add(TextFormatting.YELLOW + "- " + I18n.format("armor.exosuit.speed"));
        
        if (this.armorType == EntityEquipmentSlot.FEET) {
            tooltip.add("");
            tooltip.add(TextFormatting.YELLOW + I18n.format("armor.exosuit.full_set") + ": " + 
                TextFormatting.GREEN + "99%");
        }
    }
    
    public static double getFullSetProtection(EntityPlayer player) {
        double totalProtection = 0.0;
        
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ItemAtomicExoSuit) {
                ItemAtomicExoSuit armor = (ItemAtomicExoSuit) stack.getItem();
                totalProtection += armor.getRadiationProtection(stack, player);
            }
        }
        
        return Math.min(totalProtection, 1.0);
    }
    
    public static boolean isFullSetWorn(EntityPlayer player) {
        return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemAtomicExoSuit &&
               player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemAtomicExoSuit &&
               player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemAtomicExoSuit &&
               player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemAtomicExoSuit;
    }
}
