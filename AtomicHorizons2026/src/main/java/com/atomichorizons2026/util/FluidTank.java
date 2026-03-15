package com.atomichorizons2026.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

/**
 * Custom FluidTank implementation to replace Forge's FluidTank
 */
public class FluidTank implements IFluidHandler, IFluidTankProperties {
    @Nullable
    protected FluidStack fluid;
    protected int capacity;

    public FluidTank(int capacity) {
        this.capacity = capacity;
    }

    public FluidTank(@Nullable FluidStack stack, int capacity) {
        this.fluid = stack;
        this.capacity = capacity;
    }

    public FluidTank readFromNBT(NBTTagCompound nbt) {
        if (!nbt.hasKey("Empty")) {
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            setFluid(fluid);
        } else {
            setFluid(null);
        }
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (fluid != null) {
            fluid.writeToNBT(nbt);
        } else {
            nbt.setString("Empty", "");
        }
        return nbt;
    }

    @Nullable
    public FluidStack getFluid() {
        return fluid;
    }

    public int getFluidAmount() {
        if (fluid == null) {
            return 0;
        }
        return fluid.amount;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setFluid(@Nullable FluidStack stack) {
        this.fluid = stack;
    }

    public boolean isEmpty() {
        return fluid == null || fluid.amount <= 0;
    }

    public int getSpace() {
        return Math.max(0, capacity - getFluidAmount());
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[] { this };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) {
            return 0;
        }

        if (!doFill) {
            if (fluid == null) {
                return Math.min(capacity, resource.amount);
            }

            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }

            return Math.min(capacity - fluid.amount, resource.amount);
        }

        if (fluid == null) {
            fluid = new FluidStack(resource, Math.min(capacity, resource.amount));
            return fluid.amount;
        }

        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }

        int filled = capacity - fluid.amount;

        if (resource.amount < filled) {
            fluid.amount += resource.amount;
            filled = resource.amount;
        } else {
            fluid.amount = capacity;
        }

        return filled;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(fluid)) {
            return null;
        }
        return drain(resource.amount, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (fluid == null || maxDrain <= 0) {
            return null;
        }

        int drained = maxDrain;
        if (fluid.amount < drained) {
            drained = fluid.amount;
        }

        FluidStack stack = new FluidStack(fluid, drained);
        if (doDrain) {
            fluid.amount -= drained;
            if (fluid.amount <= 0) {
                fluid = null;
            }
        }
        return stack;
    }

    // IFluidTankProperties implementation
    @Nullable
    @Override
    public FluidStack getContents() {
        return fluid;
    }

    @Override
    public boolean canFill() {
        return true;
    }

    @Override
    public boolean canDrain() {
        return true;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluidStack) {
        return true;
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluidStack) {
        return true;
    }
}
