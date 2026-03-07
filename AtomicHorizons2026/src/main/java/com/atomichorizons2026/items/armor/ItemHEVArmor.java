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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * HEV Suit (Hazardous Environment Vest) - энергозависимая защитная броня.
 * Обеспечивает 85% защиты от радиации при наличии RF-энергии.
 * Требует энергию для работы системы защиты.
 * 
 * Вдохновлено костюмом из Half-Life.
 */
public class ItemHEVArmor extends ItemArmor implements IRadiationProtection {
    
    // Материал HEV брони
    public static final ArmorMaterial HEV_MATERIAL = EnumHelper.addArmorMaterial(
        "HEV_SUIT",
        AtomicHorizons2026.MODID + ":hev_suit",
        35, // Высокая прочность
        new int[]{3, 6, 5, 3}, // Хорошая физическая защита
        15,
        SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
        2.0f
    );
    
    // Защита от радиации для каждой части (21.25% за часть = 85% полный комплект)
    private static final double[] RADIATION_PROTECTION = {0.2125, 0.2125, 0.2125, 0.2125};
    
    // Параметры энергии
    public static final int MAX_ENERGY = 1000000; // 1M RF
    public static final int ENERGY_PER_PROTECTION = 10; // Энергия за тик защиты
    public static final int ENERGY_TRANSFER_RATE = 10000; // Скорость зарядки
    
    // NBT ключи
    private static final String NBT_ENERGY = "energy";
    private static final String NBT_ENABLED = "enabled";
    
    public ItemHEVArmor(EntityEquipmentSlot slot) {
        super(HEV_MATERIAL, 2, slot);
        
        String name = "hev_" + getArmorPartName(slot);
        setUnlocalizedName(name);
        setRegistryName(AtomicHorizons2026.MODID, name);
        setCreativeTab(CreativeTabs.COMBAT);
        setMaxDamage(0); // Используем энергию вместо прочности
    }
    
    @Override
    public double getRadiationProtection(ItemStack stack, EntityPlayer player) {
        // Защита работает только если есть энергия
        if (!isProtectionActive(stack, player)) {
            return 0.0;
        }
        return RADIATION_PROTECTION[this.armorType.getIndex()];
    }
    
    @Override
    public boolean isProtectionActive(ItemStack stack, EntityPlayer player) {
        IEnergyStorage energy = getEnergyStorage(stack);
        return energy != null && energy.getEnergyStored() >= ENERGY_PER_PROTECTION;
    }
    
    @Override
    public ProtectionType getProtectionType() {
        return ProtectionType.POWERED;
    }
    
    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        super.onArmorTick(world, player, stack);
        
        if (world.isRemote) return;
        
        // Потребление энергии при активной защите
        if (isProtectionActive(stack, player)) {
            IEnergyStorage energy = getEnergyStorage(stack);
            if (energy != null) {
                // Потребляем энергию каждый тик
                energy.extractEnergy(ENERGY_PER_PROTECTION, false);
            }
        }
        
