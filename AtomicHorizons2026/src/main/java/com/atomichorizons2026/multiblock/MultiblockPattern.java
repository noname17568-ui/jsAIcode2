package com.atomichorizons2026.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a multiblock structure pattern.
 * Supports rotation, wildcards, and validation caching.
 */
public class MultiblockPattern {
    
    private final String id;
    private final int width;  // X dimension
    private final int height; // Y dimension
    private final int depth;  // Z dimension
    
    // Pattern data: [y][z][x] = block predicate
    private final BlockPredicate[][][] pattern;
    
    // Controller position relative to pattern origin
    private final BlockPos controllerPos;
    
    // Rotation support
    private final boolean supportsRotation;
    
    public MultiblockPattern(String id, int width, int height, int depth, 
                            BlockPos controllerPos, boolean supportsRotation) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.pattern = new BlockPredicate[height][depth][width];
        this.controllerPos = controllerPos;
        this.supportsRotation = supportsRotation;
    }
    
    /**
     * Sets a block requirement at the given position.
     */
    public void setBlock(int x, int y, int z, BlockPredicate predicate) {
        if (x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth) {
            pattern[y][z][x] = predicate;
        }
    }
    
    /**
     * Validates the structure at the given position.
     * @param world World
     * @param origin Origin position (usually controller position)
     * @param facing Facing direction for rotation
     * @return Validation result
     */
    public ValidationResult validate(World world, BlockPos origin, @Nullable EnumFacing facing) {
        ValidationResult result = new ValidationResult();
        result.valid = true;
        
        // Calculate offset from controller to pattern origin
        BlockPos patternOrigin = origin.subtract(controllerPos);
        
        // Validate each block in the pattern
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++) {
                    BlockPredicate predicate = pattern[y][z][x];
                    if (predicate == null) continue; // Air or don't care
                    
                    // Calculate world position (with rotation if needed)
                    BlockPos worldPos = calculateWorldPos(patternOrigin, x, y, z, facing);
                    IBlockState state = world.getBlockState(worldPos);
                    
                    if (!predicate.test(state)) {
                        result.valid = false;
                        result.invalidPositions.put(worldPos, predicate.getExpectedBlock());
                    } else {
                        result.validPositions.add(worldPos);
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Calculates world position with rotation applied.
     */
    private BlockPos calculateWorldPos(BlockPos origin, int x, int y, int z, @Nullable EnumFacing facing) {
        if (!supportsRotation || facing == null || facing == EnumFacing.NORTH) {
            return origin.add(x, y, z);
        }
        
        // Apply rotation based on facing
        int rotatedX = x;
        int rotatedZ = z;
        
        switch (facing) {
            case SOUTH:
                rotatedX = -x;
                rotatedZ = -z;
                break;
            case WEST:
                rotatedX = z;
                rotatedZ = -x;
                break;
            case EAST:
                rotatedX = -z;
                rotatedZ = x;
                break;
        }
        
        return origin.add(rotatedX, y, rotatedZ);
    }
    
    public String getId() {
        return id;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getDepth() {
        return depth;
    }
    
    public BlockPos getControllerPos() {
        return controllerPos;
    }
    
    /**
     * Validation result containing valid/invalid positions.
     */
    public static class ValidationResult {
        public boolean valid;
        public final java.util.List<BlockPos> validPositions = new java.util.ArrayList<>();
        public final Map<BlockPos, Block> invalidPositions = new HashMap<>();
        
        public boolean isValid() {
            return valid;
        }
        
        public java.util.List<BlockPos> getValidPositions() {
            return validPositions;
        }
        
        public Map<BlockPos, Block> getInvalidPositions() {
            return invalidPositions;
        }
    }
    
    /**
     * Predicate for testing if a block matches requirements.
     */
    public interface BlockPredicate {
        boolean test(IBlockState state);
        Block getExpectedBlock();
    }
    
    /**
     * Simple block predicate that matches a specific block.
     */
    public static class ExactBlockPredicate implements BlockPredicate {
        private final Block block;
        
        public ExactBlockPredicate(Block block) {
            this.block = block;
        }
        
        @Override
        public boolean test(IBlockState state) {
            return state.getBlock() == block;
        }
        
        @Override
        public Block getExpectedBlock() {
            return block;
        }
    }
    
    /**
     * Wildcard predicate that matches any of several blocks.
     */
    public static class WildcardPredicate implements BlockPredicate {
        private final Block[] blocks;
        
        public WildcardPredicate(Block... blocks) {
            this.blocks = blocks;
        }
        
        @Override
        public boolean test(IBlockState state) {
            Block block = state.getBlock();
            for (Block b : blocks) {
                if (block == b) return true;
            }
            return false;
        }
        
        @Override
        public Block getExpectedBlock() {
            return blocks.length > 0 ? blocks[0] : null;
        }
    }
}
