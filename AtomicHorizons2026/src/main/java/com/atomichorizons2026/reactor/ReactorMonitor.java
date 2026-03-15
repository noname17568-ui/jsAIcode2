package com.atomichorizons2026.reactor;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility for monitoring and displaying reactor status.
 * Provides formatted status messages for players and GUIs.
 */
public class ReactorMonitor {
    
    /**
     * Gets a formatted status report for a reactor.
     */
    public static List<String> getStatusReport(IReactorController reactor) {
        List<String> report = new ArrayList<>();
        ReactorState state = reactor.getReactorState();
        
        // Header
        report.add(TextFormatting.BOLD + "=== Reactor Status ===");
        report.add(TextFormatting.GRAY + "Type: " + reactor.getReactorType().toUpperCase());
        report.add("");
        
        // Structure
        if (reactor.isStructureFormed()) {
            report.add(TextFormatting.GREEN + "✓ Structure: Valid");
        } else {
            report.add(TextFormatting.RED + "✗ Structure: Invalid");
            return report; // Don't show other info if structure is broken
        }
        
        // Operational State
        if (state.isActive()) {
            report.add(TextFormatting.GREEN + "✓ Status: Active");
        } else if (state.isScram()) {
            report.add(TextFormatting.RED + "✗ Status: SCRAM (Emergency Shutdown)");
        } else {
            report.add(TextFormatting.YELLOW + "○ Status: Offline");
        }
        report.add("");
        
        // Power Output
        double power = state.getPowerOutput();
        String powerColor = power > 80 ? TextFormatting.RED.toString() : 
                           power > 50 ? TextFormatting.YELLOW.toString() : 
                           TextFormatting.GREEN.toString();
        report.add(powerColor + String.format("Power: %.2f MW", power));
        
        // Temperature
        double tempC = reactor.getTemperature() - 273.15;
        String tempColor = tempC > 800 ? TextFormatting.RED.toString() : 
                          tempC > 500 ? TextFormatting.YELLOW.toString() : 
                          TextFormatting.GREEN.toString();
        report.add(tempColor + String.format("Temperature: %.1f°C", tempC));
        report.add("");
        
        // Reactivity
        double reactivity = state.getReactivity();
        String reactivityStatus;
        if (state.isCritical()) {
            reactivityStatus = TextFormatting.GREEN + "Critical (k = 1.0)";  
        } else if (state.isSupercritical()) {
            reactivityStatus = TextFormatting.YELLOW + String.format("Supercritical (k = %.3f)", reactivity);
        } else {
            reactivityStatus = TextFormatting.GRAY + String.format("Subcritical (k = %.3f)", reactivity);
        }
        report.add("Reactivity: " + reactivityStatus);
        
        // Control Rods
        double rods = state.getControlRodInsertion() * 100;
        report.add(TextFormatting.GRAY + String.format("Control Rods: %.1f%% inserted", rods));
        report.add("");
        
        // Fuel
        double fuelPercent = state.getFuelPercentage() * 100;
        String fuelColor = fuelPercent < 20 ? TextFormatting.RED.toString() : 
                          fuelPercent < 50 ? TextFormatting.YELLOW.toString() : 
                          TextFormatting.GREEN.toString();
        report.add(fuelColor + String.format("Fuel: %.1f%% remaining", fuelPercent));
        report.add(TextFormatting.GRAY + String.format("Enrichment: %.1f%%", state.getFuelEnrichment() * 100));
        report.add(TextFormatting.GRAY + String.format("Burnup: %.1f MWd/kg", state.getBurnup()));
        
        return report;
    }
    
    /**
     * Gets a compact status line for HUD display.
     */
    public static String getCompactStatus(IReactorController reactor) {
        ReactorState state = reactor.getReactorState();
        
        if (!reactor.isStructureFormed()) {
            return TextFormatting.RED + "[INVALID STRUCTURE]";
        }
        
        if (state.isScram()) {
            return TextFormatting.RED + "[SCRAM]";
        }
        
        if (!state.isActive()) {
            return TextFormatting.GRAY + "[OFFLINE]";
        }
        
        double tempC = reactor.getTemperature() - 273.15;
        return String.format("%s%.1f MW | %.0f°C | %.0f%% Fuel",
            TextFormatting.GREEN,
            state.getPowerOutput(),
            tempC,
            state.getFuelPercentage() * 100
        );
    }
    
    /**
     * Gets warning messages for dangerous conditions.
     */
    public static List<String> getWarnings(IReactorController reactor) {
        List<String> warnings = new ArrayList<>();
        ReactorState state = reactor.getReactorState();
        
        if (!reactor.isStructureFormed()) {
            warnings.add(TextFormatting.RED + "⚠ Structure is invalid!");
            return warnings;
        }
        
        // Temperature warnings
        double tempC = reactor.getTemperature() - 273.15;
        if (tempC > 900) {
            warnings.add(TextFormatting.RED + "⚠ CRITICAL TEMPERATURE!");
        } else if (tempC > 700) {
            warnings.add(TextFormatting.YELLOW + "⚠ High temperature");
        }
        
        // Reactivity warnings
        if (state.getReactivity() > 1.3) {
            warnings.add(TextFormatting.RED + "⚠ REACTIVITY TOO HIGH!");
        } else if (state.getReactivity() > 1.1) {
            warnings.add(TextFormatting.YELLOW + "⚠ Reactivity elevated");
        }
        
        // Fuel warnings
        if (state.getFuelPercentage() < 0.1) {
            warnings.add(TextFormatting.RED + "⚠ FUEL CRITICALLY LOW!");
        } else if (state.getFuelPercentage() < 0.3) {
            warnings.add(TextFormatting.YELLOW + "⚠ Fuel low");
        }
        
        // Coolant warnings
        if (state.isActive() && state.getCoolantFlow() < 1.0) {
            warnings.add(TextFormatting.RED + "⚠ INSUFFICIENT COOLANT!");
        }
        
        return warnings;
    }
    
    /**
     * Formats a value with units and color coding.
     */
    public static String formatValue(String label, double value, String unit, 
                                    double yellowThreshold, double redThreshold) {
        String color;
        if (value >= redThreshold) {
            color = TextFormatting.RED.toString();
        } else if (value >= yellowThreshold) {
            color = TextFormatting.YELLOW.toString();
        } else {
            color = TextFormatting.GREEN.toString();
        }
        
        return String.format("%s%s: %s%.2f %s", 
            TextFormatting.GRAY, label, color, value, unit);
    }
}
