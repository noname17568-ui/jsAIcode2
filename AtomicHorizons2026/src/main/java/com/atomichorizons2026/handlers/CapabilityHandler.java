package com.atomichorizons2026.handlers;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.heat.HeatCapability;
import com.atomichorizons2026.heat.IHeatHandler;
import com.atomichorizons2026.radiation.PlayerRadiationCapability;
import com.atomichorizons2026.radiation.RadiationCapabilityFactory;
import com.atomichorizons2026.radiation.RadiationCapabilityProvider;
import com.atomichorizons2026.radiation.RadiationCapabilityStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handles registration and attachment of capabilities.
 * Manages both radiation and heat capabilities.
 */
@Mod.EventBusSubscriber(modid = AtomicHorizons2026.MODID)
public class CapabilityHandler {
    
    private static final ResourceLocation RADIATION_CAP = new ResourceLocation(AtomicHorizons2026.MODID, "radiation");
    
    /**
     * Register all capabilities during preInit.
     * This must be called before any capability is used.
     */
    public static void registerCapabilities() {
        // Register radiation capability
        CapabilityManager.INSTANCE.register(
            PlayerRadiationCapability.class,
            new RadiationCapabilityStorage(),
            new RadiationCapabilityFactory()
        );
        
        // Register heat capability
        CapabilityManager.INSTANCE.register(
            IHeatHandler.class,
            new HeatCapability.Storage(),
            new HeatCapability.Factory()
        );
        
        AtomicHorizons2026.LOGGER.info("Registered PlayerRadiationCapability");
        AtomicHorizons2026.LOGGER.info("Registered HeatCapability");
    }
    
    /**
     * Attach radiation capability to all players.
     */
    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(RADIATION_CAP, new RadiationCapabilityProvider());
        }
    }
    
    /**
     * Clone capability data when player respawns or returns from End.
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // On death, reset radiation (optional - can be changed to preserve)
            // Currently resets to 0 on death
            EntityPlayer player = event.getEntityPlayer();
            EntityPlayer oldPlayer = event.getOriginal();
            
            PlayerRadiationCapability oldCap = oldPlayer.getCapability(
                PlayerRadiationCapability.RADIATION_CAPABILITY, null);
            PlayerRadiationCapability newCap = player.getCapability(
                PlayerRadiationCapability.RADIATION_CAPABILITY, null);
            
            if (oldCap != null && newCap != null) {
                // Preserve 50% of radiation on death (realistic decay)
                newCap.setRadiationDose(oldCap.getRadiationDose() * 0.5);
            }
        }
    }
}
