package com.atomichorizons2026.tileentities;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.blocks.BlockCorium;
import com.atomichorizons2026.handlers.RegistryHandler;
import com.atomichorizons2026.items.ItemFuelRod;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * TileEntity для контроллера SMR реактора.
 * Управляет мультиблочной структурой 3x3x4, физикой реактора и генерацией энергии.
 */
public class TileEntitySMRController extends TileEntity implements ITickable {
    
    // ==================== КОНСТАНТЫ ====================
    
    // Размеры структуры
    public static final int STRUCTURE_WIDTH = 3;
    public static final int STRUCTURE_DEPTH = 3;
    public static final int STRUCTURE_HEIGHT = 4;
    
    // Энергия
    public static final int MAX_ENERGY = 100_000_000; // 100M RF буфер
    public static final int MAX_EXTRACT = 10_000_000; // 10M RF/t макс вывод
    
    // Температурные пороги
    public static final double TEMP_MIN = 20.0;           // Комнатная
    public static final double TEMP_COLD_START = 100.0;   // Холодный старт
    public static final double TEMP_OPTIMAL = 800.0;      // Оптимум (100% эффективность)
    public static final double TEMP_HIGH = 1200.0;        // Высокая температура
    public static final double TEMP_CRITICAL = 1500.0;    // Критическая (300% эффективность)
    public static final double TEMP_MELTDOWN = 2000.0;    // Расплавление!
    
    // Генерация энергии
    public static final int BASE_ENERGY_COLD = 50_000;      // 50k RF/t холодный
    public static final int BASE_ENERGY_OPTIMAL = 500_000;  // 500k RF/t оптимум
    public static final int BASE_ENERGY_MAX = 2_500_000;    // 2.5M RF/t максимум
    
    // Охлаждение
    public static final int COOLING_TICKS = 5; // Обновлять температуру каждые 5 тиков
    public static final double COOLING_WATER = 1.0;
    public static final double COOLING_SODIUM = 5.0;
    public static final double COOLING_CRYOTHEUM = 10.0;
    
    // ==================== ПОЛЯ СОСТОЯНИЯ ====================
    
