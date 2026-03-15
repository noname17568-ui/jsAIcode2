package com.atomichorizons2026.reactor;

import com.atomichorizons2026.heat.IHeatHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Interface for nuclear reactor controllers.
 * Provides standard API for all reactor types.
 */
public interface IReactorController {
    
    /**
     * Gets the reactor state.
     */
    ReactorState getReactorState();
    
    /**
     * Gets the heat handler for the reactor.
     */
    @Nullable
    IHeatHandler getHeatHandler();
    
    /**
     * Checks if the multiblock structure is formed.
     */
    boolean isStructureFormed();
    
    /**
     * Validates the multiblock structure.
     * @param forceRevalidate Force revalidation (ignore cache)
     */
    void validateStructure(boolean forceRevalidate);
    
    /**
     * Starts the reactor.
     * @return True if started successfully
     */
    boolean startReactor();
    
    /**
     * Stops the reactor (controlled shutdown).
     */
    void stopReactor();
    
    /**
     * Triggers emergency shutdown (SCRAM).
     * @param reason Reason for SCRAM
     */
    void triggerScram(String reason);
    
    /**
     * Resets SCRAM state (manual intervention).
     */
    void resetScram();
    
    /**
     * Inserts fuel into the reactor.
     * @param amount Amount in kg
     * @param enrichment Enrichment level (0.0 to 1.0)
     * @return True if fuel was inserted
     */
    boolean insertFuel(double amount, double enrichment);
    
    /**
     * Removes spent fuel from the reactor.
     * @return True if fuel was removed
     */
    boolean removeFuel();
    
    /**
     * Sets control rod insertion level.
     * @param insertion 0.0 (fully withdrawn) to 1.0 (fully inserted)
     */
    void setControlRods(double insertion);
    
    /**
     * Gets the current power output in MW.
     */
    double getPowerOutput();
    
    /**
     * Gets the current temperature in Kelvin.
     */
    double getTemperature();
    
    /**
     * Gets the reactor type identifier.
     */
    String getReactorType();
    
    /**
     * Gets the world.
     */
    World getWorld();
    
    /**
     * Gets the controller position.
     */
    BlockPos getPos();
    
    /**
     * Gets all positions that are part of the multiblock.
     */
    List<BlockPos> getMultiblockPositions();
}
