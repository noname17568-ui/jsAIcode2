package com.atomichorizons2026.tileentities;

import com.atomichorizons2026.energy.EnergyStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * Enrichment Chamber - Tier 1 ore processing.
 * Enriches dust (1 dust -> 1 enriched dust)
 * Per TZ Section 2.2
 */
public class TileEntityEnrichmentChamber extends TileEntity implements ITickable {

    // Energy
    private EnergyStorage energyStorage = new EnergyStorage(100_000, 1_000, 0);
    private static final int ENERGY_PER_TICK = 10;
    private static final int PROCESSING_TIME = 150; // 7.5 seconds

    // Inventory: 1 input, 1 output
    private ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };

    private int processingProgress = 0;

    @Override
    public void update() {
        if (world.isRemote) return;

        if (processingProgress == 0) {
            ItemStack input = itemHandler.getStackInSlot(0);
            if (!input.isEmpty() && canProcess(input) && canFitOutput(input)) {
                processingProgress = 1;
            }
        }

        if (processingProgress > 0) {
            if (energyStorage.hasEnergy(ENERGY_PER_TICK)) {
                energyStorage.consumeEnergy(ENERGY_PER_TICK);
                processingProgress++;

                if (processingProgress >= PROCESSING_TIME) {
                    processItem();
                    processingProgress = 0;
                }
                markDirty();
            }
        }
    }

    private boolean canProcess(ItemStack input) {
        return !com.atomichorizons2026.crafting.MachineRecipeRegistry.getEnrichmentOutput(input).isEmpty();
    }

    private boolean canFitOutput(ItemStack input) {
        ItemStack output = com.atomichorizons2026.crafting.MachineRecipeRegistry.getEnrichmentOutput(input);
        if (output.isEmpty()) return false;
        return itemHandler.insertItem(1, output.copy(), true).isEmpty();
    }

    private void processItem() {
        ItemStack input = itemHandler.getStackInSlot(0);
        if (input.isEmpty()) return;

        ItemStack output = com.atomichorizons2026.crafting.MachineRecipeRegistry.getEnrichmentOutput(input);
        if (output.isEmpty()) return;

        ItemStack remaining = itemHandler.insertItem(1, output.copy(), false);
        if (remaining.isEmpty()) {
            input.shrink(1);
        }
    }

    public int getProcessingProgress() {
        return processingProgress;
    }

    public int getMaxProcessingTime() {
        return PROCESSING_TIME;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || 
               capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
               super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        energyStorage.readFromNBT(compound);
        itemHandler.deserializeNBT(compound.getCompoundTag("items"));
        processingProgress = compound.getInteger("progress");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        energyStorage.writeToNBT(compound);
        compound.setTag("items", itemHandler.serializeNBT());
        compound.setInteger("progress", processingProgress);
        return compound;
    }
}