    // Энергия
    private EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, 0, MAX_EXTRACT);
    
    // Жидкостный бак для охлаждения (10 вёдер)
    private FluidTank coolantTank = new FluidTank(10000) {
        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            // Принимаем воду, натрий, криотеум
            String name = fluid.getFluid().getName();
            return name.equals("water") || name.equals("sodium") || name.equals("cryotheum");
        }
    };
    
    // Инвентарь для топливных стержней (9 слотов - 3x3)
    private ItemStackHandler fuelHandler = new ItemStackHandler(9) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() instanceof ItemFuelRod;
        }
        
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };
    
    // Состояние реактора
    private boolean isFormed = false;
    private double coreTemperature = 20.0;
    private double controlRodInsertion = 0.0; // 0.0 - 1.0 (0% - 100%)
    private int tickCounter = 0;
    private int structureCheckCooldown = 0;
    
    // Статистика
    private int currentEnergyOutput = 0;
    private boolean isMeltdown = false;
    
    // ==================== ГЕТТЕРЫ ====================
    
    public boolean isFormed() { return isFormed; }
    public double getCoreTemperature() { return coreTemperature; }
    public double getControlRodInsertion() { return controlRodInsertion; }
    public int getCurrentEnergyOutput() { return currentEnergyOutput; }
    public int getEnergyStored() { return energyStorage.getEnergyStored(); }
    public int getMaxEnergyStored() { return energyStorage.getMaxEnergyStored(); }
    public FluidTank getCoolantTank() { return coolantTank; }
    public ItemStackHandler getFuelHandler() { return fuelHandler; }
    public boolean isMeltdown() { return isMeltdown; }
    
    // ==================== ЛОГИКА ОБНОВЛЕНИЯ ====================
    
    @Override
    public void update() {
        if (world.isRemote) return;
        
        tickCounter++;
        
        // Проверка структуры каждые 20 тиков
        if (structureCheckCooldown-- <= 0) {
            structureCheckCooldown = 20;
            boolean wasFormed = isFormed;
            isFormed = checkStructure();
            
            if (wasFormed != isFormed) {
                markDirty();
                syncToClient();
            }
        }
        
        // Если структура не собрана - остываем
        if (!isFormed) {
            if (coreTemperature > TEMP_MIN) {
                coreTemperature -= 0.5;
                markDirty();
            }
            currentEnergyOutput = 0;
            return;
        }
        
        // Если уже было расплавление - не делаем ничего
        if (isMeltdown) return;
        
        // Основная логика реактора (каждые COOLING_TICKS тиков)
        if (tickCounter % COOLING_TICKS == 0) {
            updateReactorPhysics();
        }
        
        // Проверка на расплавление
        if (coreTemperature >= TEMP_MELTDOWN) {
            triggerMeltdown();
            return;
        }
        
        // Вывод энергии в соседние блоки
        pushEnergy();
    }
    
    /**
     * Основная физика реактора
     */
    private void updateReactorPhysics() {
        // 1. Рассчитываем реактивность топлива
        double fuelReactivity = calculateFuelReactivity();
        
        // 2. Генерация тепла
        double baseHeatGen = 50.0; // Базовое тепло
        double heatGen = baseHeatGen * fuelReactivity * (1.0 - controlRodInsertion);
        
        // 3. Охлаждение
        double cooling = calculateCooling();
        
        // 4. Обновляем температуру
        coreTemperature += heatGen - cooling;
        
        // Ограничиваем минимум
        if (coreTemperature < TEMP_MIN) {
            coreTemperature = TEMP_MIN;
        }
        
        // 5. Генерация энергии на основе температуры
        currentEnergyOutput = calculateEnergyOutput();
        
        // 6. Добавляем энергию в буфер
        if (currentEnergyOutput > 0) {
            energyStorage.receiveEnergy(currentEnergyOutput, false);
        }
        
        // 7. Изнашиваем топливо
        consumeFuel();
        
        markDirty();
    }
    
    /**
     * Рассчитывает реактивность топлива на основе загруженных стержней
     */
    private double calculateFuelReactivity() {
        int activeRods = 0;
        int totalRods = 0;
        
        for (int i = 0; i < fuelHandler.getSlots(); i++) {
            ItemStack stack = fuelHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemFuelRod) {
                ItemFuelRod rod = (ItemFuelRod) stack.getItem();
                if (rod.getFuelPercent(stack) > 0) {
                    activeRods++;
                }
                totalRods++;
            }
        }
        
        if (totalRods == 0) return 0.0;
        return (double) activeRods / 9.0; // Макс реактивность при 9 стержнях
    }
    
    /**
     * Рассчитывает эффективность охлаждения
     */
    private double calculateCooling() {
        FluidStack fluid = coolantTank.getFluid();
        if (fluid == null || fluid.amount == 0) return 0.0;
        
        String fluidName = fluid.getFluid().getName();
        double coolingFactor = COOLING_WATER;
        
        if (fluidName.equals("sodium")) {
            coolingFactor = COOLING_SODIUM;
        } else if (fluidName.equals("cryotheum")) {
            coolingFactor = COOLING_CRYOTHEUM;
        }
        
        // Расход жидкости пропорционален температуре
        int consumeAmount = Math.min(fluid.amount, (int)(coreTemperature / 100.0));
        if (consumeAmount > 0) {
            coolantTank.drain(consumeAmount, true);
        }
        
        return consumeAmount * coolingFactor;
    }
    
    /**
     * Рассчитывает выход энергии на основе температуры
     */
    private int calculateEnergyOutput() {
        if (coreTemperature < TEMP_COLD_START) {
            // Холодный старт: 50k RF/t
            return BASE_ENERGY_COLD;
        } else if (coreTemperature < TEMP_OPTIMAL) {
            // Линейный рост от 50k до 500k
            double factor = (coreTemperature - TEMP_COLD_START) / (TEMP_OPTIMAL - TEMP_COLD_START);
            return (int)(BASE_ENERGY_COLD + (BASE_ENERGY_OPTIMAL - BASE_ENERGY_COLD) * factor);
        } else if (coreTemperature < TEMP_HIGH) {
            // Оптимум: 500k RF/t
            return BASE_ENERGY_OPTIMAL;
        } else if (coreTemperature < TEMP_CRITICAL) {
            // Рост до критической: 500k -> 2.5M
            double factor = (coreTemperature - TEMP_HIGH) / (TEMP_CRITICAL - TEMP_HIGH);
            return (int)(BASE_ENERGY_OPTIMAL + (BASE_ENERGY_MAX - BASE_ENERGY_OPTIMAL) * factor);
        } else {
            // Критическая температура: 2.5M RF/t (риск взрыва!)
            return BASE_ENERGY_MAX;
        }
    }
    
    /**
     * Изнашивает топливо
     */
    private void consumeFuel() {
        for (int i = 0; i < fuelHandler.getSlots(); i++) {
            ItemStack stack = fuelHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemFuelRod) {
                ItemFuelRod rod = (ItemFuelRod) stack.getItem();
                rod.consumeFuel(stack, 1); // 1 тик износа
                
                if (rod.getFuelPercent(stack) <= 0) {
                    // Превращаем в отработанное топливо
                    fuelHandler.setStackInSlot(i, new ItemStack(RegistryHandler.SPENT_FUEL_ROD));
                }
            }
        }
    }
    
    /**
     * Проверяет валидность мультиблочной структуры 3x3x4
     */
    public boolean checkStructure() {
        // Определяем ориентацию (контроллер должен быть на передней стенке)
        BlockPos startPos = pos.offset(EnumFacing.DOWN); // Начинаем снизу
        
        boolean hasPort = false;
        int housingCount = 0;
        int glassCount = 0;
        
        // Сканируем куб 3x3x4
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 0; y < STRUCTURE_HEIGHT; y++) {
                    BlockPos checkPos = startPos.add(x, y, z);
                    
                    // Пропускаем позицию контроллера
                    if (checkPos.equals(pos)) continue;
                    
                    Block block = world.getBlockState(checkPos).getBlock();
                    
                    // Центр должен быть пустым (внутри реактора)
                    if (x == 0 && z == 0 && y > 0 && y < STRUCTURE_HEIGHT - 1) {
                        if (!world.isAirBlock(checkPos)) {
                            return false; // Центр должен быть пустым
                        }
                        continue;
                    }
                    
                    // Проверяем блоки стенок
                    if (block == RegistryHandler.SMR_HOUSING) {
                        housingCount++;
                    } else if (block == RegistryHandler.SMR_GLASS) {
                        glassCount++;
                    } else if (block == RegistryHandler.SMR_PORT) {
                        hasPort = true;
                    } else {
                        // Недопустимый блок
                        return false;
                    }
                }
            }
        }
        
        // Минимум 1 порт и достаточно обшивки
        return hasPort && (housingCount + glassCount) >= 30;
    }
    
    /**
     * Триггер расплавления реактора (China Syndrome)
     */
    private void triggerMeltdown() {
        isMeltdown = true;
        markDirty();
        syncToClient();
        
        AtomicHorizons2026.LOGGER.warn("MELTDOWN DETECTED at " + pos);
        
        // 1. ВЗРЫВ (сила 50.0f - в 10 раз мощнее TNT)
        world.createExplosion(null, pos.getX(), pos.getY(), pos.getY(), 50.0f, true);
        
        // 2. Радиоактивные осадки (эффект на всех в радиусе 100 блоков)
        List<EntityPlayer> players = world.getEntitiesWithinAABB(
            EntityPlayer.class,
            new AxisAlignedBB(pos.add(-100, -50, -100), pos.add(100, 50, 100))
        );
        
        for (EntityPlayer player : players) {
            // Накладываем эффект радиации (если создан)
            if (RegistryHandler.POTION_RADIATION != null) {
                player.addPotionEffect(new PotionEffect(RegistryHandler.POTION_RADIATION, 6000, 2));
            }
        }
        
        // 3. Генерация Кориума
        spawnCorium();
    }
    
    /**
     * Создает Кориум в эпицентре
     */
    private void spawnCorium() {
        // Заменяем блоки в радиусе 3 блоков на Кориум
        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos coriumPos = pos.add(x, y, z);
                    double dist = Math.sqrt(x*x + y*y + z*z);
                    
                    if (dist <= 3.0 && !world.isAirBlock(coriumPos)) {
                        // Не заменяем бедрок
                        if (world.getBlockState(coriumPos).getBlock() != Blocks.BEDROCK) {
                            world.setBlockState(coriumPos, RegistryHandler.CORIUM.getDefaultState(), 3);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Выводит энергию в соседние блоки
     */
    private void pushEnergy() {
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity te = world.getTileEntity(pos.offset(facing));
            if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                IEnergyStorage receiver = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                if (receiver != null && receiver.canReceive()) {
                    int extractable = energyStorage.extractEnergy(MAX_EXTRACT, true);
                    int received = receiver.receiveEnergy(extractable, false);
                    energyStorage.extractEnergy(received, false);
                }
            }
        }
    }
    
    // ==================== УПРАВЛЕНИЕ ====================
    
    /**
     * Устанавливает вставку стержней (0.0 - 1.0)
     */
    public void setControlRodInsertion(double insertion) {
        this.controlRodInsertion = Math.max(0.0, Math.min(1.0, insertion));
        markDirty();
    }
    
    /**
     * Экстренная остановка (SCRAM)
     */
    public void emergencyScram() {
        controlRodInsertion = 1.0; // Полное погружение
        AtomicHorizons2026.LOGGER.info("Emergency SCRAM initiated at " + pos);
        markDirty();
        syncToClient();
    }
    
    // ==================== СИНХРОНИЗАЦИЯ ====================
    
    private void syncToClient() {
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }
    
    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }
    
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
    
    // ==================== NBT ====================
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        
        isFormed = compound.getBoolean("isFormed");
        coreTemperature = compound.getDouble("coreTemperature");
        controlRodInsertion = compound.getDouble("controlRodInsertion");
        isMeltdown = compound.getBoolean("isMeltdown");
        
        // Энергия
        energyStorage = new EnergyStorage(MAX_ENERGY, 0, MAX_EXTRACT);
        if (compound.hasKey("energy")) {
            CapabilityEnergy.ENERGY.readNBT(energyStorage, null, compound.getTag("energy"));
        }
        
        // Жидкость
        if (compound.hasKey("coolant")) {
            coolantTank.readFromNBT(compound.getCompoundTag("coolant"));
        }
        
        // Инвентарь
        if (compound.hasKey("fuel")) {
            fuelHandler.deserializeNBT(compound.getCompoundTag("fuel"));
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        
        compound.setBoolean("isFormed", isFormed);
        compound.setDouble("coreTemperature", coreTemperature);
        compound.setDouble("controlRodInsertion", controlRodInsertion);
        compound.setBoolean("isMeltdown", isMeltdown);
        
        // Энергия
        compound.setTag("energy", CapabilityEnergy.ENERGY.writeNBT(energyStorage, null));
        
        // Жидкость
        NBTTagCompound coolantTag = new NBTTagCompound();
        coolantTank.writeToNBT(coolantTag);
        compound.setTag("coolant", coolantTag);
        
        // Инвентарь
        compound.setTag("fuel", fuelHandler.serializeNBT());
        
        return compound;
    }
    
    // ==================== CAPABILITIES ====================
    
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || 
               capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ||
               capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
               super.hasCapability(capability, facing);
    }
    
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(coolantTank);
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(fuelHandler);
        }
        return super.getCapability(capability, facing);
    }
}
