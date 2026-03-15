package com.atomichorizons2026.heat;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Manages heat transfer between blocks.
 * Handles conduction, convection, and radiation heat transfer.
 */
public class HeatTransferManager {
    
    // Heat transfer constants
    public static final double STEFAN_BOLTZMANN = 5.67e-8; // W/(m²·K⁴)
    public static final double AMBIENT_TEMPERATURE = 293.15; // 20°C in Kelvin
    
    /**
     * Transfers heat between two heat handlers via conduction.
     * Uses Fourier's law: Q = k × A × ΔT / d
     * 
     * @param from Source heat handler
     * @param to Destination heat handler
     * @param deltaTime Time step in seconds (typically 0.05 for 1 tick)
     * @return Amount of heat transferred in Joules
     */
    public static double transferHeatConduction(IHeatHandler from, IHeatHandler to, double deltaTime) {
        if (from == null || to == null) return 0;
        if (!from.canTransferHeat(null) || !to.canTransferHeat(null)) return 0;
        
        double tempDiff = from.getTemperature() - to.getTemperature();
        if (Math.abs(tempDiff) < 0.01) return 0; // No transfer if temps are equal
        
        // Average thermal conductivity
        double k = (from.getThermalConductivity() + to.getThermalConductivity()) / 2.0;
        
        // Simplified heat transfer: Q = k × ΔT × Δt
        // (assumes unit area and distance)
        double heatTransfer = k * tempDiff * deltaTime;
        
        // Limit transfer to prevent temperature inversion
        double maxTransfer = Math.abs(tempDiff) * Math.min(from.getHeatCapacity(), to.getHeatCapacity()) * 0.1;
        heatTransfer = Math.min(Math.abs(heatTransfer), maxTransfer) * Math.signum(heatTransfer);
        
        if (heatTransfer > 0) {
            // Transfer from 'from' to 'to'
            double removed = from.removeHeat(heatTransfer, false);
            to.addHeat(removed, false);
            return removed;
        } else {
            // Transfer from 'to' to 'from'
            double removed = to.removeHeat(-heatTransfer, false);
            from.addHeat(removed, false);
            return -removed;
        }
    }
    
    /**
     * Transfers heat to/from ambient environment via convection.
     * Uses Newton's law of cooling: Q = h × A × ΔT
     * 
     * @param handler Heat handler
     * @param deltaTime Time step in seconds
     * @param convectionCoefficient Convection coefficient (W/(m²·K))
     * @return Amount of heat transferred
     */
    public static double transferHeatConvection(IHeatHandler handler, double deltaTime, double convectionCoefficient) {
        if (handler == null || !handler.canTransferHeat(null)) return 0;
        
        double tempDiff = handler.getTemperature() - AMBIENT_TEMPERATURE;
        if (Math.abs(tempDiff) < 0.01) return 0;
        
        // Q = h × ΔT × Δt (simplified, assumes unit area)
        double heatTransfer = convectionCoefficient * tempDiff * deltaTime;
        
        if (heatTransfer > 0) {
            // Cooling to ambient
            return handler.removeHeat(heatTransfer, false);
        } else {
            // Heating from ambient (rare)
            return -handler.addHeat(-heatTransfer, false);
        }
    }
    
    /**
     * Transfers heat via thermal radiation.
     * Uses Stefan-Boltzmann law: Q = σ × ε × A × (T⁴ - T_amb⁴)
     * 
     * @param handler Heat handler
     * @param deltaTime Time step in seconds
     * @param emissivity Surface emissivity (0.0 to 1.0)
     * @return Amount of heat transferred
     */
    public static double transferHeatRadiation(IHeatHandler handler, double deltaTime, double emissivity) {
        if (handler == null || !handler.canTransferHeat(null)) return 0;
        
        double temp = handler.getTemperature();
        if (temp < AMBIENT_TEMPERATURE + 50) return 0; // Negligible below ~70°C
        
        // Q = σ × ε × (T‴ - T_amb⁴) × Δt (simplified, assumes unit area)
        double temp4 = Math.pow(temp, 4);
        double ambient4 = Math.pow(AMBIENT_TEMPERATURE, 4);
        double heatTransfer = STEFAN_BOLTZMANN * emissivity * (temp4 - ambient4) * deltaTime;
        
        if (heatTransfer > 0) {
            return handler.removeHeat(heatTransfer, false);
        }
        return 0;
    }
    
    /**
     * Transfers heat between adjacent TileEntities.
     * 
     * @param world World
     * @param pos Position of source block
     * @param facing Direction to transfer heat
     * @param deltaTime Time step in seconds
     * @return Amount of heat transferred
     */
    public static double transferHeatToNeighbor(World world, BlockPos pos, EnumFacing facing, double deltaTime) {
        TileEntity te = world.getTileEntity(pos);
        if (te == null) return 0;
        
        IHeatHandler from = te.getCapability(HeatCapability.HEAT_HANDLER_CAPABILITY, facing);
        if (from == null) return 0;
        
        BlockPos neighborPos = pos.offset(facing);
        TileEntity neighborTE = world.getTileEntity(neighborPos);
        if (neighborTE == null) return 0;
        
        IHeatHandler to = neighborTE.getCapability(HeatCapability.HEAT_HANDLER_CAPABILITY, facing.getOpposite());
        if (to == null) return 0;
        
        return transferHeatConduction(from, to, deltaTime);
    }
    
    /**
     * Transfers heat to all adjacent blocks.
     * 
     * @param world World
     * @param pos Position of source block
     * @param deltaTime Time step in seconds
     * @return Total amount of heat transferred
     */
    public static double transferHeatToAllNeighbors(World world, BlockPos pos, double deltaTime) {
        double totalTransfer = 0;
        
        for (EnumFacing facing : EnumFacing.VALUES) {
            totalTransfer += transferHeatToNeighbor(world, pos, facing, deltaTime);
        }
        
        return totalTransfer;
    }
    
    /**
     * Applies ambient cooling to a heat handler.
     * Combines convection and radiation.
     * 
     * @param handler Heat handler
     * @param deltaTime Time step in seconds
     * @return Total heat lost to environment
     */
    public static double applyAmbientCooling(IHeatHandler handler, double deltaTime) {
        if (handler == null) return 0;
        
        // Natural convection coefficient for air (typical: 5-25 W/(m²·K))
        double convection = transferHeatConvection(handler, deltaTime, 10.0);
        
        // Radiation (emissivity ~0.9 for most materials)
        double radiation = transferHeatRadiation(handler, deltaTime, 0.9);
        
        return convection + radiation;
    }
    
    /**
     * Calculates equilibrium temperature between two heat handlers.
     * 
     * @param handler1 First heat handler
     * @param handler2 Second heat handler
     * @return Equilibrium temperature in Kelvin
     */
    public static double calculateEquilibriumTemperature(IHeatHandler handler1, IHeatHandler handler2) {
        if (handler1 == null || handler2 == null) return AMBIENT_TEMPERATURE;
        
        double c1 = handler1.getHeatCapacity();
        double c2 = handler2.getHeatCapacity();
        double t1 = handler1.getTemperature();
        double t2 = handler2.getTemperature();
        
        // T_eq = (C1×T1 + C2×T2) / (C1 + C2)
        return (c1 * t1 + c2 * t2) / (c1 + c2);
    }
}
