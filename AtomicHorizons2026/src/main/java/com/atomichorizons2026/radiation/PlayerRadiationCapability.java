package com.atomichorizons2026.radiation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/**
 * Capability для хранения накопленной дозы радиации игрока.
 * Доза не уменьшается со временем (только RadAway!)
 */
public class PlayerRadiationCapability implements ICapabilitySerializable<NBTTagCompound> {
    
    @CapabilityInject(PlayerRadiationCapability.class)
    public static Capability<PlayerRadiationCapability> RADIATION_CAPABILITY = null;
    
    // Пороги радиации (RAD)
    public static final double THRESHOLD_NONE = 0;
    public static final double THRESHOLD_WEAKNESS = 200;
    public static final double THRESHOLD_SLOWNESS = 400;
    public static final double THRESHOLD_POISON = 600;
    public static final double THRESHODE_WITHER = 800;
    public static final double THRESHOLD_DEATH = 1000;
    
    // Текущая доза игрока
    private double radiationDose = 0;
    
    // Сопротивление радиации (0.0 - 1.0)
    private double radiationResistance = 0.0;
    
    public double getRadiationDose() {
        return radiationDose;
    }
    
    public void setRadiationDose(double dose) {
        this.radiationDose = Math.max(0, dose);
    }
    
    /**
     * Добавляет радиацию с учетом сопротивления
     */
    public void addRadiation(double amount) {
        double actualAmount = amount * (1.0 - radiationResistance);
        this.radiationDose += actualAmount;
    }
    
    /**
     * Снижает дозу (RadAway и т.д.)
     */
    public void reduceRadiation(double amount) {
        this.radiationDose = Math.max(0, this.radiationDose - amount);
    }
    
    public double getRadiationResistance() {
        return radiationResistance;
    }
    
    public void setRadiationResistance(double resistance) {
        this.radiationResistance = Math.max(0, Math.min(1.0, resistance));
    }
    
    /**
     * Получает текущий уровень эффекта радиации
     */
    public RadiationLevel getRadiationLevel() {
        if (radiationDose >= THRESHOLD_DEATH) return RadiationLevel.DEADLY;
        if (radiationDose >= THRESHODE_WITHER) return RadiationLevel.WITHER;
        if (radiationDose >= THRESHOLD_POISON) return RadiationLevel.POISON;
        if (radiationDose >= THRESHOLD_SLOWNESS) return RadiationLevel.SLOWNESS;
        if (radiationDose >= THRESHOLD_WEAKNESS) return RadiationLevel.WEAKNESS;
        return RadiationLevel.NONE;
    }
    
    /**
     * Применяет эффекты радиации к игроку
     */
    public void applyEffects(EntityPlayer player) {
        RadiationLevel level = getRadiationLevel();
        
        switch (level) {
            case WEAKNESS:
                player.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.WEAKNESS, 100, 0));
                if (player.world.rand.nextInt(5) == 0) {
                    player.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.NAUSEA, 100, 0));
                }
                break;
            case SLOWNESS:
                player.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.WEAKNESS, 100, 1));
                player.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.SLOWNESS, 100, 0));
                player.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.HUNGER, 100, 0));
                break;
            case POISON:
                player.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.WEAKNESS, 100, 2));
                player.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.SLOWNESS, 100, 1));
                player.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.POISON, 100, 0));
                break;
            case WITHER:
                player.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.WITHER, 100, 0));
                player.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.BLINDNESS, 100, 0));
                break;
            case DEADLY:
                player.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.WITHER, 100, 1));
                // Шанс мгновенной смерти
                if (player.world.rand.nextInt(100) < 5) {
                    player.attackEntityFrom(RadiationDamageSource.RADIATION, Float.MAX_VALUE);
                }
                break;
            default:
                break;
        }
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, net.minecraft.util.EnumFacing facing) {
        return capability == RADIATION_CAPABILITY;
    }
    
    @Override
    public <T> T getCapability(Capability<T> capability, net.minecraft.util.EnumFacing facing) {
        return capability == RADIATION_CAPABILITY ? RADIATION_CAPABILITY.cast(this) : null;
    }
    
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setDouble("radiationDose", radiationDose);
        nbt.setDouble("radiationResistance", radiationResistance);
        return nbt;
    }
    
    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.radiationDose = nbt.getDouble("radiationDose");
        this.radiationResistance = nbt.getDouble("radiationResistance");
    }
    
    public enum RadiationLevel {
        NONE("Safe", TextFormatting.GREEN),
        WEAKNESS("Elevated", TextFormatting.YELLOW),
        SLOWNESS("Dangerous", TextFormatting.GOLD),
        POISON("Hazardous", TextFormatting.RED),
        WITHER("Critical", TextFormatting.DARK_RED),
        DEADLY("Lethal", TextFormatting.DARK_PURPLE);
        
        private final String displayName;
        private final TextFormatting color;
        
        RadiationLevel(String displayName, TextFormatting color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public TextFormatting getColor() {
            return color;
        }
    }
}
