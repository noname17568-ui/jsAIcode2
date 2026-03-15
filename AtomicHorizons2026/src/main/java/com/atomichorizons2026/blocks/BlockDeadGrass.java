package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.radiation.RadiationManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

/**
 * Мёртвая трава (Dead Grass) - трава, убитая радиацией.
 * Появляется в местах с высокой радиацией.
 */
public class BlockDeadGrass extends Block {
    
    public BlockDeadGrass() {
        super(Material.GRASS);
        setTranslationKey("dead_grass");
        setRegistryName(AtomicHorizons2026.MODID, "dead_grass");
        setHardness(0.6f);
        setResistance(0.6f);
        setLightLevel(0.0f);
    }
    
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, java.util.Random rand) {
        if (world.isRemote) return;
        
        // Проверяем радиацию
        ChunkPos chunkPos = new ChunkPos(pos);
        double radiation = RadiationManager.getInstance().getChunkRadiation(world, chunkPos);
        
        // Если радиация упала, трава может восстановиться
        if (radiation < 50) {
            // Восстанавливаем траву
            if (rand.nextInt(10) == 0) {
                world.setBlockState(pos, Blocks.GRASS.getDefaultState());
            }
        }
        
        // Если радиация высокая, распространяем мёртвую траву
        if (radiation > 200) {
            if (rand.nextInt(5) == 0) {
                BlockPos neighbor = pos.add(
                    rand.nextInt(3) - 1,
                    rand.nextInt(3) - 1,
                    rand.nextInt(3) - 1
                );
                
                IBlockState neighborState = world.getBlockState(neighbor);
                if (neighborState.getBlock() == Blocks.GRASS) {
                    world.setBlockState(neighbor, this.getDefaultState());
                }
            }
        }
    }
}
