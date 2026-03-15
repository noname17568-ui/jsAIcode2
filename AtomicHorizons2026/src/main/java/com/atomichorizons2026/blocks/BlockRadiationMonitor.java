package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.radiation.RadiationManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

/**
 * Radiation Monitor Block - стационарный детектор радиации.
 * Подключается к Redstone и даёт сигнал при превышении порога.
 */
public class BlockRadiationMonitor extends Block {
    
    public BlockRadiationMonitor() {
        super(Material.IRON);
        setTranslationKey("radiation_monitor");
        setRegistryName(AtomicHorizons2026.MODID, "radiation_monitor");
        setHardness(5.0f);
        setResistance(10.0f);
        setLightLevel(0.5f);
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        
        // Получаем радиацию чанка
        ChunkPos chunkPos = new ChunkPos(pos);
        double radiation = RadiationManager.getInstance().getChunkRadiation(world, chunkPos);
        
        // Выводим информацию игроку
        player.sendMessage(new net.minecraft.util.text.TextComponentString(
            net.minecraft.util.text.TextFormatting.AQUA + 
            String.format("Radiation: %.2f mRAD/t", radiation)));
        
        return true;
    }
    
    @Override
    public int getWeakPower(IBlockState blockState, net.minecraft.world.IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        World world = (World) blockAccess;
        ChunkPos chunkPos = new ChunkPos(pos);
        double radiation = RadiationManager.getInstance().getChunkRadiation(world, chunkPos);
        
        // Преобразуем радиацию в сигнал Redstone (0-15)
        // Максимум 1000 RAD = 15 сигнал
        int signal = Math.min(15, (int)(radiation / 1000.0 * 15.0));
        return signal;
    }
    
    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }
    
    @Override
    public int getStrongPower(IBlockState blockState, net.minecraft.world.IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return getWeakPower(blockState, blockAccess, pos, side);
    }
}
