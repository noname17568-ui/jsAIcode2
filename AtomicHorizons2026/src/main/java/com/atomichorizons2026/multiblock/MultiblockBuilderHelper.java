package com.atomichorizons2026.multiblock;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.Map;

/**
 * Helper for building multiblock structures.
 * Provides visual feedback and construction guidance.
 */
public class MultiblockBuilderHelper {
    
    /**
     * Sends validation feedback to a player.
     * Shows which blocks are correct/incorrect.
     */
    public static void sendValidationFeedback(EntityPlayer player, 
                                             MultiblockPattern.ValidationResult result,
                                             MultiblockPattern pattern) {
        if (result.isValid()) {
            player.sendMessage(new TextComponentString(
                TextFormatting.GREEN + "✓ Multiblock structure valid!"
            ));
            player.sendMessage(new TextComponentString(
                TextFormatting.GRAY + "Structure: " + pattern.getId()
            ));
            player.sendMessage(new TextComponentString(
                TextFormatting.GRAY + "Blocks: " + result.getValidPositions().size()
            ));
        } else {
            player.sendMessage(new TextComponentString(
                TextFormatting.RED + "✗ Multiblock structure invalid!"
            ));
            player.sendMessage(new TextComponentString(
                TextFormatting.GRAY + "Correct blocks: " + result.getValidPositions().size()
            ));
            player.sendMessage(new TextComponentString(
                TextFormatting.GRAY + "Incorrect blocks: " + result.getInvalidPositions().size()
            ));
            
            // Show first few incorrect blocks
            int shown = 0;
            for (Map.Entry<BlockPos, Block> entry : result.getInvalidPositions().entrySet()) {
                if (shown >= 3) {
                    player.sendMessage(new TextComponentString(
                        TextFormatting.GRAY + "... and " + 
                        (result.getInvalidPositions().size() - 3) + " more"
                    ));
                    break;
                }
                
                BlockPos pos = entry.getKey();
                Block expected = entry.getValue();
                player.sendMessage(new TextComponentString(
                    TextFormatting.YELLOW + "  " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + 
                    ": Expected " + (expected != null ? expected.getLocalizedName() : "specific block")
                ));
                shown++;
            }
        }
    }
    
    /**
     * Gets a user-friendly structure description.
     */
    public static String getStructureDescription(MultiblockPattern pattern) {
        return String.format("%s (%dx%dx%d)", 
            pattern.getId(),
            pattern.getWidth(),
            pattern.getHeight(),
            pattern.getDepth()
        );
    }
    
    /**
     * Checks if a player is within range to build/validate a structure.
     */
    public static boolean isPlayerInRange(EntityPlayer player, BlockPos pos, int maxDistance) {
        return player.getDistanceSq(pos) <= maxDistance * maxDistance;
    }
    
    /**
     * Calculates the total number of blocks in a pattern.
     */
    public static int getTotalBlocks(MultiblockPattern pattern) {
        return pattern.getWidth() * pattern.getHeight() * pattern.getDepth();
    }
}
