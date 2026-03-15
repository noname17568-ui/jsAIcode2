package com.atomichorizons2026.radiation;

import java.util.concurrent.Callable;

/**
 * Factory for creating new instances of PlayerRadiationCapability.
 * Required by Forge's capability system.
 */
public class RadiationCapabilityFactory implements Callable<PlayerRadiationCapability> {
    
    @Override
    public PlayerRadiationCapability call() throws Exception {
        return new PlayerRadiationCapability();
    }
}
