package com.atomichorizons2026.items;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.handlers.RegistryHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Топливный стержень HALEU с системой износа.
 * Имеет ограниченный срок службы (24 часа реального времени).
 */
public class ItemFuelRod extends ItemBase {
    
    // Максимальное время работы в тиках (24 часа * 60 минут * 60 секунд * 20 тиков)
    public static final int MAX_BURN_TIME = 24 * 60 * 60 * 20; // 1,728,000 тиков
    
    public ItemFuelRod(String name) {
        super(name);
        this.setCreativeTab(CreativeTabs.MATERIALS);
        this.setMaxStackSize(1); // Не стакаем
        this.setMaxDamage(MAX_BURN_TIME); // Используем прочность как индикатор износа
        this.setNoRepair(); // Нельзя починить
    }
    
    /**
     * Получает оставшееся время горения в тиках
     */
    public int getRemainingBurnTime(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            return MAX_BURN_TIME; // Новый стержень
        }
        return nbt.getInteger("burnTime");
    }
    
    /**
     * Устанавливает оставшееся время горения
     */
    public void setRemainingBurnTime(ItemStack stack, int burnTime) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setInteger("burnTime", burnTime);
        
        // Обновляем damage для отображения
        int damage = MAX_BURN_TIME - burnTime;
        stack.setItemDamage(Math.max(0, Math.min(damage, MAX_BURN_TIME)));
    }
    
    /**
     * Получает процент оставшегося топлива (0.0 - 1.0)
     */
    public double getFuelPercent(ItemStack stack) {
        return (double) getRemainingBurnTime(stack) / MAX_BURN_TIME;
    }
    
    /**
     * Изнашивает топливо на указанное количество тиков
     */
    public void consumeFuel(ItemStack stack, int ticks) {
        int remaining = getRemainingBurnTime(stack);
        remaining -= ticks;
        if (remaining < 0) remaining = 0;
        setRemainingBurnTime(stack, remaining);
    }
    
    /**
     * Проверяет, пустой ли стержень
     */
    public boolean isDepleted(ItemStack stack) {
        return getRemainingBurnTime(stack) <= 0;
    }
    
    /**
     * Создает новый полный стержень
     */
    public static ItemStack createFullRod() {
        ItemStack stack = new ItemStack(RegistryHandler.FUEL_ROD);
        ((ItemFuelRod)RegistryHandler.FUEL_ROD).setRemainingBurnTime(stack, MAX_BURN_TIME);
        return stack;
    }
    
    /**
     * Показывает информацию при наведении
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        double percent = getFuelPercent(stack) * 100;
        int remainingTicks = getRemainingBurnTime(stack);
        int remainingHours = remainingTicks / (60 * 60 * 20);
        int remainingMinutes = (remainingTicks % (60 * 60 * 20)) / (60 * 20);
        
        TextFormatting color;
        if (percent > 50) {
            color = TextFormatting.GREEN;
        } else if (percent > 25) {
            color = TextFormatting.YELLOW;
        } else if (percent > 5) {
            color = TextFormatting.GOLD;
        } else {
            color = TextFormatting.RED;
        }
        
        tooltip.add("");
        tooltip.add(TextFormatting.GRAY + "Fuel Level:");
        tooltip.add(color + String.format("%.1f%%", percent) + TextFormatting.GRAY + " remaining");
        tooltip.add(TextFormatting.GRAY + String.format("(~%dh %dm left)", remainingHours, remainingMinutes));
        
        if (isDepleted(stack)) {
            tooltip.add("");
            tooltip.add(TextFormatting.RED + "DEPLETED - Handle with care!");
        } else if (percent < 10) {
            tooltip.add("");
            tooltip.add(TextFormatting.GOLD + "Warning: Fuel running low!");
        }
    }
    
    /**
     * Показывает прочность в слоте
     */
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }
    
    /**
     * Цвет durability бара
     */
    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        double percent = getFuelPercent(stack);
        if (percent > 0.5) {
            return 0x00FF00; // Зеленый
        } else if (percent > 0.25) {
            return 0xFFFF00; // Желтый
        } else if (percent > 0.1) {
            return 0xFF8000; // Оранжевый
        } else {
            return 0xFF0000; // Красный
        }
    }
    
    /**
     * Начальное NBT при создании
     */
    @Override
    public void onCreated(ItemStack stack, World world, net.minecraft.entity.player.EntityPlayer player) {
        if (!stack.hasTagCompound()) {
            setRemainingBurnTime(stack, MAX_BURN_TIME);
        }
    }
}
