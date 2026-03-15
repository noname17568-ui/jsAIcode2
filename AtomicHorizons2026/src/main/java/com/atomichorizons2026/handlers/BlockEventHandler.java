package com.atomichorizons2026.handlers;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.Logger;

/**
 * Event handler for player-related events.
 * Handles player login, logout, and respawn events.
 */
@Mod.EventBusSubscriber
public class BlockEventHandler {
    
    private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("atomichorizons2026");
    
    /**
     * Handle player login event.
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player == null) {
            return;
        }
        
        try {
            LOGGER.info("Player " + event.player.getName() + " logged in");
            // TODO: Initialize player radiation data
        } catch (Exception e) {
            LOGGER.error("Error handling player login", e);
        }
    }
    
    /**
     * Handle player logout event.
     */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player == null) {
            return;
        }
        
        try {
            LOGGER.info("Player " + event.player.getName() + " logged out");
            // TODO: Save player radiation data
        } catch (Exception e) {
            LOGGER.error("Error handling player logout", e);
        }
    }
    
    /**
     * Handle player respawn event.
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player == null) {
            return;
        }
        
        try {
            LOGGER.info("Player " + event.player.getName() + " respawned");
            // TODO: Reset player radiation effects on respawn
        } catch (Exception e) {
            LOGGER.error("Error handling player respawn", e);
        }
    }
}
