package com.atomichorizons2026.radiation;

import com.atomichorizons2026.AtomicHorizons2026;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Глобальный менеджер радиации.
 * Управляет радиацией чанков и распространением.
 */
public class RadiationManager {
    
    private static final RadiationManager INSTANCE = new RadiationManager();
    
    // Хранение радиации чанков: World -> ChunkPos -> Radiation Level
    private final Map<World, Map<ChunkPos, Double>> chunkRadiation = new WeakHashMap<>();
    
    // Конфигурация
    public static final double SPREAD_RATE = 0.01; // 1% распространения в тик
    public static final double DECAY_RATE = 0.001; // Полураспад чанка
    public static final double MAX_CHUNK_RADIATION = 10000.0; // Максимум RAD/чанк
    
    private RadiationManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public static RadiationManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Получает уровень радиации чанка
     */
    public double getChunkRadiation(World world, ChunkPos pos) {
        Map<ChunkPos, Double> worldRadiation = chunkRadiation.get(world);
        if (worldRadiation == null) return 0.0;
        return worldRadiation.getOrDefault(pos, 0.0);
    }
    
    /**
     * Получает уровень радиации чанка по позиции блока
     */
    public double getChunkRadiation(World world, BlockPos pos) {
        return getChunkRadiation(world, new ChunkPos(pos));
    }
    
    /**
     * Устанавливает радиацию чанка
     */
    public void setChunkRadiation(World world, ChunkPos pos, double radiation) {
        chunkRadiation.computeIfAbsent(world, k -> new HashMap<>())
                      .put(pos, Math.max(0, Math.min(radiation, MAX_CHUNK_RADIATION)));
    }
    
    /**
     * Добавляет радиацию к чанку
     */
    public void addChunkRadiation(World world, ChunkPos pos, double amount) {
        double current = getChunkRadiation(world, pos);
        setChunkRadiation(world, pos, current + amount);
    }
    
    /**
     * Уменьшает радиацию чанка
     */
    public void reduceChunkRadiation(World world, ChunkPos pos, double amount) {
        double current = getChunkRadiation(world, pos);
        setChunkRadiation(world, pos, Math.max(0, current - amount));
    }
    
    /**
     * Обработка тика игрока - начисление радиации
     */
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.world.isRemote) return;
        
        EntityPlayer player = event.player;
        World world = player.world;
        
        // Получаем радиацию чанка
        double chunkRad = getChunkRadiation(world, new ChunkPos(player.getPosition()));
        
        if (chunkRad > 0) {
            // Получаем capability игрока
            PlayerRadiationCapability cap = player.getCapability(
                PlayerRadiationCapability.RADIATION_CAPABILITY, null);
            
            if (cap != null) {
                // Учитываем защиту брони
                double protection = calculateArmorProtection(player);
                double actualRad = chunkRad * (1.0 - protection) * 0.01; // 1% от уровня чанка
                
                cap.addRadiation(actualRad);
                cap.applyEffects(player);
                
                // Отладка
                if (player.world.getTotalWorldTime() % 100 == 0 && chunkRad > 100) {
                    AtomicHorizons2026.LOGGER.debug(
                        "Player " + player.getName() + " received " + actualRad + " RAD");
                }
            }
        }
    }
    
    /**
     * Распространение радиации между чанками
     */
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.world.isRemote) return;
        
        World world = event.world;
        Map<ChunkPos, Double> worldRadiation = chunkRadiation.get(world);
        
        if (worldRadiation == null || worldRadiation.isEmpty()) return;
        
        // Обрабатываем каждые 20 тиков (1 секунда)
        if (world.getTotalWorldTime() % 20 != 0) return;
        
        Map<ChunkPos, Double> newRadiation = new HashMap<>();
        
        for (Map.Entry<ChunkPos, Double> entry : worldRadiation.entrySet()) {
            ChunkPos pos = entry.getKey();
            double radiation = entry.getValue();
            
            if (radiation <= 0) continue;
            
            // Распад радиации
            radiation *= (1.0 - DECAY_RATE);
            
            // Распространение в соседние чанки
            double spreadAmount = radiation * SPREAD_RATE;
            
            // Соседние чанки
            ChunkPos[] neighbors = {
                new ChunkPos(pos.x + 1, pos.z),
                new ChunkPos(pos.x - 1, pos.z),
                new ChunkPos(pos.x, pos.z + 1),
                new ChunkPos(pos.x, pos.z - 1)
            };
            
            for (ChunkPos neighbor : neighbors) {
                double neighborRad = getChunkRadiation(world, neighbor);
                double transfer = spreadAmount * 0.25;
                
                if (neighborRad < radiation) {
                    newRadiation.put(neighbor, neighborRad + transfer);
                    radiation -= transfer;
                }
            }
            
            newRadiation.put(pos, radiation);
        }
        
        // Обновляем мапу
        worldRadiation.clear();
        worldRadiation.putAll(newRadiation);
    }
    
    /**
     * Рассчитывает защиту брони от радиации
     */
    private double calculateArmorProtection(EntityPlayer player) {
        double totalProtection = 0.0;
        
        for (net.minecraft.item.ItemStack stack : player.inventory.armorInventory) {
            if (!stack.isEmpty() && stack.getItem() instanceof IRadiationProtection) {
                totalProtection += ((IRadiationProtection) stack.getItem()).getRadiationProtection(stack);
            }
        }
        
        // Максимум 95% защиты
        return Math.min(0.95, totalProtection);
    }
    
    /**
     * Сохранение радиации чанка
     */
    @SubscribeEvent
    public void onChunkSave(ChunkDataEvent.Save event) {
        ChunkPos pos = new ChunkPos(event.getChunk().x, event.getChunk().z);
        double radiation = getChunkRadiation(event.getWorld(), pos);
        
        if (radiation > 0) {
            event.getData().setDouble("AtomicHorizons_Radiation", radiation);
        }
    }
    
    /**
     * Загрузка радиации чанка
     */
    @SubscribeEvent
    public void onChunkLoad(ChunkDataEvent.Load event) {
        ChunkPos pos = new ChunkPos(event.getChunk().x, event.getChunk().z);
        
        if (event.getData().hasKey("AtomicHorizons_Radiation")) {
            double radiation = event.getData().getDouble("AtomicHorizons_Radiation");
            setChunkRadiation(event.getWorld(), pos, radiation);
        }
    }
    
    /**
     * Интерфейс для предметов с защитой от радиации
     */
    public interface IRadiationProtection {
        double getRadiationProtection(net.minecraft.item.ItemStack stack);
    }
}
