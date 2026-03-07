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
 * Свинцовая броня (Lead-Lined Armor).
 * Обеспечивает 90% защиты от радиации.
 * Накладывает эффект замедления при надевании из-за веса.
 * Высокая прочность.
 */
public class ItemLeadLinedArmor extends ItemArmor implements IRadiationProtection {
    
    // Материал свинцовой брони
    public static final ArmorMaterial LEAD_MATERIAL = EnumHelper.addArmorMaterial(
        "LEAD_LINED",
        AtomicHorizons2026.MODID + ":lead_lined",
        25, // Высокая прочность
        new int[]{2, 5, 4, 2}, // Умеренная физическая защита
        10,
        SoundEvents.ITEM_ARMOR_EQUIP_IRON,
        1.0f
    );
    
    // Защита от радиации для каждой части (22.5% за часть = 90% полный комплект)
    private static final double[] RADIATION_PROTECTION = {0.225, 0.225, 0.225, 0.225};
    
    // Уровень замедления
    private static final int SLOWNESS_LEVEL = 1; // Замедление II
    private static final int SLOWNESS_DURATION = 100; // 5 секунд (обновляется)
    
    public ItemLeadLinedArmor(EntityEquipmentSlot slot) {
        super(LEAD_MATERIAL, 1, slot);
        
        String name = "lead_lined_" + getArmorPartName(slot);
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
        return ProtectionType.LEAD_LINED;
    }
    
    @Override
    public boolean isProtectionActive(ItemStack stack, EntityPlayer player) {
        return stack.getItemDamage() < stack.getMaxDamage();
    }
    
    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);
        
        if (world.isRemote) return;
        
        // Накладываем замедление каждые 4 секунды
        // Только если надета хотя бы одна часть
        if (world.getTotalWorldTime() % 80 == 0) {
            // Проверяем, не активен ли уже эффект от другой части брони
            PotionEffect currentSlowness = player.getActivePotionEffect(MobEffects.SLOWNESS);
            
            if (currentSlowness == null || currentSlowness.getDuration() < 60) {
                // Рассчитываем уровень замедления в зависимости от количества надетых частей
                int piecesWorn = countWornPieces(player);
                
                if (piecesWorn > 0) {
                    // Чем больше частей, тем сильнее замедление
                    int level = Math.min(piecesWorn - 1, 3); // Максимум Замедление IV
                    player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, level));
                }
            }
        }
    }
    
    /**
     * Считает количество надетых частей свинцовой брони
     */
    private int countWornPieces(EntityPlayer player) {
        int count = 0;
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ItemLeadLinedArmor) {
                count++;
            }
        }
        return count;
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
            TextFormatting.DARK_GREEN + String.format("%.1f%%", protection));
        
        tooltip.add(TextFormatting.GRAY + I18n.format("armor.lead_lined.desc"));
        
        // Предупреждение о замедлении
        tooltip.add(TextFormatting.RED + I18n.format("armor.lead_lined.slowdown"));
        
        // Показываем общую защиту при полном комплекте
        if (this.armorType == EntityEquipmentSlot.FEET) {
            tooltip.add("");
            tooltip.add(TextFormatting.YELLOW + I18n.format("armor.lead_lined.full_set") + ": " + 
                TextFormatting.DARK_GREEN + "90%");
            tooltip.add(TextFormatting.RED + I18n.format("armor.lead_lined.full_slowdown"));
        }
        
        // Показываем прочность
        int damage = stack.getItemDamage();
        int maxDamage = stack.getMaxDamage();
        int remaining = maxDamage - damage;
        
        TextFormatting durabilityColor = TextFormatting.GREEN;
        if (remaining < maxDamage * 0.3) durabilityColor = TextFormatting.YELLOW;
        if (remaining < maxDamage * 0.1) durabilityColor = TextFormatting.RED;
        
        tooltip.add(durabilityColor + I18n.format("armor.durability") + ": " + 
            remaining + "/" + maxDamage);
    }
    
    /**
     * Получает полную защиту от радиации для комплекта брони
     */
    public static double getFullSetProtection(EntityPlayer player) {
        double totalProtection = 0.0;
        
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ItemLeadLinedArmor) {
                ItemLeadLinedArmor armor = (ItemLeadLinedArmor) stack.getItem();
                totalProtection += armor.getRadiationProtection(stack, player);
            }
        }
        
        return Math.min(totalProtection, 1.0);
    }
    
    /**
     * Проверяет, надет ли полный комплект свинцовой брони
     */
    public static boolean isFullSetWorn(EntityPlayer player) {
        return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemLeadLinedArmor &&
               player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemLeadLinedArmor &&
               player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemLeadLinedArmor &&
               player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemLeadLinedArmor;
    }
    
    /**
     * Получает уровень замедления в зависимости от количества частей
     */
    public static int getSlownessLevel(EntityPlayer player) {
        int pieces = 0;
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ItemLeadLinedArmor) {
                pieces++;
            }
        }
        return Math.min(pieces, 4);
    }
}
