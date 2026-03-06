package com.atomichorizons2026.heat;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Система тепла для мультиблоков и машин.
 * Работает с температурами (°C) и теплоемкостью.
 */
public class HeatSystem {
    
    // Константы
    public static final double ABSOLUTE_ZERO = -273.15; // °C
    public static final double ROOM_TEMPERATURE = 20.0; // °C
    public static final double WATER_BOILING = 100.0; // °C
    public static final double STEAM_TEMPERATURE = 300.0; // °C (пар высокого давления)
    
    // Теплоемкости материалов (Дж/(кг·К))
    public static final double SPECIFIC_HEAT_WATER = 4186;
    public static final double SPECIFIC_HEAT_IRON = 450;
    public static final double SPECIFIC_HEAT_COPPER = 385;
    public static final double SPECIFIC_HEAT_STEEL = 500;
    public static final double SPECIFIC_HEAT_CONCRETE = 880;
    
    // Теплопроводность (Вт/(м·К))
    public static final double THERMAL_CONDUCTIVITY_COPPER = 401;
    public static final double THERMAL_CONDUCTIVITY_IRON = 80;
    public static final double THERMAL_CONDUCTIVITY_STEEL = 50;
    public static final double THERMAL_CONDUCTIVITY_CONCRETE = 1.7;
    public static final double THERMAL_CONDUCTIVITY_AIR = 0.026;
    
    /**
     * Тепловой узел - хранит тепловую энергию объекта
     */
    public static class HeatNode {
        private double temperature; // Температура в °C
        private double heatCapacity; // Теплоемкость в Дж/К
        private double thermalMass; // Масса * теплоемкость
        
        public HeatNode(double initialTemp, double heatCapacity, double mass) {
            this.temperature = initialTemp;
            this.heatCapacity = heatCapacity;
            this.thermalMass = heatCapacity * mass;
        }
        
        public double getTemperature() {
            return temperature;
        }
        
        public void setTemperature(double temp) {
            this.temperature = Math.max(ABSOLUTE_ZERO, temp);
        }
        
        public double getHeatCapacity() {
            return heatCapacity;
        }
        
        public double getThermalMass() {
            return thermalMass;
        }
        
        /**
         * Добавляет тепловую энергию (Дж)
         */
        public void addEnergy(double joules) {
            double deltaT = joules / thermalMass;
            temperature += deltaT;
        }
        
        /**
         * Удаляет тепловую энергию (Дж)
         */
        public void removeEnergy(double joules) {
            addEnergy(-joules);
        }
        
        /**
         * Получает текущую тепловую энергию относительно 0°C
         */
        public double getEnergy() {
            return temperature * thermalMass;
        }
        
        /**
         * Передает тепло другому узлу
         */
        public void transferHeatTo(HeatNode other, double conductivity, double surfaceArea, double time) {
            double tempDiff = this.temperature - other.temperature;
            if (Math.abs(tempDiff) < 0.001) return;
            
            // Формула теплопередачи: Q = k * A * ΔT * t
            double heatTransfer = conductivity * surfaceArea * tempDiff * time;
            
            // Ограничиваем передачу (не можем передать больше, чем есть разница)
            double maxTransfer = Math.abs(tempDiff) * Math.min(this.thermalMass, other.thermalMass) * 0.5;
            heatTransfer = Math.max(-maxTransfer, Math.min(maxTransfer, heatTransfer));
            
            this.removeEnergy(heatTransfer);
            other.addEnergy(heatTransfer);
        }
        
        public void readFromNBT(NBTTagCompound nbt) {
            this.temperature = nbt.getDouble("temperature");
            this.thermalMass = nbt.getDouble("thermalMass");
        }
        
        public void writeToNBT(NBTTagCompound nbt) {
            nbt.setDouble("temperature", temperature);
            nbt.setDouble("thermalMass", thermalMass);
        }
    }
    
    /**
     * Теплообменник между двумя контурами
     */
    public static class HeatExchanger {
        private HeatNode primary; // Первичный контур (может быть радиоактивным)
        private HeatNode secondary; // Вторичный контур (чистый)
        private double efficiency; // Эффективность (0.0 - 1.0)
        private double surfaceArea; // Площадь поверхности
        
        public HeatExchanger(HeatNode primary, HeatNode secondary, double efficiency, double surfaceArea) {
            this.primary = primary;
            this.secondary = secondary;
            this.efficiency = efficiency;
            this.surfaceArea = surfaceArea;
        }
        
        /**
         * Обновляет теплообмен
         */
        public void update(double conductivity) {
            primary.transferHeatTo(secondary, conductivity * efficiency, surfaceArea, 0.05); // 1 тик
        }
        
        public HeatNode getPrimary() { return primary; }
        public HeatNode getSecondary() { return secondary; }
    }
    
    /**
     * Тепловая сеть - соединяет несколько узлов
     */
    public static class HeatNetwork {
        private Map<BlockPos, HeatNode> nodes = new HashMap<>();
        private Map<BlockPos, Double> conductivities = new HashMap<>();
        
        public void addNode(BlockPos pos, HeatNode node, double conductivity) {
            nodes.put(pos, node);
            conductivities.put(pos, conductivity);
        }
        
        public void removeNode(BlockPos pos) {
            nodes.remove(pos);
            conductivities.remove(pos);
        }
        
        @Nullable
        public HeatNode getNode(BlockPos pos) {
            return nodes.get(pos);
        }
        
        /**
         * Распространяет тепло по сети
         */
        public void update(World world) {
            for (Map.Entry<BlockPos, HeatNode> entry : nodes.entrySet()) {
                BlockPos pos = entry.getKey();
                HeatNode node = entry.getValue();
                
                // Передаем тепло соседним узлам
                for (EnumFacing facing : EnumFacing.values()) {
                    BlockPos neighborPos = pos.offset(facing);
                    HeatNode neighbor = nodes.get(neighborPos);
                    
                    if (neighbor != null) {
                        double avgConductivity = (conductivities.get(pos) + conductivities.get(neighborPos)) / 2;
                        node.transferHeatTo(neighbor, avgConductivity, 1.0, 0.05);
                    }
                }
                
                // Потери тепла в окружающую среду
                double ambientTemp = getAmbientTemperature(world, pos);
                double heatLoss = (node.getTemperature() - ambientTemp) * 0.001; // Небольшие потери
                node.removeEnergy(heatLoss);
            }
        }
        
        private double getAmbientTemperature(World world, BlockPos pos) {
            // Базовая температура зависит от биома
            float temperature = world.getBiome(pos).getTemperature(pos);
            return temperature * 20; // Приблизительно
        }
    }
}
