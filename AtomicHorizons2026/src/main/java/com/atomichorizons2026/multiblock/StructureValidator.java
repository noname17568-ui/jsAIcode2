package com.atomichorizons2026.multiblock;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Validates and caches multiblock structures.
 * Prevents expensive validation every tick.
 */
public class StructureValidator {
    
    // Cache: World -> Position -> CachedValidation
    private static final Map<World, Map<BlockPos, CachedValidation>> validationCache = 
        new WeakHashMap<>();
    
    // Cache duration in ticks (20 ticks = 1 second)
    private static final int CACHE_DURATION = 20;
    
    /**
     * Validates a multiblock structure with caching.
     * @param world World
     * @param pos Controller position
     * @param pattern Pattern to validate
     * @param facing Facing direction
     * @param forceRevalidate Force revalidation (ignore cache)
     * @return Validation result
     */
    public static MultiblockPattern.ValidationResult validate(World world, BlockPos pos, 
                                                              MultiblockPattern pattern, 
                                                              @Nullable EnumFacing facing,
                                                              boolean forceRevalidate) {
        if (!forceRevalidate) {
            // Check cache
            CachedValidation cached = getCachedValidation(world, pos);
            if (cached != null && !cached.isExpired(world.getTotalWorldTime())) {
                return cached.result;
            }
        }
        
        // Perform validation
        MultiblockPattern.ValidationResult result = pattern.validate(world, pos, facing);
        
        // Cache result
        cacheValidation(world, pos, result, world.getTotalWorldTime());
        
        return result;
    }
    
    /**
     * Invalidates cache for a specific position.
     * Call this when blocks change near the structure.
     */
    public static void invalidateCache(World world, BlockPos pos) {
        Map<BlockPos, CachedValidation> worldCache = validationCache.get(world);
        if (worldCache != null) {
            worldCache.remove(pos);
        }
    }
    
    /**
     * Invalidates cache for an area around a position.
     * @param radius Radius in blocks
     */
    public static void invalidateCacheArea(World world, BlockPos center, int radius) {
        Map<BlockPos, CachedValidation> worldCache = validationCache.get(world);
        if (worldCache == null) return;
        
        // Remove all cached validations within radius
        worldCache.entrySet().removeIf(entry -> {
            BlockPos pos = entry.getKey();
            return pos.distanceSq(center) <= radius * radius;
        });
    }
    
    /**
     * Clears all cached validations for a world.
     */
    public static void clearCache(World world) {
        validationCache.remove(world);
    }
    
    private static CachedValidation getCachedValidation(World world, BlockPos pos) {
        Map<BlockPos, CachedValidation> worldCache = validationCache.get(world);
        if (worldCache == null) return null;
        return worldCache.get(pos);
    }
    
    private static void cacheValidation(World world, BlockPos pos, 
                                       MultiblockPattern.ValidationResult result, 
                                       long timestamp) {
        Map<BlockPos, CachedValidation> worldCache = validationCache.computeIfAbsent(
            world, k -> new HashMap<>());
        worldCache.put(pos, new CachedValidation(result, timestamp));
    }
    
    /**
     * Cached validation result with timestamp.
     */
    private static class CachedValidation {
        final MultiblockPattern.ValidationResult result;
        final long timestamp;
        
        CachedValidation(MultiblockPattern.ValidationResult result, long timestamp) {
            this.result = result;
            this.timestamp = timestamp;
        }
        
        boolean isExpired(long currentTime) {
            return currentTime - timestamp > CACHE_DURATION;
        }
    }
}
