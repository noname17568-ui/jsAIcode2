package com.atomichorizons2026.items;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.radiation.PlayerRadiationCapability;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * RadAway (Антирадин) - снижает уровень радиации игрока.
 * Может быть в форме таблеток или шприца.
 * После применения накладывает временную слабость.
 */
public class ItemRadAway extends ItemFood {
    
    public enum RadAwayType {
        TABLET("radaway_tablet", 200, 100),      // Таблетка - слабее, быстрее
        INJECTOR("radaway_injector", 500, 20),   // Шприц - сильнее, мгновенно
        ADVANCED("radaway_advanced", 1000, 50);  // Улучшенный - максимальный эффект
        
        private final String name;
        private final int radiationReduction;
        private final int useDuration;
        
        RadAwayType(String name, int radiationReduction, int useDuration) {
            this.name = name;
            this.radiationReduction = radiationReduction;
            this.useDuration = useDuration;
        }
        
        public String getName() {
            return name;
        }
        
        public int getRadiationReduction() {
            return radiationReduction;
        }
        
        public int getUseDuration() {
            return useDuration;
        }
    }
    
    private final RadAwayType type;
    
    /**
     * Создает RadAway указанного типа
     */
    public ItemRadAway(RadAwayType type) {
        super(0, 0.0f, false);
        this.type = type;
        
        setUnlocalizedName(type.getName());
        setRegistryName(AtomicHorizons2026.MODID, type.getName());
        setCreativeTab(CreativeTabs.BREWING);
        setMaxStackSize(16);
        
        // Всегда съедобно
        setAlwaysEdible();
    }
    
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return type.getUseDuration();
    }
    
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        // Шприц использует анимацию блокировки (как зелье)
        // Таблетка - анимацию еды
        if (type == RadAwayType.INJECTOR || type == RadAwayType.ADVANCED) {
            return EnumAction.BOW;
        }
        return EnumAction.EAT;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        // Проверяем, есть ли радиация
        PlayerRadiationCapability cap = player.getCapability(
            PlayerRadiationCapability.RADIATION_CAPABILITY, null);
        
        if (cap == null || cap.getRadiationDose() <= 0) {
            if (!world.isRemote) {
                player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    net.minecraft.util.text.TextFormatting.GREEN + 
                    I18n.format("radaway.no_radiation")));
            }
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
    
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return stack;
        }
        
        // Получаем capability
        PlayerRadiationCapability cap = player.getCapability(
            PlayerRadiationCapability.RADIATION_CAPABILITY, null);
        
        if (cap != null) {
            double oldDose = cap.getRadiationDose();
            
            // Снижаем радиацию
            cap.reduceRadiation(type.getRadiationReduction());
            
            double newDose = cap.getRadiationDose();
            double reduced = oldDose - newDose;
            
            // Сообщаем игроку
            player.sendMessage(new net.minecraft.util.text.TextComponentString(
                net.minecraft.util.text.TextFormatting.GREEN + 
                I18n.format("radaway.reduced", String.format("%.1f", reduced))));
            
            // Оставшаяся доза
            if (newDose > 0) {
                player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    net.minecraft.util.text.TextFormatting.YELLOW + 
                    I18n.format("radaway.remaining", String.format("%.1f", newDose))));
            } else {
                player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    net.minecraft.util.text.TextFormatting.GREEN + 
                    I18n.format("radaway.fully_cleared")));
            }
        }
        
        // Накладываем побочные эффекты
        applySideEffects(player);
        
        // Звуковой эффект
        world.playSound(null, player.getPosition(), 
            type == RadAwayType.TABLET ? SoundEvents.ENTITY_PLAYER_BURP : SoundEvents.ITEM_BOTTLE_FILL,
            SoundCategory.PLAYERS, 0.5f, 1.0f);
        
        // Уменьшаем стек
        stack.shrink(1);
        
        return stack;
    }
    
    /**
     * Применяет побочные эффекты после использования
     */
    private void applySideEffects(EntityPlayer player) {
        // Все типы накладывают слабость
        int weaknessDuration = 1200; // 60 секунд
        int weaknessLevel = 0;
        
        switch (type) {
            case TABLET:
                // Таблетка - только слабость
                player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, weaknessDuration, weaknessLevel));
                break;
            case INJECTOR:
                // Шприц - слабость и тошнота
                player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, weaknessDuration, weaknessLevel));
                player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
                break;
            case ADVANCED:
                // Улучшенный - минимальные побочки
                player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 300, 0));
                break;
        }
        
        // Сообщение о побочках
        player.sendMessage(new net.minecraft.util.text.TextComponentString(
            net.minecraft.util.text.TextFormatting.GRAY + 
            I18n.format("radaway.side_effects")));
    }
    
    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        // Не используется - логика в onItemUseFinish
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        tooltip.add(net.minecraft.util.text.TextFormatting.AQUA + 
            I18n.format("radaway.reduces_radiation", type.getRadiationReduction()));
        
        tooltip.add("");
        tooltip.add(net.minecraft.util.text.TextFormatting.GRAY + 
            I18n.format("radaway.side_effect.warning"));
        
        switch (type) {
            case TABLET:
                tooltip.add(net.minecraft.util.text.TextFormatting.RED + "- " + 
                    I18n.format("effect.weakness.name") + " (60s)");
                break;
            case INJECTOR:
                tooltip.add(net.minecraft.util.text.TextFormatting.RED + "- " + 
                    I18n.format("effect.weakness.name") + " (60s)");
                tooltip.add(net.minecraft.util.text.TextFormatting.RED + "- " + 
                    I18n.format("effect.nausea.name") + " (10s)");
                break;
            case ADVANCED:
                tooltip.add(net.minecraft.util.text.TextFormatting.YELLOW + "- " + 
                    I18n.format("effect.weakness.name") + " (15s)");
                break;
        }
        
        tooltip.add("");
        tooltip.add(net.minecraft.util.text.TextFormatting.DARK_GRAY + 
            I18n.format("radaway.tooltip.use_when_irradiated"));
    }
    
    /**
     * Получает тип RadAway
     */
    public RadAwayType getType() {
        return type;
    }
}
