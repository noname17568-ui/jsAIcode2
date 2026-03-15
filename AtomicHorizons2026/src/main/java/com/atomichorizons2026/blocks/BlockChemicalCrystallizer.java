package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Chemical Crystallizer (Tier 2)
 * Crystallizes dissolved materials from solutions through evaporation.
 * 
 * Features:
 * - Fluid inputs
 * - Item outputs (crystals)
 * - Temperature control (heating)
 * - Energy consumption
 */
public class BlockChemicalCrystallizer extends BlockBase {
    
    public BlockChemicalCrystallizer() {
        super("chemical_crystallizer", Material.IRON, 5.0f, 2);
        setCreativeTab(AtomicHorizons2026.CREATIVE_TAB);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new com.atomichorizons2026.tileentities.TileEntityChemicalCrystallizer();
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                   EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof com.atomichorizons2026.tileentities.TileEntityChemicalCrystallizer) {
                // TODO: Open GUI when GUI system is implemented
                player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    "Chemical Crystallizer (GUI not yet implemented)"));
            }
        }
        return true;
    }
}
