package com.atomichorizons2026.handlers;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.items.ItemDosimeter;
import com.atomichorizons2026.items.ItemGeigerCounter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Обработчик звуков мода.
 * Регистрирует все пользовательские звуковые события.
 */
@EventBusSubscriber
public class SoundHandler {
    
    // ==================== GEIGER COUNTER ====================
    public static SoundEvent GEIGER_CLICK_LOW;
    public static SoundEvent GEIGER_CLICK_MEDIUM;
    public static SoundEvent GEIGER_CLICK_HIGH;
    public static SoundEvent GEIGER_CLICK_CRITICAL;
    
    // ==================== DOSIMETER ====================
    public static SoundEvent DOSIMETER_BEEP;
    
    // ==================== ALARMS ====================
    public static SoundEvent ALARM_RADIATION_LOW;
    public static SoundEvent ALARM_RADIATION_HIGH;
    public static SoundEvent ALARM_MELTDOWN;
    
    // ==================== REACTOR ====================
    public static SoundEvent REACTOR_HUM;
    public static SoundEvent REACTOR_SCRAM;
    public static SoundEvent REACTOR_STARTUP;
    
    // ==================== HEV SUIT ====================
    public static SoundEvent HEV_POWER_ON;
    public static SoundEvent HEV_POWER_OFF;
    public static SoundEvent HEV_DAMAGE;
    public static SoundEvent HEV_LOW_ENERGY;
    
    public static void preInit() {
        // ==================== GEIGER COUNTER SOUNDS ====================
        GEIGER_CLICK_LOW = createSoundEvent("geiger_click_low");
        GEIGER_CLICK_MEDIUM = createSoundEvent("geiger_click_medium");
        GEIGER_CLICK_HIGH = createSoundEvent("geiger_click_high");
        GEIGER_CLICK_CRITICAL = createSoundEvent("geiger_click_critical");
        
        // ==================== DOSIMETER ====================
        DOSIMETER_BEEP = createSoundEvent("dosimeter_beep");
        
        // ==================== ALARMS ====================
        ALARM_RADIATION_LOW = createSoundEvent("alarm_radiation_low");
        ALARM_RADIATION_HIGH = createSoundEvent("alarm_radiation_high");
        ALARM_MELTDOWN = createSoundEvent("alarm_meltdown");
        
        // ==================== REACTOR ====================
        REACTOR_HUM = createSoundEvent("reactor_hum");
        REACTOR_SCRAM = createSoundEvent("reactor_scram");
        REACTOR_STARTUP = createSoundEvent("reactor_startup");
        
        // ==================== HEV SUIT ====================
        HEV_POWER_ON = createSoundEvent("hev_power_on");
        HEV_POWER_OFF = createSoundEvent("hev_power_off");
        HEV_DAMAGE = createSoundEvent("hev_damage");
        HEV_LOW_ENERGY = createSoundEvent("hev_low_energy");
        
        // Устанавливаем ссылки в классах предметов
        ItemGeigerCounter.SOUND_GEIGER_CLICK = GEIGER_CLICK_MEDIUM;
        ItemDosimeter.SOUND_DOSIMETER_CLICK = DOSIMETER_BEEP;
    }
    
    /**
     * Создает SoundEvent
     */
    private static SoundEvent createSoundEvent(String name) {
        ResourceLocation location = new ResourceLocation(AtomicHorizons2026.MODID, name);
        SoundEvent event = new SoundEvent(location);
        event.setRegistryName(location);
        return event;
    }
    
    /**
     * Получает подходящий звук счетчика Гейгера в зависимости от уровня радиации
     * @param radiationLevel Уровень радиации
     * @return SoundEvent
     */
    public static SoundEvent getGeigerSoundForRadiation(double radiationLevel) {
        if (radiationLevel < 10.0) {
            return GEIGER_CLICK_LOW;
        } else if (radiationLevel < 100.0) {
            return GEIGER_CLICK_MEDIUM;
        } else if (radiationLevel < 500.0) {
            return GEIGER_CLICK_HIGH;
        } else {
            return GEIGER_CLICK_CRITICAL;
        }
    }
    
    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
            // Geiger Counter
            GEIGER_CLICK_LOW,
            GEIGER_CLICK_MEDIUM,
            GEIGER_CLICK_HIGH,
            GEIGER_CLICK_CRITICAL,
            // Dosimeter
            DOSIMETER_BEEP,
            // Alarms
            ALARM_RADIATION_LOW,
            ALARM_RADIATION_HIGH,
            ALARM_MELTDOWN,
            // Reactor
            REACTOR_HUM,
            REACTOR_SCRAM,
            REACTOR_STARTUP,
            // HEV Suit
            HEV_POWER_ON,
            HEV_POWER_OFF,
            HEV_DAMAGE,
            HEV_LOW_ENERGY
        );
    }
}