        // Обновляем NBT
        updateEnergyNBT(stack);
    }
    
    /**
     * Получает хранилище энергии из стека
     */
    public static IEnergyStorage getEnergyStorage(ItemStack stack) {
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            return stack.getCapability(CapabilityEnergy.ENERGY, null);
        }
        return null;
    }
    
    /**
     * Получает текущую энергию
     */
    public static int getEnergy(ItemStack stack) {
        IEnergyStorage energy = getEnergyStorage(stack);
        return energy != null ? energy.getEnergyStored() : 0;
    }
    
    /**
     * Получает максимальную энергию
     */
    public static int getMaxEnergy(ItemStack stack) {
        IEnergyStorage energy = getEnergyStorage(stack);
        return energy != null ? energy.getMaxEnergyStored() : MAX_ENERGY;
    }
    
    /**
     * Устанавливает энергию
     */
    public static void setEnergy(ItemStack stack, int energy) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setInteger(NBT_ENERGY, Math.min(energy, MAX_ENERGY));
    }
    
    /**
     * Обновляет NBT энергии
     */
    private void updateEnergyNBT(ItemStack stack) {
        IEnergyStorage energy = getEnergyStorage(stack);
        if (energy != null) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            nbt.setInteger(NBT_ENERGY, energy.getEnergyStored());
        }
    }
    
    /**
     * Включает/выключает броню
     */
    public static void toggleEnabled(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setBoolean(NBT_ENABLED, !nbt.getBoolean(NBT_ENABLED));
    }
    
    /**
     * Проверяет, включена ли броня
     */
    public static boolean isEnabled(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt == null || nbt.getBoolean(NBT_ENABLED);
    }
    
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new HEVCapabilityProvider(stack, nbt);
    }
    
    /**
     * Capability provider для HEV брони
     */
    private static class HEVCapabilityProvider implements ICapabilityProvider {
        private final EnergyStorage energyStorage;
        
        public HEVCapabilityProvider(ItemStack stack, @Nullable NBTTagCompound nbt) {
            int initialEnergy = 0;
            if (nbt != null && nbt.hasKey(NBT_ENERGY)) {
                initialEnergy = nbt.getInteger(NBT_ENERGY);
            }
            
            this.energyStorage = new EnergyStorage(MAX_ENERGY, ENERGY_TRANSFER_RATE, ENERGY_TRANSFER_RATE, initialEnergy);
        }
        
        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityEnergy.ENERGY;
        }
        
        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityEnergy.ENERGY) {
                return CapabilityEnergy.ENERGY.cast(energyStorage);
            }
            return null;
        }
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
        
        int energy = getEnergy(stack);
        int maxEnergy = getMaxEnergy(stack);
        double protection = getRadiationProtection(stack, null) * 100;
        boolean active = isProtectionActive(stack, null);
        
        // Статус защиты
        if (active) {
            tooltip.add(TextFormatting.GREEN + "● " + I18n.format("hev.status.active"));
        } else {
            tooltip.add(TextFormatting.RED + "● " + I18n.format("hev.status.inactive"));
        }
        
        // Защита от радиации
        tooltip.add(TextFormatting.AQUA + I18n.format("armor.radiation_protection") + ": " + 
            (active ? TextFormatting.GREEN : TextFormatting.GRAY) + 
            String.format("%.1f%%", protection));
        
        // Энергия
        TextFormatting energyColor = TextFormatting.GREEN;
        double energyPercent = (double) energy / maxEnergy;
        if (energyPercent < 0.3) energyColor = TextFormatting.YELLOW;
        if (energyPercent < 0.1) energyColor = TextFormatting.RED;
        
        tooltip.add(TextFormatting.GRAY + I18n.format("hev.energy") + ": " + 
            energyColor + String.format("%,d / %,d RF", energy, maxEnergy));
        
        // Прогресс-бар энергии
        int barLength = 20;
        int filled = (int) (energyPercent * barLength);
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            bar.append(i < filled ? "|" : " ");
        }
        bar.append("]");
        tooltip.add(energyColor + bar.toString());
        
        // Описание
        tooltip.add("");
        tooltip.add(TextFormatting.GRAY + I18n.format("armor.hev.desc"));
        
        // Предупреждение о энергии
        if (energyPercent < 0.1) {
            tooltip.add(TextFormatting.RED + I18n.format("hev.warning.low_energy"));
        }
        
        // Полный комплект
        if (this.armorType == EntityEquipmentSlot.FEET) {
            tooltip.add("");
            tooltip.add(TextFormatting.YELLOW + I18n.format("armor.hev.full_set") + ": " + 
                TextFormatting.GREEN + "85%");
            tooltip.add(TextFormatting.GRAY + I18n.format("hev.energy_consumption", ENERGY_PER_PROTECTION));
        }
    }
    
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }
    
    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        // Используем полосу прочности для отображения энергии
        int energy = getEnergy(stack);
        int maxEnergy = getMaxEnergy(stack);
        return 1.0 - ((double) energy / maxEnergy);
    }
    
    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        double energyPercent = (double) getEnergy(stack) / getMaxEnergy(stack);
        
        if (energyPercent > 0.5) {
            return 0x00FF00; // Зеленый
        } else if (energyPercent > 0.25) {
            return 0xFFFF00; // Желтый
        } else {
            return 0xFF0000; // Красный
        }
    }
    
    /**
     * Получает полную защиту от радиации для комплекта брони
     */
    public static double getFullSetProtection(EntityPlayer player) {
        double totalProtection = 0.0;
        
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ItemHEVArmor) {
                ItemHEVArmor armor = (ItemHEVArmor) stack.getItem();
                totalProtection += armor.getRadiationProtection(stack, player);
            }
        }
        
        return Math.min(totalProtection, 1.0);
    }
    
    /**
     * Проверяет, надет ли полный комплект HEV
     */
    public static boolean isFullSetWorn(EntityPlayer player) {
        return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemHEVArmor &&
               player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemHEVArmor &&
               player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemHEVArmor &&
               player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemHEVArmor;
    }
    
    /**
     * Получает общий запас энергии комплекта
     */
    public static int getTotalEnergy(EntityPlayer player) {
        int total = 0;
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ItemHEVArmor) {
                total += getEnergy(stack);
            }
        }
        return total;
    }
    
    /**
     * Получает максимальную энергию комплекта
     */
    public static int getTotalMaxEnergy(EntityPlayer player) {
        int total = 0;
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ItemHEVArmor) {
                total += getMaxEnergy(stack);
            }
        }
        return total;
    }
}
