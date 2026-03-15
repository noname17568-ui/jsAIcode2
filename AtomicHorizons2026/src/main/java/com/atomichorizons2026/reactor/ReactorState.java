package com.atomichorizons2026.reactor;

import com.atomichorizons2026.heat.IHeatHandler;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Represents the state of a nuclear reactor.
 * Tracks fuel, temperature, power output, and safety parameters.
 */
public class ReactorState {
    
    // Fuel state
    private double fuelAmount; // kg of fuel
    private double fuelEnrichment; // 0.0 to 1.0 (0% to 100%)
    private double burnup; // MWd/kg (megawatt-days per kilogram)
    
    // Operational state
    private boolean active;
    private double powerOutput; // MW (megawatts)
    private double controlRodInsertion; // 0.0 to 1.0 (0% = fully withdrawn, 100% = fully inserted)
    
    // Safety parameters
    private double reactivity; // Neutron multiplication factor (k)
    private double neutronFlux; // neutrons/cm²/s
    private boolean scram; // Emergency shutdown
    
    // Coolant state
    private double coolantFlow; // L/s (liters per second)
    private double coolantTemperature; // K (Kelvin)
    
    // Limits
    private static final double MAX_TEMPERATURE = 1200.0; // K (~927°C)
    private static final double MAX_POWER = 100.0; // MW
    private static final double CRITICAL_REACTIVITY = 1.0;
    private static final double MAX_BURNUP = 60000.0; // MWd/kg
    
    public ReactorState() {
        this.fuelAmount = 0.0;
        this.fuelEnrichment = 0.0;
        this.burnup = 0.0;
        this.active = false;
        this.powerOutput = 0.0;
        this.controlRodInsertion = 1.0; // Start fully inserted (safe)
        this.reactivity = 0.0;
        this.neutronFlux = 0.0;
        this.scram = false;
        this.coolantFlow = 0.0;
        this.coolantTemperature = 293.15; // Room temperature
    }
    
    /**
     * Updates reactor physics for one tick.
     * @param deltaTime Time step in seconds (typically 0.05 for 1 tick)
     * @param heatHandler Heat handler for temperature management
     */
    public void update(double deltaTime, IHeatHandler heatHandler) {
        if (scram) {
            // Emergency shutdown - insert all control rods
            controlRodInsertion = 1.0;
            active = false;
        }
        
        if (!active || fuelAmount <= 0) {
            // Reactor is off or no fuel
            powerOutput = 0.0;
            neutronFlux = 0.0;
            reactivity = 0.0;
            return;
        }
        
        // Calculate reactivity based on control rods and fuel
        calculateReactivity();
        
        // Update neutron flux
        updateNeutronFlux(deltaTime);
        
        // Calculate power output
        calculatePowerOutput();
        
        // Generate heat from fission
        generateHeat(deltaTime, heatHandler);
        
        // Consume fuel (burnup)
        consumeFuel(deltaTime);
        
        // Check safety limits
        checkSafetyLimits(heatHandler);
    }
    
    /**
     * Calculates reactivity (k) based on control rods and fuel enrichment.
     */
    private void calculateReactivity() {
        // Base reactivity from fuel enrichment
        double baseReactivity = 0.5 + (fuelEnrichment * 1.5); // 0.5 to 2.0
        
        // Control rod effect (exponential)
        double rodEffect = Math.exp(-5.0 * controlRodInsertion);
        
        // Burnup penalty (fuel depletes over time)
        double burnupPenalty = 1.0 - (burnup / MAX_BURNUP);
        
        reactivity = baseReactivity * rodEffect * burnupPenalty;
    }
    
    /**
     * Updates neutron flux based on reactivity.
     */
    private void updateNeutronFlux(double deltaTime) {
        if (reactivity > CRITICAL_REACTIVITY) {
            // Supercritical - flux increases
            double increase = (reactivity - 1.0) * 1e13 * deltaTime;
            neutronFlux += increase;
        } else if (reactivity < CRITICAL_REACTIVITY) {
            // Subcritical - flux decreases
            double decrease = (1.0 - reactivity) * neutronFlux * 0.1 * deltaTime;
            neutronFlux = Math.max(0, neutronFlux - decrease);
        }
        
        // Clamp flux
        neutronFlux = Math.max(0, Math.min(neutronFlux, 1e15));
    }
    
    /**
     * Calculates power output from neutron flux.
     */
    private void calculatePowerOutput() {
        // Power proportional to neutron flux
        // 1e14 neutrons/cm²/s ≈ 1 MW
        powerOutput = (neutronFlux / 1e14) * fuelAmount;
        powerOutput = Math.min(powerOutput, MAX_POWER);
    }
    
    /**
     * Generates heat from fission reactions.
     */
    private void generateHeat(double deltaTime, IHeatHandler heatHandler) {
        if (heatHandler == null) return;
        
        // Convert power to heat (1 MW = 1e6 J/s)
        double heatGenerated = powerOutput * 1e6 * deltaTime;
        
        // Add heat to reactor
        heatHandler.addHeat(heatGenerated, false);
    }
    
