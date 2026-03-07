package com.atomichorizons2026.blocks;

import com.atomichorizons2026.handlers.RegistryHandler;
import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Блок Кориума (Corium) — расплавленное ядерное топливо.
 * Прожигает землю до бедрока, наносит радиационный урон.
 * Эмулирует "China Syndrome" — расплавление реактора.
 */
public class BlockCorium extends Block {
    
    // Таймер для обновления (в тиках)
    private static final int BURN_TICK_RATE = 20; // Раз в секунду
    
    public BlockCorium(String name) {
        super(Material.LAVA); // Используем материал лавы для поведения
        this.setTranslationKey(name);
        this.setRegistryName(AtomicHorizons2026.MODID, name);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        
        // Характеристики блока
        this.setHardness(-1.0F); // Неразрушим обычными инструментами
        this.setResistance(6000000.0F); // Как бедрок
        this.setLightLevel(1.0F); // Максимальная яркость
        this.setTickRandomly(true); // Случайные тики для вариативности
        
        // Блок движется как песок (падает вниз)
        // this.setDefaultState(this.blockState.getBaseState());
    }
    
    /**
     * Вызывается каждый случайный тик блока.
     * Основная логика прожигания земли.
     */
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) return;
        
        // Проверяем блок под кориумом
        BlockPos belowPos = pos.down();
        IBlockState belowState = world.getBlockState(belowPos);
        Block belowBlock = belowState.getBlock();
        
        // Если под нами бедрок или воздух — не падаем
        if (belowBlock == Blocks.BEDROCK || belowBlock == Blocks.AIR) {
            return;
        }
        
        // Если под нами другой кориум — тоже не падаем (формируем столб)
        if (belowBlock instanceof BlockCorium) {
            return;
        }
        
        // ПРОЖИГАНИЕ: Уничтожаем блок под нами
        // Не прожигаем: бедрок, барьер, воздух, другой кориум
        if (canBurnThrough(belowBlock)) {
            // Уничтожаем блок с эффектами
            world.destroyBlock(belowPos, false); // false = не дропаем предметы
            
            // Создаем эффекты частиц
            spawnBurnEffects(world, belowPos);
            
            // Перемещаем кориум вниз
            world.setBlockToAir(pos);
            world.setBlockState(belowPos, this.getDefaultState(), 3);
            
            // Наносим урон существам в радиусе
            damageEntitiesInRadius(world, belowPos, 3.0);
            
            AtomicHorizons2026.LOGGER.debug("Corium burned through at " + belowPos);
        }
        
        // Запланировать следующий тик
        world.scheduleUpdate(pos, this, BURN_TICK_RATE + rand.nextInt(10));
    }
    
    /**
     * Проверяет, можно ли прожечь блок
     */
    private boolean canBurnThrough(Block block) {
        if (block == Blocks.BEDROCK) return false;
        if (block == Blocks.BARRIER) return false;
        if (block == Blocks.AIR) return false;
        if (block instanceof BlockCorium) return false;
        if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) return false; // Пар
        if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) return false;
        
        return true;
    }
    
    /**
     * Создает эффекты частиц при прожигании
     */
    @SideOnly(Side.CLIENT)
    private void spawnBurnEffects(World world, BlockPos pos) {
        // Дым
        for (int i = 0; i < 10; i++) {
            double x = pos.getX() + 0.5 + (world.rand.nextDouble() - 0.5) * 1.5;
            double y = pos.getY() + 0.5 + world.rand.nextDouble() * 0.5;
            double z = pos.getZ() + 0.5 + (world.rand.nextDouble() - 0.5) * 1.5;
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 0, 0.1, 0);
        }
        
        // Огонь
        for (int i = 0; i < 5; i++) {
            double x = pos.getX() + 0.5 + (world.rand.nextDouble() - 0.5);
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5 + (world.rand.nextDouble() - 0.5);
            world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0.05, 0);
        }
        
        // Лава (искры)
        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.8;
            double z = pos.getZ() + 0.5;
            world.spawnParticle(EnumParticleTypes.LAVA, x, y, z, 0, 0, 0);
        }
    }
    
    /**
     * Наносит урон существам в радиусе
     */
    private void damageEntitiesInRadius(World world, BlockPos pos, double radius) {
        for (Entity entity : world.getEntitiesWithinAABB(Entity.class, 
                new net.minecraft.util.math.AxisAlignedBB(
                    pos.add(-radius, -radius, -radius),
                    pos.add(radius, radius, radius)
                ))) {
            
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) entity;
                
                // Наносим урон от радиации
                living.attackEntityFrom(DamageSource.IN_FIRE, 10.0F);
                living.setFire(10); // Поджигаем на 10 секунд
                
                // Накладываем эффект радиации (если есть)
                if (RegistryHandler.POTION_RADIATION != null) {
                    living.addPotionEffect(new PotionEffect(RegistryHandler.POTION_RADIATION, 600, 1));
                }
            }
        }
    }
    
    /**
     * Вызывается при размещении блока
     */
    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (!world.isRemote) {
            world.scheduleUpdate(pos, this, BURN_TICK_RATE);
        }
    }
    
    /**
     * Сущности получают урон при касании
     */
    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (!world.isRemote && entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;
            living.attackEntityFrom(DamageSource.IN_FIRE, 5.0F);
            living.setFire(5);
        }
        super.onEntityWalk(world, pos, entity);
    }
    
    /**
     * Блок горит вечно (для визуальных эффектов)
     */
    @Override
    public int getLightValue(IBlockState state) {
        return 15; // Максимальная яркость
    }
    
    /**
     * Не даем блоку быть замененным другими жидкостями
     */
    @Override
    public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        return false;
    }
    
    /**
     * Блок не может быть сдвинут поршнем
     */
    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }
    
    /**
     * Визуальные эффекты на клиенте
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        // Постоянное тепловое мерцание
        if (rand.nextInt(5) == 0) {
            double x = pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 0.8;
            double y = pos.getY() + 0.8 + rand.nextDouble() * 0.3;
            double z = pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 0.8;
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0.05, 0);
        }
        
        // Искры
        if (rand.nextInt(10) == 0) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;
            world.spawnParticle(EnumParticleTypes.LAVA, x, y, z, 0, 0.1, 0);
        }
    }
    
    /**
     * Блок не может быть сломан обычными инструментами
     */
    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return false;
    }
    
    /**
     * Не дропаем ничего при разрушении
     */
    @Override
    public int quantityDropped(Random random) {
        return 0;
    }
    
    /**
     * Блок не может быть подожжен
     */
    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 0;
    }
    
    /**
     * Блок не горит
     */
    @Override
    public boolean isBurning(IBlockAccess world, BlockPos pos) {
        return true; // Всегда "горит" для визуальных эффектов
    }
}
