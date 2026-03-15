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
 * Химзащитный костюм (Hazmat Suit).
 * Обеспечивает 50% защиты от радиации.
 * Не требует энергии, но имеет низкую прочность.
 */
public class ItemHazmatArmor extends ItemArmor implements IRadiationProtection {
    
    // Материал брони Hazmat
    public static final ArmorMaterial HAZMAT_MATERIAL = EnumHelper.addArmorMaterial(
        "HAZMAT", 
        AtomicHorizons2026.MODID + ":hazmat",
        10, // Прочность (низкая)
        new int[]{1, 2, 3, 1}, // Защита от физического урона (голова, тело, ноги, ботинки)
        5, // Зачарование
        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
        0.0f
    );
    
    // Защита от радиации для каждой части
    private static final double[] RADIATION_PROTECTION = {0.125, 0.125, 0.125, 0.125}; // По 12.5% за часть = 50% полный комплект
    
    public ItemHazmatArmor(EntityEquipmentSlot slot) {
        super(HAZMAT_MATERIAL, 0, slot);
        
        String name = "hazmat_" + getArmorPartName(slot);
        setTranslationKey(name);
        setRegistryName(AtomicHorizons2026.MODID, name);
        setCreativeTab(CreativeTabs.COMBAT);
        
        // Hazmat не дает брони от физического урона
        // Только от радиации
    }
    
    @Override
    public double getRadiationProtection(ItemStack stack, EntityPlayer player) {
        // Защита зависит от слота
        return RADIATION_PROTECTION[this.armorType.getIndex()];
    }
    
    @Override
    public ProtectionType getProtectionType() {
        return ProtectionType.HAZMAT;
    }
    
    @Override
    public boolean isProtectionActive(ItemStack stack, EntityPlayer player) {
        // Hazmat всегда активен, если надет
        // Но проверяем прочность
        return stack.getItemDamage() < stack.getMaxDamage();
    }
    
    /**
     * Получает имя части брони
     */
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
        
        tooltip.add(TextFormatting.GRAY + I18n.format("armor.hazmat.desc"));
        
        // Показываем общую защиту при полном комплекте
        if (this.armorType == EntityEquipmentSlot.FEET) {
            tooltip.add("");
            tooltip.add(TextFormatting.YELLOW + I18n.format("armor.hazmat.full_set") + ": " + 
                TextFormatting.GREEN + "50%");
        }
        
        // Предупреждение о прочности
        int damage = stack.getItemDamage();
        int maxDamage = stack.getMaxDamage();
        int remaining = maxDamage - damage;
        
        if (remaining < maxDamage * 0.2) {
            tooltip.add(TextFormatting.RED + I18n.format("armor.hazmat.low_durability"));
        }
    }
    
    /**
     * Получает полную защиту от радиации для комплекта брони
     * @param player Игрок
     * @return Общая защита (0.0 - 1.0)
     */
    public static double getFullSetProtection(EntityPlayer player) {
        double totalProtection = 0.0;
        
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ItemHazmatArmor) {
                ItemHazmatArmor armor = (ItemHazmatArmor) stack.getItem();
                totalProtection += armor.getRadiationProtection(stack, player);
            }
        }
        
        return Math.min(totalProtection, 1.0);
    }
    
    /**
     * Проверяет, надет ли полный комплект Hazmat
     */
    public static boolean isFullSetWorn(EntityPlayer player) {
        return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemHazmatArmor &&
               player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemHazmatArmor &&
               player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemHazmatArmor &&
               player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemHazmatArmor;
    }
}
