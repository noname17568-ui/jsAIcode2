package com.atomichorizons2026.config;

/**
 * Configuration for items.
 * Centralized item properties and metadata.
 */
public class ItemConfig {
    
    // Raw materials
    public static final String[] RAW_MATERIALS = {
        "raw_uranium", "raw_thorium", "raw_zirconium", "raw_fluorite",
        "raw_sulfur", "raw_lead", "raw_beryllium", "raw_boron",
        "raw_graphite", "raw_radium"
    };
    
    // Ingots
    public static final String[] INGOTS = {
        "ingot_uranium", "ingot_thorium", "ingot_zirconium", "ingot_lead",
        "ingot_steel", "ingot_beryllium", "ingot_boron", "ingot_graphite",
        "ingot_radium"
    };
    
    // Fuel and nuclear materials
    public static final String[] NUCLEAR_ITEMS = {
        "haleu", "triso_particles", "fuel_rod_haleu", "fuel_rod",
        "spent_fuel_rod", "yellowcake", "uranium_hexafluoride"
    };
    
    // Radiation protection items
    public static final String[] RADIATION_ITEMS = {
        "hazmat_spray", "potassium_iodide", "radaway", "radx"
    };
    
    // Tools
    public static final String[] TOOLS = {
        "geiger_counter", "dosimeter"
    };
    
    // RadAway variants
    public static final String[] RADAWAY_VARIANTS = {
        "radaway_tablet", "radaway_injector", "radaway_advanced"
    };
}
