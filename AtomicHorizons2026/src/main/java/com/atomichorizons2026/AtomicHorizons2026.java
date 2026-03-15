package com.atomichorizons2026;

import com.atomichorizons2026.handlers.RegistryHandler;
import com.atomichorizons2026.handlers.SoundHandler;
import com.atomichorizons2026.proxy.CommonProxy;
import com.atomichorizons2026.worldgen.OreWorldGen;
import net.minecraft.creativetab.CreativeTabs; // Добавлено в прошлый раз
import net.minecraft.item.ItemStack; // НОВОЕ: для ItemStack
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side; // НОВОЕ: для Side
import net.minecraftforge.fml.relauncher.SideOnly; // НОВОЕ: для SideOnly
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = AtomicHorizons2026.MODID,
    name = AtomicHorizons2026.NAME,
    version = AtomicHorizons2026.VERSION,
    acceptedMinecraftVersions = "[1.12.2]",
    dependencies = "after:jei;after:mekanism;after:thermalexpansion"
)
public class AtomicHorizons2026 {
    public static CreativeTabs CREATIVE_TAB;
    public static final String MODID = "atomichorizons2026";
    public static final String NAME = "Atomic Horizons: 2026";
    public static final String VERSION = "1.0.1-beta";
    public static final String CLIENT_PROXY = "com.atomichorizons2026.proxy.ClientProxy";
    public static final String COMMON_PROXY = "com.atomichorizons2026.proxy.CommonProxy";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Instance(MODID)
    public static AtomicHorizons2026 instance;

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Initializing Atomic Horizons: 2026 - Next Generation Nuclear Technology");
        LOGGER.info("Sprint 2: Fluids, Tools, and Radiation Protection Armor");
        CREATIVE_TAB = new CreativeTabs(MODID) {
            @Override
            @SideOnly(Side.CLIENT)
            public ItemStack createIcon() {
                // Здесь выберите иконку для вашей вкладки, например, урановую руду
                return new ItemStack(RegistryHandler.ORE_URANIUM); 
            }
        };
        // Initialize sounds
        SoundHandler.preInit();
        
        // Register blocks and items
        RegistryHandler.preInitRegistries();
        
        // Register world generation
        GameRegistry.registerWorldGenerator(new OreWorldGen(), 0);
        
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Initializing recipes and integrations...");
        
        // Register recipes
        RegistryHandler.initRegistries();
        
        // Register smelting recipes (Tier 0)
        com.atomichorizons2026.crafting.SmeltingRecipes.register();
        
        // Register machine recipes (Tier 1)
        com.atomichorizons2026.crafting.MachineRecipeRegistry.init();
        
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LOGGER.info("Atomic Horizons: 2026 successfully loaded!");
        LOGGER.info("Features loaded:");
        LOGGER.info("  - 11 new ores with realistic distribution");
        LOGGER.info("  - Radiation system with chunk-based contamination");
        LOGGER.info("  - Environmental effects (grass dies, crops destroyed, leaves wither)");
        LOGGER.info("  - Inventory radiation from radioactive items");
        LOGGER.info("  - 10 industrial fluids (acids, coolants, nuclear materials)");
        LOGGER.info("  - Energy system with 5 voltage tiers (LV/MV/HV/EV/UV)");
        LOGGER.info("  - Tier 1 chemical processing machines:");
        LOGGER.info("     * Crusher (ore -> 2x dust)");
        LOGGER.info("     * Enrichment Chamber (dust -> enriched dust)");
        LOGGER.info("     * Industrial Smelter (dust -> ingot)");
        LOGGER.info("  - Capacitor Banks for energy storage");
        LOGGER.info("  - Geiger Counter and Dosimeter tools");
        LOGGER.info("  - RadAway anti-radiation medicine");
        LOGGER.info("  - 3 tiers of radiation protection armor");
        LOGGER.info("     * Hazmat Suit (50% protection)");
        LOGGER.info("     * Lead-Lined Armor (90% protection, slowness)");
        LOGGER.info("     * HEV Suit (85% protection, RF-powered)");
        LOGGER.info("  - SMR Reactor (Tier 1, 30-150 MWe)");
        
        proxy.postInit(event);
    }
}
