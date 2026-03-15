package com.atomichorizons2026.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ITickable;

public abstract class AbstractMultiblockController extends TileEntity implements ITickable {
    
    // Состояние структуры
    protected boolean isFormed = false;
    protected List<BlockPos> structureBlocks = new ArrayList<>();
    
    // Таймер проверки структуры
    protected int validationCooldown = 0;
    protected static final int VALIDATION_INTERVAL = 20; // Проверять раз в секунду
    
    /**
     * Проверяет валидность структуры
     * @return true если структура собрана правильно
     */
    public abstract boolean validateStructure();
    
    /**
     * Вызывается при успешном формировании структуры
     */
    public abstract void onStructureFormed();
    
    /**
     * Вызывается при разрушении структуры
     */
    public abstract void onStructureBroken();
    
    /**
     * Получает минимальные размеры структуры
     */
    public abstract int getMinSizeX();
    public abstract int getMinSizeY();
    public abstract int getMinSizeZ();
    
    /**
     * Получает максимальные размеры структуры
     */
    public abstract int getMaxSizeX();
    public abstract int getMaxSizeY();
    public abstract int getMaxSizeZ();
    
    /**
     * Основной тик контроллера
     */
    @Override
    public void update() {
        
        if (world.isRemote) return;
        
        // Проверка структуры с кулдауном
        if (validationCooldown-- <= 0) {
            validationCooldown = VALIDATION_INTERVAL;
            
            boolean wasFormed = isFormed;
            isFormed = validateStructure();
            
            if (!wasFormed && isFormed) {
                onStructureFormed();
                markDirty();
            } else if (wasFormed && !isFormed) {
                onStructureBroken();
                markDirty();
            }
        }
    }
    
    /**
     * Проверяет, является ли блок валидным для структуры
     */
    protected boolean isValidBlockForStructure(World world, BlockPos pos, @Nullable EnumFacing facing) {
        IBlockState state = world.getBlockState(pos);
        return isValidBlockState(state, facing);
    }
    
    /**
     * Проверяет состояние блока
     */
    protected abstract boolean isValidBlockState(IBlockState state, @Nullable EnumFacing facing);
    
    /**
     * Сканирует область для поиска структуры
     */
    protected List<BlockPos> scanArea(BlockPos start, int rangeX, int rangeY, int rangeZ) {
        List<BlockPos> found = new ArrayList<>();
        
        for (int x = -rangeX; x <= rangeX; x++) {
            for (int y = -rangeY; y <= rangeY; y++) {
                for (int z = -rangeZ; z <= rangeZ; z++) {
                    BlockPos checkPos = start.add(x, y, z);
                    if (isValidBlockForStructure(world, checkPos, null)) {
                        found.add(checkPos);
                    }
                }
            }
        }
        
        return found;
    }
    
    /**
     * Проверяет, сформирована ли структура
     */
    public boolean isFormed() {
        return isFormed;
    }
    
    /**
     * Получает список блоков структуры
     */
    public List<BlockPos> getStructureBlocks() {
        return new ArrayList<>(structureBlocks);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        isFormed = compound.getBoolean("isFormed");
        
        // Загрузка позиций блоков
        structureBlocks.clear();
        int blockCount = compound.getInteger("blockCount");
        for (int i = 0; i < blockCount; i++) {
            int[] pos = compound.getIntArray("block_" + i);
            if (pos.length == 3) {
                structureBlocks.add(new BlockPos(pos[0], pos[1], pos[2]));
            }
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("isFormed", isFormed);
        
        // Сохранение позиций блоков
        compound.setInteger("blockCount", structureBlocks.size());
        for (int i = 0; i < structureBlocks.size(); i++) {
            BlockPos pos = structureBlocks.get(i);
            compound.setIntArray("block_" + i, new int[]{pos.getX(), pos.getY(), pos.getZ()});
        }
        
        return compound;
    }
    
    /**
     * Разрушает структуру (вызывается при удалении TileEntity)
     */
    @Override
    public void invalidate() {
        if (isFormed) {
            onStructureBroken();
        }
        super.invalidate();
    }
}
