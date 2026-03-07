package com.atomichorizons2026.radiation;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Источник урона от радиации.
 */
public class RadiationDamageSource extends DamageSource {
    
    public static final RadiationDamageSource RADIATION = new RadiationDamageSource("radiation");
    public static final RadiationDamageSource ACUTE_RADIATION = new RadiationDamageSource("acute_radiation");
    
    private boolean isAcute = false;
    
    public RadiationDamageSource(String damageType) {
        super(damageType);
        this.setDamageBypassesArmor(); // Игнорирует броню (но не защиту от радиации!)
        this.setDamageIsAbsolute();
        this.setMagicDamage();
    }
    
    public RadiationDamageSource setAcute() {
        this.isAcute = true;
        return this;
    }
    
    public boolean isAcute() {
        return isAcute;
    }
    
    @Override
    public ITextComponent getDeathMessage(EntityLivingBase entity) {
        if (isAcute) {
            return new TextComponentTranslation("death.attack.acute_radiation", entity.getDisplayName());
        }
        return new TextComponentTranslation("death.attack.radiation", entity.getDisplayName());
    }
}
