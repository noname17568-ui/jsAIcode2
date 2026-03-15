package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.radiation.RadiationManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

/**
 * Заражённая вода (Contaminated Water) - вода с высокой радиацией.
 * Наносит урон и эффекты игрокам и мобам, которые в неё входят.
 */
public class BlockContaminatedWater extends Block {
    
    public BlockContaminatedWater() {
        super(Material.WATER);
        setTranslationKey("contaminated_water");
        setRegistryName(AtomicHorizons2026.MODID, "contaminated_water");
        setHardness(-1.0f); // Как обычная вода
        setResistance(100.0f);
        setLightLevel(0.2f);
    }
    
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (world.isRemote) return;
        
        if (!(entity instanceof EntityLivingBase)) return;
        
        EntityLivingBase living = (EntityLivingBase) entity;
        
        // Получаем радиацию из чанка
        ChunkPos chunkPos = new ChunkPos(pos);
        double radiation = RadiationManager.getInstance().getChunkRadiation(world, chunkPos);
        
        // Если нет радиации, вода не опасна
        if (radiation <= 0) return;
        
        // Применяем эффекты в зависимости от радиации
        if (radiation > 500) {
            // Высокая радиация - наносим урон и даём Poison
            living.attackEntityFrom(com.atomichorizons2026.radiation.RadiationDamageSource.RADIATION, 2.0f);
            living.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 1, false, false));
            living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 100, 0, false, false));
        } else if (radiation > 200) {
            // Средняя радиация - Poison и Slowness
            living.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0, false, false));
            living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 0, false, false));
        } else if (radiation > 50) {
            // Низкая радиация - только Slowness
            living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 0, false, false));
        }
    }
    
    @Override
    public boolean isReplaceable(World world, BlockPos pos) {
        return true; // Вода заменяема
    }
}
