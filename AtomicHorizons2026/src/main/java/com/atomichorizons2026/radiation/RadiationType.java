package com.atomichorizons2026.radiation;

/**
 * Types of ionizing radiation with different properties.
 * Each type has different penetration and shielding requirements.
 */
public enum RadiationType {
    
    /**
     * Alpha radiation (α)
     * - Helium nuclei (2 protons + 2 neutrons)
     * - Low penetration (stopped by paper/skin)
     * - High ionization (dangerous if ingested)
     * - Blocked by: Any solid material
     */
    ALPHA(
        "Alpha",
        0.01,  // penetration (blocks)
        10.0,  // ionization power
        0.05,  // air absorption per block
        1.0    // biological effectiveness
    ),
    
    /**
     * Beta radiation (β)
     * - High-energy electrons or positrons
     * - Medium penetration (stopped by aluminum)
     * - Medium ionization
     * - Blocked by: Aluminum, plastic, glass
     */
    BETA(
        "Beta",
        0.5,   // penetration (blocks)
        5.0,   // ionization power
        0.1,   // air absorption per block
        1.0    // biological effectiveness
    ),
    
    /**
     * Gamma radiation (γ)
     * - High-energy photons
     * - High penetration (requires lead/concrete)
     * - Low ionization
     * - Blocked by: Lead, concrete, water
     */
    GAMMA(
        "Gamma",
        10.0,  // penetration (blocks)
        1.0,   // ionization power
        0.01,  // air absorption per block
        1.0    // biological effectiveness
    ),
    
    /**
     * Neutron radiation (n)
     * - Free neutrons
     * - Very high penetration
     * - Causes secondary radiation (activation)
     * - Blocked by: Water, concrete, boron
     */
    NEUTRON(
        "Neutron",
        15.0,  // penetration (blocks)
        2.0,   // ionization power
        0.005, // air absorption per block
        10.0   // biological effectiveness (very dangerous)
    );
    
    private final String displayName;
    private final double penetration; // Maximum penetration distance in blocks
    private final double ionizationPower; // Damage multiplier
    private final double airAbsorption; // Absorption per block of air
    private final double biologicalEffectiveness; // Relative biological effectiveness (RBE)
    
    RadiationType(String displayName, double penetration, double ionizationPower, 
                  double airAbsorption, double biologicalEffectiveness) {
        this.displayName = displayName;
        this.penetration = penetration;
        this.ionizationPower = ionizationPower;
        this.airAbsorption = airAbsorption;
        this.biologicalEffectiveness = biologicalEffectiveness;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public double getPenetration() {
        return penetration;
    }
    
    public double getIonizationPower() {
        return ionizationPower;
    }
    
    public double getAirAbsorption() {
        return airAbsorption;
    }
    
    public double getBiologicalEffectiveness() {
        return biologicalEffectiveness;
    }
    
    /**
     * Calculates radiation intensity after traveling through air.
     * @param distance Distance in blocks
     * @param initialIntensity Initial radiation intensity
     * @return Remaining intensity
     */
    public double calculateAirAttenuation(double distance, double initialIntensity) {
        // Exponential attenuation: I = I0 * e^(-μ * d)
        return initialIntensity * Math.exp(-airAbsorption * distance);
    }
    
    /**
     * Checks if this radiation type can penetrate a given distance.
     * @param distance Distance in blocks
     * @return True if can penetrate
     */
    public boolean canPenetrate(double distance) {
        return distance <= penetration;
    }
}
