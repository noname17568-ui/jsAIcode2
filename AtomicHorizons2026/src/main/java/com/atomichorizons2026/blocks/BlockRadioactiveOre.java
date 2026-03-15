package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.radiation.IRadiationProtection;
import com.atomichorizons2026.radiation.IRadiationSource;
import com.atomichorizons2026.radiation.RadiationDamageSource;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Radioactive ore that emits radiation.
 * Deals radiation damage when mined without protection.
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
        // Use mod creative tab instead of BUILDING_BLOCKS
        this.setCreativeTab(AtomicHorizons2026.CREATIVE_TAB);
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
     * Deals radiation damage when mined without protection.
     */
    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        super.onBlockHarvested(world, pos, state, player);
        
        if (!world.isRemote && isRadioactive()) {
            // Check player protection
            boolean hasProtection = hasRadiationProtection(player);
            
            if (!hasProtection) {
                // Deal radiation damage
                float damage = (float) (radiationLevel * 0.1);
                player.attackEntityFrom(RadiationDamageSource.RADIATION, damage);
            }
        }
    }
    
    /**
     * Checks if entity has radiation protection.
     */
    private boolean hasRadiationProtection(EntityLivingBase entity) {
        // Check armor
        for (ItemStack armor : entity.getArmorInventoryList()) {
            if (!armor.isEmpty() && armor.getItem() instanceof IRadiationProtection) {
                double protection = ((IRadiationProtection) armor.getItem()).getRadiationProtection(armor, (EntityPlayer) entity);
                if (protection > 0.5) return true; // 50%+ protection is enough
            }
        }
        return false;
    }
}
