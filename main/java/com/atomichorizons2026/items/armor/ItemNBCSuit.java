package com.atomichorizons2026.items.armor;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.radiation.IRadiationProtection;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
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
 * NBC Suit - Ядерно-биологическая защита.
 * Обеспечивает 75% защиты от радиации.
 * Встроенный дозиметр в шлеме.
 */
public class ItemNBCSuit extends ItemArmor implements IRadiationProtection {
    
    public static final ArmorMaterial NBC_MATERIAL = EnumHelper.addArmorMaterial(
        "NBC",
        AtomicHorizons2026.MODID + ":nbc",
        15,
        new int[]{2, 3, 2, 2},
        10,
        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
        0.0f
    );
    
    private static final double[] RADIATION_PROTECTION = {0.1875, 0.1875, 0.1875, 0.1875}; // 18.75% каждая = 75% полный
    
    public ItemNBCSuit(EntityEquipmentSlot slot) {
        super(NBC_MATERIAL, 0, slot);
        
        String name = "nbc_" + getArmorPartName(slot);
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
        return ProtectionType.NBC;
    }
    
    @Override
    public boolean isProtectionActive(ItemStack stack, EntityPlayer player) {
        return stack.getItemDamage() < stack.getMaxDamage();
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
        
        tooltip.add(TextFormatting.GRAY + I18n.format("armor.nbc.desc"));
        
        if (this.armorType == EntityEquipmentSlot.HEAD) {
            tooltip.add(TextFormatting.AQUA + I18n.format("armor.nbc.dosimeter"));
        }
        
        if (this.armorType == EntityEquipmentSlot.FEET) {
            tooltip.add("");
            tooltip.add(TextFormatting.YELLOW + I18n.format("armor.nbc.full_set") + ": " + 
                TextFormatting.GREEN + "75%");
        }
    }
    
    public static double getFullSetProtection(EntityPlayer player) {
        double totalProtection = 0.0;
        
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ItemNBCSuit) {
                ItemNBCSuit armor = (ItemNBCSuit) stack.getItem();
                totalProtection += armor.getRadiationProtection(stack, player);
            }
        }
        
        return Math.min(totalProtection, 1.0);
    }
    
    public static boolean isFullSetWorn(EntityPlayer player) {
        return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemNBCSuit &&
               player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemNBCSuit &&
               player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemNBCSuit &&
               player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemNBCSuit;
    }
}
