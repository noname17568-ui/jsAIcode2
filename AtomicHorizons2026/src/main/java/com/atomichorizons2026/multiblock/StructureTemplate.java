package com.atomichorizons2026.multiblock;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Template for defining multiblock structures.
 * Provides a fluent API for pattern creation.
 */
public class StructureTemplate {
    
    private final MultiblockPattern pattern;
    private int currentY = 0;
    
    private StructureTemplate(String id, int width, int height, int depth, 
                             BlockPos controllerPos, boolean supportsRotation) {
        this.pattern = new MultiblockPattern(id, width, height, depth, controllerPos, supportsRotation);
    }
    
    /**
     * Creates a new structure template builder.
     */
    public static Builder builder(String id) {
        return new Builder(id);
    }
    
    /**
     * Sets the current layer (Y level) for adding blocks.
     */
    public StructureTemplate layer(int y) {
        this.currentY = y;
        return this;
    }
    
    /**
     * Adds a block at the specified position in the current layer.
     */
    public StructureTemplate block(int x, int z, Block block) {
        pattern.setBlock(x, currentY, z, new MultiblockPattern.ExactBlockPredicate(block));
        return this;
    }
    
    /**
     * Adds a wildcard block (matches any of the given blocks).
     */
    public StructureTemplate wildcard(int x, int z, Block... blocks) {
        pattern.setBlock(x, currentY, z, new MultiblockPattern.WildcardPredicate(blocks));
        return this;
    }
    
    /**
     * Adds a row of blocks in the current layer.
     */
    public StructureTemplate row(int z, Block... blocks) {
        for (int x = 0; x < blocks.length; x++) {
            if (blocks[x] != null) {
                block(x, z, blocks[x]);
            }
        }
        return this;
    }
    
    /**
     * Gets the built pattern.
     */
    public MultiblockPattern build() {
        return pattern;
    }
    
    /**
     * Builder for structure templates.
     */
    public static class Builder {
        private final String id;
        private int width = 3;
        private int height = 3;
        private int depth = 3;
        private BlockPos controllerPos = BlockPos.ORIGIN;
        private boolean supportsRotation = true;
        
        private Builder(String id) {
            this.id = id;
        }
        
        public Builder size(int width, int height, int depth) {
            this.width = width;
            this.height = height;
            this.depth = depth;
            return this;
        }
        
        public Builder controllerAt(int x, int y, int z) {
            this.controllerPos = new BlockPos(x, y, z);
            return this;
        }
        
        public Builder noRotation() {
            this.supportsRotation = false;
            return this;
        }
        
        public StructureTemplate create() {
            return new StructureTemplate(id, width, height, depth, controllerPos, supportsRotation);
        }
    }
}
