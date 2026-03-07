package com.atomichorizons2026.items;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.radiation.RadiationManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Счетчик Гейгера - измеряет уровень радиации в чанке.
 * Издает звуки треска при наличии радиации.
 */
public class ItemGeigerCounter extends ItemBase {
    
    private static final String NBT_LAST_CHECK = "last_check_time";
    private static final String NBT_LAST_RADIATION = "last_radiation_level";
    private static final int CHECK_INTERVAL = 20; // Проверка каждую секунду
    
    // Звук треска (будет зарегистрирован отдельно)
    public static SoundEvent SOUND_GEIGER_CLICK;
    
    public ItemGeigerCounter() {
        super("geiger_counter");
        setCreativeTab(CreativeTabs.TOOLS);
        setMaxStackSize(1);
        setMaxDamage(0);
    }
    
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected);
        
        if (world.isRemote) return;
        if (!(entity instanceof EntityPlayerMP)) return;
        
        EntityPlayerMP player = (EntityPlayerMP) entity;
        
        // Проверяем только если предмет в инвентаре (не обязательно в руке)
        long currentTime = world.getTotalWorldTime();
        
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        
        long lastCheck = nbt.getLong(NBT_LAST_CHECK);
        
        // Проверяем каждую секунду
        if (currentTime - lastCheck >= CHECK_INTERVAL) {
            nbt.setLong(NBT_LAST_CHECK, currentTime);
            
            // Получаем уровень радиации в чанке игрока
            BlockPos playerPos = player.getPosition();
            ChunkPos chunkPos = new ChunkPos(playerPos);
            double radiation = RadiationManager.getInstance().getChunkRadiation(world, chunkPos);
            
            nbt.setDouble(NBT_LAST_RADIATION, radiation);
            
            // Если есть радиация - издаем звук и показываем в Action Bar
            if (radiation > 0) {
                // Рассчитываем pitch в зависимости от уровня радиации
                // Чем выше радиация - тем выше частота
                float pitch = calculatePitch(radiation);
                
                // Воспроизводим звук
                if (SOUND_GEIGER_CLICK != null) {
                    world.playSound(null, playerPos, SOUND_GEIGER_CLICK, 
                        SoundCategory.PLAYERS, 0.5f, pitch);
                }
                
                // Отправляем сообщение в Action Bar
                String radiationText = formatRadiationLevel(radiation);
                player.sendStatusMessage(new TextComponentString(
                    TextFormatting.YELLOW + "☢ " + radiationText), true);
            }
        }
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (world.isRemote) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        
        // При ПКМ показываем детальную информацию в чат
        BlockPos playerPos = player.getPosition();
        ChunkPos chunkPos = new ChunkPos(playerPos);
        double radiation = RadiationManager.getInstance().getChunkRadiation(world, chunkPos);
        
        // Сохраняем в NBT
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setDouble(NBT_LAST_RADIATION, radiation);
        
        // Отправляем сообщение
        String radiationText = formatRadiationLevel(radiation);
        TextFormatting color = getRadiationColor(radiation);
        
        player.sendMessage(new TextComponentString(
            TextFormatting.GREEN + "=== " + I18n.format("item.geiger_counter.name") + " ==="));
        player.sendMessage(new TextComponentString(
            TextFormatting.GRAY + I18n.format("geiger.chunk_radiation") + ": " + 
            color + radiationText));
        
        if (radiation > 0) {
            player.sendMessage(new TextComponentString(
                TextFormatting.GRAY + I18n.format("geiger.status") + ": " + 
                getRadiationStatus(radiation)));
        }
        
        // Воспроизводим звук
        if (SOUND_GEIGER_CLICK != null) {
            world.playSound(null, playerPos, SOUND_GEIGER_CLICK, 
                SoundCategory.PLAYERS, 0.8f, calculatePitch(radiation));
        }
        
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        NBTTagCompound nbt = stack.getTagCompound();
        double lastRadiation = nbt != null ? nbt.getDouble(NBT_LAST_RADIATION) : 0.0;
        
        tooltip.add(TextFormatting.GRAY + I18n.format("geiger.tooltip.last_reading") + ": " + 
            getRadiationColor(lastRadiation) + formatRadiationLevel(lastRadiation));
        
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_GRAY + I18n.format("geiger.tooltip.auto_check"));
        tooltip.add(TextFormatting.DARK_GRAY + I18n.format("geiger.tooltip.right_click_info"));
    }
    
    /**
     * Рассчитывает pitch звука в зависимости от уровня радиации
     */
    private float calculatePitch(double radiation) {
        // Базовый pitch 0.5, максимум 2.0
        // Логарифмическая шкала для более реалистичного эффекта
        double logRad = Math.log10(radiation + 1) / 4.0; // Нормализация
        return 0.5f + (float) (logRad * 1.5f);
    }
    
    /**
     * Форматирует уровень радиации для отображения
     */
    private String formatRadiationLevel(double radiation) {
        if (radiation < 1.0) {
            return String.format("%.2f RAD", radiation);
        } else if (radiation < 1000.0) {
            return String.format("%.1f RAD", radiation);
        } else {
            return String.format("%.2f kRAD", radiation / 1000.0);
        }
    }
    
    /**
     * Получает цвет в зависимости от уровня радиации
     */
    private TextFormatting getRadiationColor(double radiation) {
        if (radiation < 10.0) return TextFormatting.GREEN;
        if (radiation < 100.0) return TextFormatting.YELLOW;
        if (radiation < 500.0) return TextFormatting.GOLD;
        if (radiation < 1000.0) return TextFormatting.RED;
        return TextFormatting.DARK_RED;
    }
    
    /**
     * Получает статус радиации
     */
    private String getRadiationStatus(double radiation) {
        if (radiation < 1.0) return TextFormatting.GREEN + I18n.format("geiger.status.safe");
        if (radiation < 10.0) return TextFormatting.YELLOW + I18n.format("geiger.status.elevated");
        if (radiation < 100.0) return TextFormatting.GOLD + I18n.format("geiger.status.dangerous");
        if (radiation < 500.0) return TextFormatting.RED + I18n.format("geiger.status.critical");
        return TextFormatting.DARK_RED + I18n.format("geiger.status.lethal");
    }
}
