package com.atomichorizons2026.reactor;

import com.atomichorizons2026.heat.HeatCapability;
import com.atomichorizons2026.heat.HeatStorage;
import com.atomichorizons2026.heat.IHeatHandler;
import com.atomichorizons2026.multiblock.MultiblockPattern;
import com.atomichorizons2026.multiblock.StructureValidator;
import com.atomichorizons2026.multiblock.patterns.MultiblockPatterns;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * TileEntity for SMR Controller.
 * Integrates multiblock framework (MR5) with reactor physics (MR6).
 */
public class TileEntitySMRControllerNew extends TileEntity implements ITickable, IReactorController {
    
    // Reactor state
    private final ReactorState reactorState = new ReactorState();
    
    // Heat system (from MR2)
    private final HeatStorage heat = new HeatStorage(
        10000.0,  // 10kJ/K heat capacity
        100.0,    // 100 W/m·K thermal conductivity
        1500.0    // 1500K max temperature (~1227°C)
    );
    
    // Multiblock state
    private boolean structureFormed = false;
    private EnumFacing facing = EnumFacing.NORTH;
    private List<BlockPos> multiblockPositions = new ArrayList<>();
    
    // Update counters
    private int tickCounter = 0;
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        tickCounter++;
        
        // Validate structure every second (20 ticks)
        if (tickCounter % 20 == 0) {
            validateStructure(false);
        }
        
        // Update reactor physics
        if (structureFormed) {
            reactorState.update(0.05, heat); // 0.05 seconds per tick
            
            // Update coolant temperature from heat
            reactorState.setCoolantTemperature(heat.getTemperature());
        }
        
        // Apply ambient cooling every second
        if (tickCounter % 20 == 0) {
            com.atomichorizons2026.heat.HeatTransferManager.applyAmbientCooling(heat, 1.0);
        }
        
        // Transfer heat to adjacent blocks
        if (tickCounter % 5 == 0 && structureFormed) {
            com.atomichorizons2026.heat.HeatTransferManager.transferHeatToAllNeighbors(world, pos, 0.25);
        }
    }
    
    @Override
    public void validateStructure(boolean forceRevalidate) {
        MultiblockPattern pattern = MultiblockPatterns.getSMRPattern();
        MultiblockPattern.ValidationResult result = StructureValidator.validate(
            world, pos, pattern, facing, forceRevalidate
        );
        
        boolean wasFormed = structureFormed;
        structureFormed = result.isValid();
        
        if (structureFormed) {
            multiblockPositions = result.getValidPositions();
        } else {
            multiblockPositions.clear();
            
            // Structure broke - emergency shutdown
            if (wasFormed && reactorState.isActive()) {
                triggerScram("Multiblock structure broken");
            }
        }
        
        markDirty();
    }
    
    @Override
    public boolean startReactor() {
        if (!structureFormed) {
            return false;
        }
        
        if (reactorState.getFuelAmount() <= 0) {
            return false;
        }
        
        if (reactorState.isScram()) {
            return false;
        }
        
        reactorState.setActive(true);
        markDirty();
        return true;
    }
    
    @Override
    public void stopReactor() {
        reactorState.setActive(false);
        markDirty();
    }
    
    @Override
    public void triggerScram(String reason) {
        reactorState.triggerScram(reason);
        markDirty();
    }
    
    @Override
    public void resetScram() {
        reactorState.resetScram();
        markDirty();
    }
    
    @Override
    public boolean insertFuel(double amount, double enrichment) {
        if (!structureFormed) return false;
        if (reactorState.isActive()) return false; // Can't refuel while running
        
        reactorState.setFuelAmount(amount);
        reactorState.setFuelEnrichment(enrichment);
        markDirty();
        return true;
    }
    
    @Override
    public boolean removeFuel() {
        if (reactorState.isActive()) return false;
        
        reactorState.setFuelAmount(0);
        markDirty();
        return true;
    }
    
    @Override
    public void setControlRods(double insertion) {
        reactorState.setControlRodInsertion(insertion);
        markDirty();
    }
    
    @Override
    public double getPowerOutput() {
        return reactorState.getPowerOutput();
    }
    
    @Override
    public double getTemperature() {
        return heat.getTemperature();
    }
    
    @Override
    public String getReactorType() {
        return "smr";
    }
    
    @Override
    public ReactorState getReactorState() {
        return reactorState;
    }
    
    @Nullable
    @Override
    public IHeatHandler getHeatHandler() {
        return heat;
    }
    
    @Override
    public boolean isStructureFormed() {
        return structureFormed;
    }
    
    @Override
    public World getWorld() {
        return world;
    }
    
    @Override
    public BlockPos getPos() {
        return pos;
    }
    
    @Override
    public List<BlockPos> getMultiblockPositions() {
        return new ArrayList<>(multiblockPositions);
    }
    
    public EnumFacing getFacing() {
        return facing;
    }
    
    public void setFacing(EnumFacing facing) {
        this.facing = facing;
        validateStructure(true);
    }
    
    // NBT Serialization
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        
        NBTTagCompound reactorTag = new NBTTagCompound();
        reactorState.writeToNBT(reactorTag);
        compound.setTag("reactor", reactorTag);
        
        NBTTagCompound heatTag = new NBTTagCompound();
        heat.writeToNBT(heatTag);
        compound.setTag("heat", heatTag);
        
        compound.setBoolean("structureFormed", structureFormed);
        compound.setInteger("facing", facing.getIndex());
        
        return compound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        
        if (compound.hasKey("reactor")) {
            reactorState.readFromNBT(compound.getCompoundTag("reactor"));
        }
        
        if (compound.hasKey("heat")) {
            heat.readFromNBT(compound.getCompoundTag("heat"));
        }
        
        structureFormed = compound.getBoolean("structureFormed");
        facing = EnumFacing.byIndex(compound.getInteger("facing"));
    }
    
    // Capabilities
    
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == HeatCapability.HEAT_HANDLER_CAPABILITY) return true;
        return super.hasCapability(capability, facing);
    }
    
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == HeatCapability.HEAT_HANDLER_CAPABILITY) {
            return HeatCapability.HEAT_HANDLER_CAPABILITY.cast(heat);
        }
        return super.getCapability(capability, facing);
    }
}
