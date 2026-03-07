package com.atomichorizons2026.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Интерфейс для валидаторов мультиблочных структур.
 * Позволяет создавать кастомные правила проверки.
 */
public interface IMultiblockValidator {
    
    /**
     * Проверяет структуру
     * @param world Мир
     * @param controllerPos Позиция контроллера
     * @param facing Направление (может быть null)
     * @return Результат проверки
     */
    ValidationResult validate(World world, BlockPos controllerPos, @Nullable net.minecraft.util.EnumFacing facing);
    
    /**
     * Получает минимальные размеры
     */
    int getMinX();
    int getMinY();
    int getMinZ();
    
    /**
     * Получает максимальные размеры
     */
    int getMaxX();
    int getMaxY();
    int getMaxZ();
    
    /**
     * Результат валидации
     */
    class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        private final List<BlockPos> validBlocks;
        private final BlockPos errorPos;
        
        public ValidationResult(boolean valid, String errorMessage, List<BlockPos> validBlocks, BlockPos errorPos) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.validBlocks = validBlocks;
            this.errorPos = errorPos;
        }
        
        public static ValidationResult success(List<BlockPos> validBlocks) {
            return new ValidationResult(true, "", validBlocks, null);
        }
        
        public static ValidationResult failure(String message, BlockPos errorPos) {
            return new ValidationResult(false, message, null, errorPos);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public List<BlockPos> getValidBlocks() {
            return validBlocks;
        }
        
        public BlockPos getErrorPos() {
            return errorPos;
        }
    }
    
    /**
     * Проверяет конкретный блок
     */
    interface IBlockValidator {
        boolean isValid(IBlockState state, BlockPos pos, World world);
        String getErrorMessage();
    }
}
