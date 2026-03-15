package com.atomichorizons2026.worldgen;

import com.atomichorizons2026.handlers.RegistryHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class OreWorldGen implements IWorldGenerator {
    
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) { // Overworld only
            generateOverworld(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }
    }
    
    private void generateOverworld(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        // Uranium Ore - rare, deep underground
        runGenerator(
            RegistryHandler.ORE_URANIUM.getDefaultState(),
            4,  // vein size
            3,  // chances per chunk
            5,  // min height
            30, // max height
            BlockMatcher.forBlock(Blocks.STONE),
            world, random, chunkX, chunkZ
        );
        
        // Thorium Ore (Monazite) - in sand/clay areas, more common
        runGenerator(
            RegistryHandler.ORE_THORIUM.getDefaultState(),
            6,  // vein size
            5,  // chances per chunk
            20, // min height
            60, // max height
            BlockMatcher.forBlock(Blocks.STONE),
            world, random, chunkX, chunkZ
        );
        
        // Zirconium Ore - medium rarity
        runGenerator(
            RegistryHandler.ORE_ZIRCONIUM.getDefaultState(),
            5,  // vein size
            4,  // chances per chunk
            10, // min height
            45, // max height
            BlockMatcher.forBlock(Blocks.STONE),
            world, random, chunkX, chunkZ
        );
    }
    
    private void runGenerator(IBlockState blockToGen, int blockAmount, int chancesToSpawn, int minHeight, int maxHeight, BlockMatcher blockToReplace, World world, Random rand, int chunk_X, int chunk_Z) {
        if (minHeight < 0 || maxHeight > 256 || minHeight > maxHeight) {
            throw new IllegalArgumentException("Illegal Height Arguments for WorldGenerator");
        }
        
        WorldGenMinable generator = new WorldGenMinable(blockToGen, blockAmount, blockToReplace);
        int heightdiff = maxHeight - minHeight + 1;
        
        for (int i = 0; i < chancesToSpawn; i++) {
            int x = chunk_X * 16 + rand.nextInt(16);
            int y = minHeight + rand.nextInt(heightdiff);
            int z = chunk_Z * 16 + rand.nextInt(16);
            
            generator.generate(world, rand, new BlockPos(x, y, z));
        }
    }
}
