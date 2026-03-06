package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.tileentities.TileEntitySMRCore;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class BlockSMRCore extends BlockBase {
    
    public BlockSMRCore(String name) {
        super(name, Material.IRON, 20.0f, 2);
        setCreativeTab(CreativeTabs.REDSTONE);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntitySMRCore();
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntitySMRCore) {
                TileEntitySMRCore smr = (TileEntitySMRCore) tile;
                IEnergyStorage energy = smr.getCapability(CapabilityEnergy.ENERGY, facing);
                if (energy != null) {
                    AtomicHorizons2026.LOGGER.info("SMR Energy: " + energy.getEnergyStored() + " / " + energy.getMaxEnergyStored());
                }
            }
        }
        return true;
    }
    
    public Class<? extends TileEntity> getTileEntityClass() {
        return TileEntitySMRCore.class;
    }
}
