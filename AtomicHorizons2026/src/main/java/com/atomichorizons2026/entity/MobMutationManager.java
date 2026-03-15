package com.atomichorizons2026.entity;

import com.atomichorizons2026.radiation.RadiationManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

/**
 * Менеджер мутаций мобов под воздействием радиации.
 * Мобы получают эффекты и изменения в зависимости от уровня радиации.
 */
public class MobMutationManager {
    
    /**
     * Применяет мутации к мобу на основе радиации в чанке
     */
    public static void applyRadiationEffects(EntityLiving entity) {
        if (entity.world.isRemote) return;
        
        BlockPos pos = entity.getPosition();
        ChunkPos chunkPos = new ChunkPos(pos);
        double radiation = RadiationManager.getInstance().getChunkRadiation(entity.world, chunkPos);
        
        if (radiation <= 0) return;
        
        // Применяем эффекты в зависимости от уровня радиации
        if (radiation > 500) {
            applyHighRadiationEffects(entity, radiation);
        } else if (radiation > 200) {
            applyMediumRadiationEffects(entity, radiation);
        } else if (radiation > 50) {
            applyLowRadiationEffects(entity, radiation);
        }
    }
    
    /**
     * Низкая радиация (50-200 RAD/t)
     */
    private static void applyLowRadiationEffects(EntityLiving entity, double radiation) {
        // Зомби становятся немного быстрее
        if (entity instanceof EntityZombie) {
            entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 0, false, false));
        }
        
        // Скелеты стреляют быстрее (через эффект Haste)
        if (entity instanceof EntitySkeleton) {
            entity.addPotionEffect(new PotionEffect(MobEffects.HASTE, 100, 0, false, false));
        }
    }
    
    /**
     * Средняя радиация (200-500 RAD/t)
     */
    private static void applyMediumRadiationEffects(EntityLiving entity, double radiation) {
        // Зомби становятся сильнее и быстрее
        if (entity instanceof EntityZombie) {
            entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 0, false, false));
            entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 1, false, false));
            // Увеличиваем здоровье
            entity.setHealth(Math.min(entity.getMaxHealth() * 1.5f, entity.getMaxHealth()));
        }
        
        // Крипер становится более взрывчатым (больше урона)
        if (entity instanceof EntityCreeper) {
            entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 1, false, false));
        }
        
        // Скелеты получают регенерацию
        if (entity instanceof EntitySkeleton) {
            entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 0, false, false));
        }
    }
    
    /**
     * Высокая радиация (>500 RAD/t)
     */
    private static void applyHighRadiationEffects(EntityLiving entity, double radiation) {
        // Зомби становятся очень опасными
        if (entity instanceof EntityZombie) {
            entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 2, false, false));
            entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 2, false, false));
            entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1, false, false));
            entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 100, 0, false, false));
            // Максимальное здоровье
            entity.setHealth(entity.getMaxHealth() * 2);
        }
        
        // Крипер становится супер-взрывчатым
        if (entity instanceof EntityCreeper) {
            entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 2, false, false));
            entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 100, 1, false, false));
        }
        
        // Скелеты получают все баффы
        if (entity instanceof EntitySkeleton) {
            entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 1, false, false));
            entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1, false, false));
            entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 1, false, false));
        }
    }
}
