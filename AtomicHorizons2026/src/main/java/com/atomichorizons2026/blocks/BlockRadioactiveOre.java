package com.atomichorizons2026.blocks;

import com.atomichorizons2026.radiation.IRadiationSource;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Радиоактивная руда, излучающая радиацию.
 * Наносит урон при добыче без защиты.
 */
public class BlockRadioactiveOre extends BlockOre implements IRadiationSource {
    
    private final double radiationLevel;
    private final RadiationType radiationType;
    private final double radiationRadius;
    
    public BlockRadioactiveOre(String name, int harvestLevel, float hardness, int exp,
                                double radiationLevel, RadiationType radiationType, double radiationRadius) {
        super(name, harvestLevel, hardness, exp);
        this.radiationLevel = radiationLevel;
        this.radiationType = radiationType;
        this.radiationRadius = radiationRadius;
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
    
    @Override
    public double getRadiationLevel() {
        return radiationLevel;
    }
    
    @Override
    public RadiationType getRadiationType() {
        return radiationType;
    }
    
    @Override
    public double getRadiationRadius() {
        return radiationRadius;
    }
    
    @Override
    public boolean isRadioactive() {
        return radiationLevel > 0;
    }
    
    /**
     * При добыче наносит радиационный урон если нет защиты
     */
    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        super.onBlockHarvested(world, pos, state, player);
        
        if (!world.isRemote && isRadioactive()) {
            // Проверяем защиту игрока
            boolean hasProtection = hasRadiationProtection(player);
            
            if (!hasProtection) {
                // Наносим радиацию
                player.attackEntityFrom(
                    com.atomichorizons2026.radiation.RadiationDamageSource.RADIATION, 
                    (float) (radiationLevel * 0.1)
                );
            }
        }
    }
    
    /**
     * Проверяет, есть ли у игрока защита от радиации
     */
    private boolean hasRadiationProtection(EntityLivingBase entity) {
        // Проверяем броню
        for (ItemStack armor : entity.getArmorInventoryList()) {
            if (!armor.isEmpty() && armor.getItem() instanceof com.atomichorizons2026.radiation.RadiationManager.IRadiationProtection) {
                double protection = ((com.atomichorizons2026.radiation.RadiationManager.IRadiationProtection) armor.getItem())
                    .getRadiationProtection(armor);
                if (protection > 0.5) return true;
            }
        }
        return false;
    }
}
