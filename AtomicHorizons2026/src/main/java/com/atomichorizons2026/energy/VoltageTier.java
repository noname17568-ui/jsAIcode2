package com.atomichorizons2026.energy;

/**
 * Voltage tier definitions per TZ Section 3.1.
 * Each tier has max RF/t, loss per block, and color coding.
 */
public enum VoltageTier {

    LV("Low Voltage", 256, 1024, 0.005f, 0xFFFF00),
    MV("Medium Voltage", 1024, 4096, 0.002f, 0xFF8800),
    HV("High Voltage", 4096, 16384, 0.001f, 0xFF0000),
    EV("Extreme Voltage", 16384, 65536, 0.0005f, 0xFF00FF),
    UV("Ultimate Voltage", 65536, 262144, 0.0f, 0x00FFFF);

    private final String displayName;
    private final int maxRFPerTick;
    private final int maxRFBuffer;
    private final float lossPerBlock;
    private final int color;

    VoltageTier(String displayName, int maxRFPerTick, int maxRFBuffer, float lossPerBlock, int color) {
        this.displayName = displayName;
        this.maxRFPerTick = maxRFPerTick;
        this.maxRFBuffer = maxRFBuffer;
        this.lossPerBlock = lossPerBlock;
        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public int getMaxRFPerTick() { return maxRFPerTick; }
    public int getMaxRFBuffer() { return maxRFBuffer; }
    public float getLossPerBlock() { return lossPerBlock; }
    public int getColor() { return color; }

    /**
     * Calculate energy after transmission loss over distance.
     */
    public int calculateLoss(int energy, int distance) {
        if (lossPerBlock <= 0) return energy;
        float totalLoss = lossPerBlock * distance;
        return (int)(energy * (1.0f - Math.min(1.0f, totalLoss)));
    }

    /**
     * Get tier by max RF/t capacity.
     */
    public static VoltageTier getTierForEnergy(int rfPerTick) {
        for (VoltageTier tier : values()) {
            if (rfPerTick <= tier.maxRFPerTick) return tier;
        }
        return UV;
    }
}