    /**
     * Consumes fuel based on power output (burnup).
     */
    private void consumeFuel(double deltaTime) {
        if (powerOutput <= 0) return;
        
        // Burnup rate: MWd/kg
        // 1 MW for 1 day (86400 seconds) = 1 MWd
        double burnupRate = (powerOutput * deltaTime) / 86400.0; // MWd
        double burnupPerKg = burnupRate / fuelAmount; // MWd/kg
        
        burnup += burnupPerKg;
        
        // Fuel is spent when burnup reaches limit
        if (burnup >= MAX_BURNUP) {
            fuelAmount = 0;
            active = false;
        }
    }
    
    /**
     * Checks safety limits and triggers SCRAM if needed.
     */
    private void checkSafetyLimits(IHeatHandler heatHandler) {
        if (heatHandler == null) return;
        
        double temperature = heatHandler.getTemperature();
        
        // Temperature limit
        if (temperature > MAX_TEMPERATURE) {
            triggerScram("Temperature exceeded limit");
        }
        
        // Reactivity limit
        if (reactivity > 1.5) {
            triggerScram("Reactivity too high");
        }
        
        // Coolant flow check
        if (active && coolantFlow < 1.0) {
            triggerScram("Insufficient coolant flow");
        }
    }
    
    /**
     * Triggers emergency shutdown (SCRAM).
     */
    public void triggerScram(String reason) {
        scram = true;
        active = false;
        // Log reason for debugging
        System.out.println("REACTOR SCRAM: " + reason);
    }
    
    /**
     * Resets SCRAM state (manual intervention required).
     */
    public void resetScram() {
        scram = false;
    }
    
    // Getters and setters
    
    public double getFuelAmount() {
        return fuelAmount;
    }
    
    public void setFuelAmount(double fuelAmount) {
        this.fuelAmount = Math.max(0, fuelAmount);
    }
    
    public double getFuelEnrichment() {
        return fuelEnrichment;
    }
    
    public void setFuelEnrichment(double enrichment) {
        this.fuelEnrichment = Math.max(0, Math.min(1.0, enrichment));
    }
    
    public double getBurnup() {
        return burnup;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        if (!scram) {
            this.active = active;
        }
    }
    
    public double getPowerOutput() {
        return powerOutput;
    }
    
    public double getControlRodInsertion() {
        return controlRodInsertion;
    }
    
    public void setControlRodInsertion(double insertion) {
        this.controlRodInsertion = Math.max(0, Math.min(1.0, insertion));
    }
    
    public double getReactivity() {
        return reactivity;
    }
    
    public double getNeutronFlux() {
        return neutronFlux;
    }
    
    public boolean isScram() {
        return scram;
    }
    
    public double getCoolantFlow() {
        return coolantFlow;
    }
    
    public void setCoolantFlow(double flow) {
        this.coolantFlow = Math.max(0, flow);
    }
    
    public double getCoolantTemperature() {
        return coolantTemperature;
    }
    
    public void setCoolantTemperature(double temperature) {
        this.coolantTemperature = temperature;
    }
    
    /**
     * Gets fuel remaining as percentage.
     */
    public double getFuelPercentage() {
        if (burnup >= MAX_BURNUP) return 0.0;
        return 1.0 - (burnup / MAX_BURNUP);
    }
    
    /**
     * Checks if reactor is critical (self-sustaining chain reaction).
     */
    public boolean isCritical() {
        return Math.abs(reactivity - CRITICAL_REACTIVITY) < 0.01;
    }
    
    /**
     * Checks if reactor is supercritical (power increasing).
     */
    public boolean isSupercritical() {
        return reactivity > CRITICAL_REACTIVITY;
    }
    
    /**
     * Checks if reactor is subcritical (power decreasing).
     */
    public boolean isSubcritical() {
        return reactivity < CRITICAL_REACTIVITY;
    }
    
    // NBT Serialization
    
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setDouble("fuelAmount", fuelAmount);
        nbt.setDouble("fuelEnrichment", fuelEnrichment);
        nbt.setDouble("burnup", burnup);
        nbt.setBoolean("active", active);
        nbt.setDouble("powerOutput", powerOutput);
        nbt.setDouble("controlRodInsertion", controlRodInsertion);
        nbt.setDouble("reactivity", reactivity);
        nbt.setDouble("neutronFlux", neutronFlux);
        nbt.setBoolean("scram", scram);
        nbt.setDouble("coolantFlow", coolantFlow);
        nbt.setDouble("coolantTemperature", coolantTemperature);
    }
    
    public void readFromNBT(NBTTagCompound nbt) {
        fuelAmount = nbt.getDouble("fuelAmount");
        fuelEnrichment = nbt.getDouble("fuelEnrichment");
        burnup = nbt.getDouble("burnup");
        active = nbt.getBoolean("active");
        powerOutput = nbt.getDouble("powerOutput");
        controlRodInsertion = nbt.getDouble("controlRodInsertion");
        reactivity = nbt.getDouble("reactivity");
        neutronFlux = nbt.getDouble("neutronFlux");
        scram = nbt.getBoolean("scram");
        coolantFlow = nbt.getDouble("coolantFlow");
        coolantTemperature = nbt.getDouble("coolantTemperature");
    }
}
