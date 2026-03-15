package com.atomichorizons2026.radiation;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * Handles environmental effects of radiation on the world.
 * Per TZ Section 6.2: Environmental Effects
 * - Grass -> Dead Grass (Dirt) at >100 RAD/chunk
 * - Leaves -> Withered (destroyed) at >200 RAD/chunk
 * - Water -> Contaminated at >500 RAD/chunk
 * - Crops destroyed at >150 RAD/chunk
 */
@Mod.EventBusSubscriber
public class RadiationEnvironmentHandler {

    private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("atomichorizons2026");

    // Radiation thresholds for environmental effects
    public static final double THRESHOLD_GRASS_DEATH = 100.0;
    public static final double THRESHOLD_CROP_DEATH = 150.0;
    public static final double THRESHOLD_LEAF_WITHER = 200.0;
    public static final double THRESHOLD_WATER_CONTAMINATION = 500.0;

    // How many blocks to process per tick per chunk (performance)
    private static final int BLOCKS_PER_TICK = 3;

    /**
     * Process environmental radiation effects each world tick.
     * Only runs every 100 ticks (5 seconds) for performance.
     * Uses world time instead of static counter to avoid dimension conflicts.
     */
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.world.isRemote) return;

        if (event.world.getTotalWorldTime() % 100 != 0) return;

        World world = event.world;
        RadiationManager radManager = RadiationManager.getInstance();
        Random rand = world.rand;

        try {
            // Process loaded chunks with radiation
            for (net.minecraft.entity.player.EntityPlayer player : world.playerEntities) {
                ChunkPos playerChunk = new ChunkPos(player.getPosition());

                // Check 3x3 area around player
                for (int cx = -1; cx <= 1; cx++) {
                    for (int cz = -1; cz <= 1; cz++) {
                        ChunkPos checkPos = new ChunkPos(playerChunk.x + cx, playerChunk.z + cz);
                        double radiation = radManager.getChunkRadiation(world, checkPos);

                        if (radiation >= THRESHOLD_GRASS_DEATH) {
                            processChunkRadiation(world, checkPos, radiation, rand);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error processing environmental radiation", e);
        }
    }

    /**
     * Process radiation effects for a single chunk.
     */
    private static void processChunkRadiation(World world, ChunkPos chunkPos, double radiation, Random rand) {
        int baseX = chunkPos.x * 16;
        int baseZ = chunkPos.z * 16;

        for (int i = 0; i < BLOCKS_PER_TICK; i++) {
            int x = baseX + rand.nextInt(16);
            int z = baseZ + rand.nextInt(16);
            int y = world.getHeight(x, z);

            BlockPos pos = new BlockPos(x, y, z);
            BlockPos below = pos.down();
            IBlockState stateBelow = world.getBlockState(below);
            IBlockState stateAt = world.getBlockState(pos);
            Block blockBelow = stateBelow.getBlock();
            Block blockAt = stateAt.getBlock();

            // Grass -> Dirt (coarse dirt to look dead)
            if (radiation >= THRESHOLD_GRASS_DEATH) {
                if (blockBelow == Blocks.GRASS) {
                    world.setBlockState(below, Blocks.DIRT.getStateFromMeta(1)); // Coarse dirt
                }
            }

            // Crops destroyed
            if (radiation >= THRESHOLD_CROP_DEATH) {
                if (blockAt == Blocks.WHEAT || blockAt == Blocks.CARROTS ||
                    blockAt == Blocks.POTATOES || blockAt == Blocks.BEETROOTS ||
                    blockAt == Blocks.MELON_STEM || blockAt == Blocks.PUMPKIN_STEM) {
                    world.setBlockToAir(pos);
                }
                // Flowers die too
                if (blockAt == Blocks.RED_FLOWER || blockAt == Blocks.YELLOW_FLOWER ||
                    blockAt == Blocks.TALLGRASS || blockAt == Blocks.DOUBLE_PLANT) {
                    world.setBlockToAir(pos);
                }
            }

            // Leaves wither
            if (radiation >= THRESHOLD_LEAF_WITHER) {
                if (blockAt == Blocks.LEAVES || blockAt == Blocks.LEAVES2) {
                    world.setBlockToAir(pos);
                }
            }

            // Saplings die
            if (radiation >= THRESHOLD_CROP_DEATH) {
                if (blockAt == Blocks.SAPLING) {
                    world.setBlockToAir(pos);
                }
            }
        }
    }
}
