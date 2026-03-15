package com.atomichorizons2026.fluids;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.radiation.PlayerRadiationCapability;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic; // <--- ВАЖНО: Импорт из Forge
import net.minecraftforge.fluids.Fluid;

import java.util.Random;

/**
 * Блок жидкости с расширенной функциональностью.
 */
public class BlockFluidCustom extends BlockFluidClassic { // <--- Наследуемся от Forge класса
    
    protected final FluidBase fluidBase;
    protected final DamageSource acidDamageSource;
    
    public BlockFluidCustom(FluidBase fluid) {
        super(fluid, getMaterialForFluid(fluid));
        this.fluidBase = fluid;
        
        setTranslationKey(fluid.getName());
        setRegistryName(AtomicHorizons2026.MODID, fluid.getName());
        
        // Создаем источник урона для кислот
        if (fluid.isAcid()) {
            this.acidDamageSource = new DamageSource("acid_burn").setDamageBypassesArmor().setDamageIsAbsolute();
        } else {
            this.acidDamageSource = null;
        }
        
        // Настройки блока
        if (fluid.isGas()) {
            setLightOpacity(0);
        } else {
            setLightOpacity(3);
        }
        
        if (fluid.getLuminosity() > 0) {
            setLightLevel(fluid.getLuminosity() / 15.0f);
        }
    }
    
    private static Material getMaterialForFluid(FluidBase fluid) {
        if (fluid.isGas()) {
            return Material.AIR;
        }
        return Material.WATER;
    }
    
    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        super.onEntityCollision(world, pos, state, entity);
        
        if (world.isRemote) return;
        if (!(entity instanceof EntityLivingBase)) return;
        
        EntityLivingBase living = (EntityLivingBase) entity;
        
        // Обработка кислот
        if (fluidBase.isAcid() && acidDamageSource != null) {
            if (world.getTotalWorldTime() % 20 == 0) {
                living.attackEntityFrom(acidDamageSource, fluidBase.getAcidDamage());
                
                if (fluidBase.getName().contains("hydrofluoric")) {
                    living.addPotionEffect(new PotionEffect(MobEffects.WITHER, 60, 0));
                } else if (fluidBase.getName().contains("sulfuric")) {
                    living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 1));
                } else if (fluidBase.getName().contains("nitric")) {
                    living.addPotionEffect(new PotionEffect(MobEffects.POISON, 60, 1));
                }
            }
        }
        
        // Обработка радиоактивных жидкостей
        if (fluidBase.isRadioactive() && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            
            if (world.getTotalWorldTime() % 20 == 0) {
                PlayerRadiationCapability cap = player.getCapability(
                    PlayerRadiationCapability.RADIATION_CAPABILITY, null);
                
                if (cap != null) {
                    cap.addRadiation(fluidBase.getRadiationLevel());
                }
            }
        }
        
        // Обработка горячих жидкостей
        if (fluidBase.getFluidType() == FluidBase.FluidType.LIQUID_METAL) {
            if (world.getTotalWorldTime() % 20 == 0) {
                living.setFire(2);
            }
        }
    }
    
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
        
        if (fluidBase.isAcid() && rand.nextInt(10) == 0) {
            worldIn.spawnParticle(EnumParticleTypes.SPELL_MOB, 
                pos.getX() + rand.nextDouble(), 
                pos.getY() + rand.nextDouble() * 0.5, 
                pos.getZ() + rand.nextDouble(), 
                0.0, 0.1, 0.0);
        }
        
        if (fluidBase.isRadioactive() && rand.nextInt(20) == 0) {
            worldIn.spawnParticle(EnumParticleTypes.END_ROD, 
                pos.getX() + rand.nextDouble(), 
                pos.getY() + rand.nextDouble(), 
                pos.getZ() + rand.nextDouble(), 
                0.0, 0.05, 0.0);
        }
        
        if (fluidBase.getFluidType() == FluidBase.FluidType.LIQUID_METAL && rand.nextInt(5) == 0) {
            worldIn.spawnParticle(EnumParticleTypes.LAVA, 
                pos.getX() + rand.nextDouble(), 
                pos.getY() + 0.8, 
                pos.getZ() + rand.nextDouble(), 
                0.0, 0.1, 0.0);
        }
    }
    
    public FluidBase getFluidBase() {
        return fluidBase;
    }
    
    public boolean isAcid() {
        return fluidBase.isAcid();
    }
    
    public boolean isRadioactive() {
        return fluidBase.isRadioactive();
    }
}