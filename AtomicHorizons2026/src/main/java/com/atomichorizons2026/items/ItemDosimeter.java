package com.atomichorizons2026.items;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.radiation.PlayerRadiationCapability;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Дозиметр - измеряет накопленную дозу радиации игрока.
 * Показывает общее количество полученной радиации за все время.
 */
public class ItemDosimeter extends ItemBase {
    
    private static final String NBT_PLAYER_UUID = "player_uuid";
    private static final String NBT_LAST_DOSE = "last_recorded_dose";
    
    // Звук щелчка дозиметра
    public static SoundEvent SOUND_DOSIMETER_CLICK;
    
    public ItemDosimeter() {
        super("dosimeter");
        setCreativeTab(CreativeTabs.TOOLS);
        setMaxStackSize(1);
        setMaxDamage(0);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (world.isRemote) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        
        // Получаем capability радиации игрока
        PlayerRadiationCapability cap = player.getCapability(
            PlayerRadiationCapability.RADIATION_CAPABILITY, null);
        
        if (cap == null) {
            player.sendMessage(new TextComponentString(
                TextFormatting.RED + I18n.format("dosimeter.error.no_data")));
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        
        double dose = cap.getRadiationDose();
        double resistance = cap.getRadiationResistance();
        
        // Сохраняем данные в NBT
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setString(NBT_PLAYER_UUID, player.getUniqueID().toString());
        nbt.setDouble(NBT_LAST_DOSE, dose);
        
        // Отправляем информацию игроку
        sendDosimeterReading(player, dose, resistance);
        
        // Воспроизводим звук
        if (SOUND_DOSIMETER_CLICK != null) {
            world.playSound(null, player.getPosition(), SOUND_DOSIMETER_CLICK, 
                SoundCategory.PLAYERS, 0.6f, 1.0f);
        }
        
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
    
    /**
     * Отправляет игроку информацию о дозе радиации
     */
    private void sendDosimeterReading(EntityPlayer player, double dose, double resistance) {
        // Заголовок
        player.sendMessage(new TextComponentString(""));
        player.sendMessage(new TextComponentString(
            TextFormatting.GREEN + "╔════════════════════════════════════╗"));
        player.sendMessage(new TextComponentString(
            TextFormatting.GREEN + "║     " + TextFormatting.WHITE + I18n.format("item.dosimeter.name") + 
            TextFormatting.GREEN + "     ║"));
        player.sendMessage(new TextComponentString(
            TextFormatting.GREEN + "╠════════════════════════════════════╣"));
        
        // Текущая доза
        String doseStr = formatDose(dose);
        TextFormatting doseColor = getDoseColor(dose);
        
        player.sendMessage(new TextComponentString(
            TextFormatting.GREEN + "║ " + TextFormatting.GRAY + I18n.format("dosimeter.total_dose") + 
            ": " + doseColor + doseStr + 
            getPadding(doseStr) + TextFormatting.GREEN + "║"));
        
        // Сопротивление
        String resistStr = String.format("%.0f%%", resistance * 100);
        player.sendMessage(new TextComponentString(
            TextFormatting.GREEN + "║ " + TextFormatting.GRAY + I18n.format("dosimeter.resistance") + 
            ": " + TextFormatting.AQUA + resistStr + 
            getPadding(resistStr + "   ") + TextFormatting.GREEN + "║"));
        
        // Статус здоровья
        String status = getHealthStatus(dose);
        TextFormatting statusColor = getStatusColor(dose);
        
        player.sendMessage(new TextComponentString(
            TextFormatting.GREEN + "║ " + TextFormatting.GRAY + I18n.format("dosimeter.status") + 
            ": " + statusColor + status + 
            getPadding(status) + TextFormatting.GREEN + "║"));
        
        // Рекомендации
        player.sendMessage(new TextComponentString(
            TextFormatting.GREEN + "╠════════════════════════════════════╣"));
        String advice = getMedicalAdvice(dose);
        player.sendMessage(new TextComponentString(
            TextFormatting.GREEN + "║ " + TextFormatting.YELLOW + advice + 
            getPadding(advice) + TextFormatting.GREEN + "║"));
        
        player.sendMessage(new TextComponentString(
            TextFormatting.GREEN + "╚════════════════════════════════════╝"));
        player.sendMessage(new TextComponentString(""));
    }
    
    /**
     * Форматирует дозу для отображения
     */
    private String formatDose(double dose) {
        if (dose < 1.0) {
            return String.format("%.2f RAD", dose);
        } else if (dose < 1000.0) {
            return String.format("%.1f RAD", dose);
        } else if (dose < 1000000.0) {
            return String.format("%.2f kRAD", dose / 1000.0);
        } else {
            return String.format("%.2f MRAD", dose / 1000000.0);
        }
    }
    
    /**
     * Получает цвет в зависимости от дозы
     */
    private TextFormatting getDoseColor(double dose) {
        if (dose < PlayerRadiationCapability.THRESHOLD_WEAKNESS) return TextFormatting.GREEN;
        if (dose < PlayerRadiationCapability.THRESHOLD_SLOWNESS) return TextFormatting.YELLOW;
        if (dose < PlayerRadiationCapability.THRESHOLD_POISON) return TextFormatting.GOLD;
        if (dose < PlayerRadiationCapability.THRESHODE_WITHER) return TextFormatting.RED;
        return TextFormatting.DARK_RED;
    }
    
    /**
     * Получает статус здоровья
     */
    private String getHealthStatus(double dose) {
        if (dose < PlayerRadiationCapability.THRESHOLD_WEAKNESS) 
            return I18n.format("dosimeter.status.healthy");
        if (dose < PlayerRadiationCapability.THRESHOLD_SLOWNESS) 
            return I18n.format("dosimeter.status.mild_exposure");
        if (dose < PlayerRadiationCapability.THRESHOLD_POISON) 
            return I18n.format("dosimeter.status.moderate_exposure");
        if (dose < PlayerRadiationCapability.THRESHODE_WITHER) 
            return I18n.format("dosimeter.status.severe_exposure");
        if (dose < PlayerRadiationCapability.THRESHOLD_DEATH) 
            return I18n.format("dosimeter.status.critical");
        return I18n.format("dosimeter.status.lethal");
    }
    
    /**
     * Получает цвет статуса
     */
    private TextFormatting getStatusColor(double dose) {
        if (dose < PlayerRadiationCapability.THRESHOLD_WEAKNESS) return TextFormatting.GREEN;
        if (dose < PlayerRadiationCapability.THRESHOLD_SLOWNESS) return TextFormatting.YELLOW;
        if (dose < PlayerRadiationCapability.THRESHOLD_POISON) return TextFormatting.GOLD;
        if (dose < PlayerRadiationCapability.THRESHODE_WITHER) return TextFormatting.RED;
        return TextFormatting.DARK_RED;
    }
    
    /**
     * Получает медицинскую рекомендацию
     */
    private String getMedicalAdvice(double dose) {
        if (dose < PlayerRadiationCapability.THRESHOLD_WEAKNESS) 
            return I18n.format("dosimeter.advice.none");
        if (dose < PlayerRadiationCapability.THRESHOLD_SLOWNESS) 
            return I18n.format("dosimeter.advice.monitor");
        if (dose < PlayerRadiationCapability.THRESHOLD_POISON) 
            return I18n.format("dosimeter.advice.radaway");
        if (dose < PlayerRadiationCapability.THRESHODE_WITHER) 
            return I18n.format("dosimeter.advice.urgent");
        return I18n.format("dosimeter.advice.emergency");
    }
    
    /**
     * Создает отступы для выравнивания
     */
    private String getPadding(String text) {
        int maxLen = 20;
        int spaces = maxLen - text.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        NBTTagCompound nbt = stack.getTagCompound();
        
        if (nbt != null && nbt.hasKey(NBT_LAST_DOSE)) {
            double lastDose = nbt.getDouble(NBT_LAST_DOSE);
            tooltip.add(TextFormatting.GRAY + I18n.format("dosimeter.tooltip.last_reading") + ": " + 
                getDoseColor(lastDose) + formatDose(lastDose));
        }
        
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_GRAY + I18n.format("dosimeter.tooltip.right_click"));
        tooltip.add(TextFormatting.DARK_GRAY + I18n.format("dosimeter.tooltip.measures_total"));
    }
}
