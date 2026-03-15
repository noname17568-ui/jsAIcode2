package com.atomichorizons2026.handlers;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.blocks.*;
import com.atomichorizons2026.fluids.FluidsRegistry;
import com.atomichorizons2026.items.ItemBase;
import com.atomichorizons2026.items.ItemDosimeter;
import com.atomichorizons2026.items.ItemFuelRod;
import com.atomichorizons2026.items.ItemGeigerCounter;
import com.atomichorizons2026.items.ItemRadAway;
import com.atomichorizons2026.items.armor.ItemHazmatArmor;
import com.atomichorizons2026.items.armor.ItemHEVArmor;
import com.atomichorizons2026.items.armor.ItemLeadLinedArmor;
import com.atomichorizons2026.radiation.IRadiationSource;
import com.atomichorizons2026.tileentities.TileEntitySMRController;
import com.atomichorizons2026.tileentities.TileEntityCrusher;
import com.atomichorizons2026.tileentities.TileEntityEnrichmentChamber;
import com.atomichorizons2026.tileentities.TileEntityIndustrialSmelter;
import com.atomichorizons2026.blocks.BlockCrusher;
import com.atomichorizons2026.blocks.BlockEnrichmentChamber;
import com.atomichorizons2026.blocks.BlockIndustrialSmelter;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class RegistryHandler {
    
    // ==================== ORES ====================
    public static Block ORE_URANIUM;
    public static Block ORE_THORIUM;
    public static Block ORE_ZIRCONIUM;
    public static Block ORE_FLUORITE;
    public static Block ORE_SULFUR;
    public static Block ORE_LEAD;
    public static Block ORE_BERYLLIUM;
    public static Block ORE_BORON;
    public static Block ORE_GRAPHITE;
    public static Block ORE_RADIUM;
    public static Block ORE_MONAZITE;
    
    // ==================== BLOCKS ====================
    public static Block BLOCK_GRAPHITE;
    public static Block BLOCK_LEAD;
    public static Block BLOCK_STEEL;
    public static Block BLOCK_CONCRETE;
    public static Block BLOCK_REINFORCED_CONCRETE;
    public static Block BLOCK_RADIUM;
    
    // ==================== MACHINES ====================
    public static Block CRUSHER;
    public static Block ENRICHMENT_CHAMBER;
    public static Block INDUSTRIAL_SMELTER;
    
    // ==================== SMR ====================
    public static Block SMR_CORE;
    public static Block SMR_CASING;
    public static Block SMR_CONTROLLER;
    public static Block SMR_HOUSING;
    public static Block SMR_PORT;
    public static Block SMR_GLASS;
    public static Block CORIUM;
    
    // ==================== ITEMS ====================
    public static Item RAW_URANIUM;
    public static Item RAW_THORIUM;
    public static Item RAW_ZIRCONIUM;
    public static Item RAW_FLUORITE;
    public static Item RAW_SULFUR;
    public static Item RAW_LEAD;
    public static Item RAW_BERYLLIUM;
    public static Item RAW_BORON;
    public static Item RAW_GRAPHITE;
    public static Item RAW_RADIUM;
    
    // ==================== ENRICHED ITEMS ====================
    public static Item ENRICHED_URANIUM;
    public static Item ENRICHED_THORIUM;
    public static Item ENRICHED_ZIRCONIUM;
    public static Item ENRICHED_LEAD;
    public static Item ENRICHED_BERYLLIUM;
    public static Item ENRICHED_BORON;
    
    public static Item INGOT_URANIUM;
    public static Item INGOT_THORIUM;
    public static Item INGOT_ZIRCONIUM;
    public static Item INGOT_LEAD;
    public static Item INGOT_STEEL;
    public static Item INGOT_BERYLLIUM;
    public static Item INGOT_BORON;
    public static Item INGOT_GRAPHITE;
    public static Item INGOT_RADIUM;
    
    public static Item HALEU;
    public static Item TRISO_PARTICLES;
    public static Item FUEL_ROD_HALEU_LEGACY;
    public static Item FUEL_ROD;
    public static Item SPENT_FUEL_ROD;
    public static Item YELLOWCAKE;
    public static Item URANIUM_HEXAFLUORIDE_ITEM;
    
    public static Item HAZMAT_SPRAY;
    public static Item POTASSIUM_IODIDE;
    public static Item RADAWAY_LEGACY;
    public static Item RADX;
    
    // ==================== SPRINT 2: TOOLS ====================
    public static Item GEIGER_COUNTER;
    public static Item DOSIMETER;
    public static Item RADAWAY_TABLET;
    public static Item RADAWAY_INJECTOR;
    public static Item RADAWAY_ADVANCED;
    
    // ==================== SPRINT 2: ARMOR ====================
    public static ItemArmor HAZMAT_HELMET;
    public static ItemArmor HAZMAT_CHESTPLATE;
    public static ItemArmor HAZMAT_LEGGINGS;
    public static ItemArmor HAZMAT_BOOTS;
    
    public static ItemArmor LEAD_LINED_HELMET;
    public static ItemArmor LEAD_LINED_CHESTPLATE;
    public static ItemArmor LEAD_LINED_LEGGINGS;
    public static ItemArmor LEAD_LINED_BOOTS;
    
    public static ItemArmor HEV_HELMET;
    public static ItemArmor HEV_CHESTPLATE;
    public static ItemArmor HEV_LEGGINGS;
    public static ItemArmor HEV_BOOTS;
    
    public static Potion POTION_RADIATION;
    
    public static void preInitRegistries() {
        // Init Ores
        ORE_URANIUM = new BlockRadioactiveOre("ore_uranium", 3, 15.0f, 5, 5.0, IRadiationSource.RadiationType.GAMMA, 3.0);
        ORE_THORIUM = new BlockRadioactiveOre("ore_thorium", 3, 12.0f, 3, 1.0, IRadiationSource.RadiationType.ALPHA, 2.0);
        ORE_ZIRCONIUM = new BlockOre("ore_zirconium", 2, 10.0f, 2);
        ORE_FLUORITE = new BlockOre("ore_fluorite", 2, 8.0f, 3) { @Override public int getLightValue(net.minecraft.block.state.IBlockState state) { return 5; } };
        ORE_SULFUR = new BlockOre("ore_sulfur", 1, 6.0f, 2);
        ORE_LEAD = new BlockOre("ore_lead", 2, 8.0f, 2);
        ORE_BERYLLIUM = new BlockOre("ore_beryllium", 3, 12.0f, 4);
        ORE_BORON = new BlockOre("ore_boron", 2, 9.0f, 2);
        ORE_GRAPHITE = new BlockOre("ore_graphite", 1, 5.0f, 1);
        ORE_RADIUM = new BlockRadioactiveOre("ore_radium", 3, 20.0f, 10, 50.0, IRadiationSource.RadiationType.GAMMA, 5.0) { @Override public int getLightValue(net.minecraft.block.state.IBlockState state) { return 15; } };
        ORE_MONAZITE = new BlockOre("ore_monazite", 2, 7.0f, 3);
        
        // Init Blocks
        BLOCK_GRAPHITE = new BlockBase("block_graphite", Material.IRON, 5.0f, 2);
        BLOCK_LEAD = new BlockBase("block_lead", Material.IRON, 8.0f, 2);
        BLOCK_STEEL = new BlockBase("block_steel", Material.IRON, 10.0f, 2);
        BLOCK_CONCRETE = new BlockBase("block_concrete", Material.ROCK, 50.0f, 2);
        BLOCK_REINFORCED_CONCRETE = new BlockBase("block_reinforced_concrete", Material.IRON, 100.0f, 3);
        BLOCK_RADIUM = new BlockRadioactiveOre("block_radium", 3, 20.0f, 0, 200.0, IRadiationSource.RadiationType.GAMMA, 10.0) { @Override public int getLightValue(net.minecraft.block.state.IBlockState state) { return 15; } };
        
        // Init Machines
        CRUSHER = new com.atomichorizons2026.blocks.BlockCrusher("crusher");
        ENRICHMENT_CHAMBER = new com.atomichorizons2026.blocks.BlockEnrichmentChamber("enrichment_chamber");
        INDUSTRIAL_SMELTER = new com.atomichorizons2026.blocks.BlockIndustrialSmelter("industrial_smelter");
        
        // Init SMR
        SMR_CORE = new BlockSMRCore("smr_core");
        SMR_CASING = new BlockBase("smr_casing", Material.IRON, 15.0f, 2);
        SMR_CONTROLLER = new BlockSMRController("smr_controller");
        SMR_HOUSING = new BlockSMRHousing("smr_housing");
        SMR_PORT = new BlockSMRPort("smr_port");
        SMR_GLASS = new BlockSMRGlass("smr_glass");
        CORIUM = new BlockCorium("corium");
        
        // Init Items
        RAW_URANIUM = new ItemBase("raw_uranium");
        RAW_THORIUM = new ItemBase("raw_thorium");
        RAW_ZIRCONIUM = new ItemBase("raw_zirconium");
        RAW_FLUORITE = new ItemBase("raw_fluorite");
        RAW_SULFUR = new ItemBase("raw_sulfur");
        RAW_LEAD = new ItemBase("raw_lead");
        RAW_BERYLLIUM = new ItemBase("raw_beryllium");
        RAW_BORON = new ItemBase("raw_boron");
        RAW_GRAPHITE = new ItemBase("raw_graphite");
        RAW_RADIUM = new ItemBase("raw_radium");
        
        // Enriched items (Tier 1 processing output)
        ENRICHED_URANIUM = new ItemBase("enriched_uranium");
        ENRICHED_THORIUM = new ItemBase("enriched_thorium");
        ENRICHED_ZIRCONIUM = new ItemBase("enriched_zirconium");
        ENRICHED_LEAD = new ItemBase("enriched_lead");
        ENRICHED_BERYLLIUM = new ItemBase("enriched_beryllium");
        ENRICHED_BORON = new ItemBase("enriched_boron");
        
        INGOT_URANIUM = new ItemBase("ingot_uranium");
        INGOT_THORIUM = new ItemBase("ingot_thorium");
        INGOT_ZIRCONIUM = new ItemBase("ingot_zirconium");
        INGOT_LEAD = new ItemBase("ingot_lead");
        INGOT_STEEL = new ItemBase("ingot_steel");
        INGOT_BERYLLIUM = new ItemBase("ingot_beryllium");
        INGOT_BORON = new ItemBase("ingot_boron");
        INGOT_GRAPHITE = new ItemBase("ingot_graphite");
        INGOT_RADIUM = new ItemBase("ingot_radium");
        
        HALEU = new ItemBase("haleu");
        TRISO_PARTICLES = new ItemBase("triso_particles");
        FUEL_ROD_HALEU_LEGACY = new ItemBase("fuel_rod_haleu");
        FUEL_ROD = new ItemFuelRod("fuel_rod");
        SPENT_FUEL_ROD = new ItemBase("spent_fuel_rod");
        YELLOWCAKE = new ItemBase("yellowcake");
        URANIUM_HEXAFLUORIDE_ITEM = new ItemBase("uranium_hexafluoride");
        
        HAZMAT_SPRAY = new ItemBase("hazmat_spray");
        POTASSIUM_IODIDE = new ItemBase("potassium_iodide");
        RADAWAY_LEGACY = new ItemBase("radaway");
        RADX = new ItemBase("radx");
        
        // INIT NEW FLUIDS
        FluidsRegistry.init();
        
        // INIT TOOLS
        GEIGER_COUNTER = new ItemGeigerCounter();
        DOSIMETER = new ItemDosimeter();
        RADAWAY_TABLET = new ItemRadAway(ItemRadAway.RadAwayType.TABLET);
        RADAWAY_INJECTOR = new ItemRadAway(ItemRadAway.RadAwayType.INJECTOR);
        RADAWAY_ADVANCED = new ItemRadAway(ItemRadAway.RadAwayType.ADVANCED);
        
        // INIT ARMOR
        HAZMAT_HELMET = new ItemHazmatArmor(EntityEquipmentSlot.HEAD);
        HAZMAT_CHESTPLATE = new ItemHazmatArmor(EntityEquipmentSlot.CHEST);
        HAZMAT_LEGGINGS = new ItemHazmatArmor(EntityEquipmentSlot.LEGS);
        HAZMAT_BOOTS = new ItemHazmatArmor(EntityEquipmentSlot.FEET);
        
        LEAD_LINED_HELMET = new ItemLeadLinedArmor(EntityEquipmentSlot.HEAD);
        LEAD_LINED_CHESTPLATE = new ItemLeadLinedArmor(EntityEquipmentSlot.CHEST);
        LEAD_LINED_LEGGINGS = new ItemLeadLinedArmor(EntityEquipmentSlot.LEGS);
        LEAD_LINED_BOOTS = new ItemLeadLinedArmor(EntityEquipmentSlot.FEET);
        
        HEV_HELMET = new ItemHEVArmor(EntityEquipmentSlot.HEAD);
        HEV_CHESTPLATE = new ItemHEVArmor(EntityEquipmentSlot.CHEST);
        HEV_LEGGINGS = new ItemHEVArmor(EntityEquipmentSlot.LEGS);
        HEV_BOOTS = new ItemHEVArmor(EntityEquipmentSlot.FEET);
    }
    
    public static void initRegistries() {
        // Recipes registered in SmeltingRecipes.register()
    }
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
            ORE_URANIUM, ORE_THORIUM, ORE_ZIRCONIUM, ORE_FLUORITE, ORE_SULFUR, 
            ORE_LEAD, ORE_BERYLLIUM, ORE_BORON, ORE_GRAPHITE, ORE_RADIUM, ORE_MONAZITE,
            BLOCK_GRAPHITE, BLOCK_LEAD, BLOCK_STEEL, BLOCK_CONCRETE, BLOCK_REINFORCED_CONCRETE, BLOCK_RADIUM,
            CRUSHER, ENRICHMENT_CHAMBER, INDUSTRIAL_SMELTER,
            SMR_CORE, SMR_CASING, SMR_CONTROLLER, SMR_HOUSING, SMR_PORT, SMR_GLASS, CORIUM,
            
            // FLUIDS (Corrected)
            FluidsRegistry.BLOCK_SULFURIC_ACID,
            FluidsRegistry.BLOCK_HYDROFLUORIC_ACID,
            FluidsRegistry.BLOCK_NITRIC_ACID,
            FluidsRegistry.BLOCK_LIQUID_SODIUM,
            FluidsRegistry.BLOCK_LIQUID_LEAD_BISMUTH,
            FluidsRegistry.BLOCK_MOLTEN_SALT,
            FluidsRegistry.BLOCK_URANIUM_HEXAFLUORIDE,
            FluidsRegistry.BLOCK_MOLTEN_URANIUM,
            FluidsRegistry.BLOCK_MOLTEN_THORIUM,
            FluidsRegistry.BLOCK_NUCLEAR_WASTE
        );
        
        GameRegistry.registerTileEntity(((BlockSMRCore)SMR_CORE).getTileEntityClass(), SMR_CORE.getRegistryName());
        GameRegistry.registerTileEntity(TileEntitySMRController.class, SMR_CONTROLLER.getRegistryName());
        GameRegistry.registerTileEntity(TileEntityCrusher.class, CRUSHER.getRegistryName());
        GameRegistry.registerTileEntity(TileEntityEnrichmentChamber.class, ENRICHMENT_CHAMBER.getRegistryName());
        GameRegistry.registerTileEntity(TileEntityIndustrialSmelter.class, INDUSTRIAL_SMELTER.getRegistryName());
    }
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
            new ItemBlock(ORE_URANIUM).setRegistryName(ORE_URANIUM.getRegistryName()),
            new ItemBlock(ORE_THORIUM).setRegistryName(ORE_THORIUM.getRegistryName()),
            new ItemBlock(ORE_ZIRCONIUM).setRegistryName(ORE_ZIRCONIUM.getRegistryName()),
            new ItemBlock(ORE_FLUORITE).setRegistryName(ORE_FLUORITE.getRegistryName()),
            new ItemBlock(ORE_SULFUR).setRegistryName(ORE_SULFUR.getRegistryName()),
            new ItemBlock(ORE_LEAD).setRegistryName(ORE_LEAD.getRegistryName()),
            new ItemBlock(ORE_BERYLLIUM).setRegistryName(ORE_BERYLLIUM.getRegistryName()),
            new ItemBlock(ORE_BORON).setRegistryName(ORE_BORON.getRegistryName()),
            new ItemBlock(ORE_GRAPHITE).setRegistryName(ORE_GRAPHITE.getRegistryName()),
            new ItemBlock(ORE_RADIUM).setRegistryName(ORE_RADIUM.getRegistryName()),
            new ItemBlock(ORE_MONAZITE).setRegistryName(ORE_MONAZITE.getRegistryName()),
            
            new ItemBlock(BLOCK_GRAPHITE).setRegistryName(BLOCK_GRAPHITE.getRegistryName()),
            new ItemBlock(BLOCK_LEAD).setRegistryName(BLOCK_LEAD.getRegistryName()),
            new ItemBlock(BLOCK_STEEL).setRegistryName(BLOCK_STEEL.getRegistryName()),
            new ItemBlock(BLOCK_CONCRETE).setRegistryName(BLOCK_CONCRETE.getRegistryName()),
            new ItemBlock(BLOCK_REINFORCED_CONCRETE).setRegistryName(BLOCK_REINFORCED_CONCRETE.getRegistryName()),
            new ItemBlock(BLOCK_RADIUM).setRegistryName(BLOCK_RADIUM.getRegistryName()),
            
            new ItemBlock(CRUSHER).setRegistryName(CRUSHER.getRegistryName()),
            new ItemBlock(ENRICHMENT_CHAMBER).setRegistryName(ENRICHMENT_CHAMBER.getRegistryName()),
            new ItemBlock(INDUSTRIAL_SMELTER).setRegistryName(INDUSTRIAL_SMELTER.getRegistryName()),
            
            new ItemBlock(SMR_CORE).setRegistryName(SMR_CORE.getRegistryName()),
            new ItemBlock(SMR_CASING).setRegistryName(SMR_CASING.getRegistryName()),
            new ItemBlock(SMR_CONTROLLER).setRegistryName(SMR_CONTROLLER.getRegistryName()),
            new ItemBlock(SMR_HOUSING).setRegistryName(SMR_HOUSING.getRegistryName()),
            new ItemBlock(SMR_PORT).setRegistryName(SMR_PORT.getRegistryName()),
            new ItemBlock(SMR_GLASS).setRegistryName(SMR_GLASS.getRegistryName()),
            new ItemBlock(CORIUM).setRegistryName(CORIUM.getRegistryName()),
            
            RAW_URANIUM, RAW_THORIUM, RAW_ZIRCONIUM, RAW_FLUORITE, RAW_SULFUR, RAW_LEAD,
            RAW_BERYLLIUM, RAW_BORON, RAW_GRAPHITE, RAW_RADIUM,
            
            ENRICHED_URANIUM, ENRICHED_THORIUM, ENRICHED_ZIRCONIUM,
            ENRICHED_LEAD, ENRICHED_BERYLLIUM, ENRICHED_BORON,
            
            INGOT_URANIUM, INGOT_THORIUM, INGOT_ZIRCONIUM, INGOT_LEAD, INGOT_STEEL,
            INGOT_BERYLLIUM, INGOT_BORON, INGOT_GRAPHITE, INGOT_RADIUM,
            
            HALEU, TRISO_PARTICLES, FUEL_ROD_HALEU_LEGACY, FUEL_ROD, SPENT_FUEL_ROD, YELLOWCAKE, URANIUM_HEXAFLUORIDE_ITEM,
            
            HAZMAT_SPRAY, POTASSIUM_IODIDE, RADAWAY_LEGACY, RADX,
            
            GEIGER_COUNTER, DOSIMETER, RADAWAY_TABLET, RADAWAY_INJECTOR, RADAWAY_ADVANCED,
            
            HAZMAT_HELMET, HAZMAT_CHESTPLATE, HAZMAT_LEGGINGS, HAZMAT_BOOTS,
            LEAD_LINED_HELMET, LEAD_LINED_CHESTPLATE, LEAD_LINED_LEGGINGS, LEAD_LINED_BOOTS,
            HEV_HELMET, HEV_CHESTPLATE, HEV_LEGGINGS, HEV_BOOTS
        );
    }
    
    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        if (POTION_RADIATION != null) {
            event.getRegistry().register(POTION_RADIATION);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        registerBlockModel(ORE_URANIUM); registerBlockModel(ORE_THORIUM); registerBlockModel(ORE_ZIRCONIUM);
        registerBlockModel(ORE_FLUORITE); registerBlockModel(ORE_SULFUR); registerBlockModel(ORE_LEAD);
        registerBlockModel(ORE_BERYLLIUM); registerBlockModel(ORE_BORON); registerBlockModel(ORE_GRAPHITE);
        registerBlockModel(ORE_RADIUM); registerBlockModel(ORE_MONAZITE);
        
        registerBlockModel(BLOCK_GRAPHITE); registerBlockModel(BLOCK_LEAD); registerBlockModel(BLOCK_STEEL);
        registerBlockModel(BLOCK_CONCRETE); registerBlockModel(BLOCK_REINFORCED_CONCRETE); registerBlockModel(BLOCK_RADIUM);
        
        registerBlockModel(CRUSHER); registerBlockModel(ENRICHMENT_CHAMBER); registerBlockModel(INDUSTRIAL_SMELTER);
        
        registerBlockModel(SMR_CORE); registerBlockModel(SMR_CASING); registerBlockModel(SMR_CONTROLLER);
        registerBlockModel(SMR_HOUSING); registerBlockModel(SMR_PORT); registerBlockModel(SMR_GLASS); registerBlockModel(CORIUM);
        
        registerBlockModel(FluidsRegistry.BLOCK_SULFURIC_ACID);
        registerBlockModel(FluidsRegistry.BLOCK_HYDROFLUORIC_ACID);
        registerBlockModel(FluidsRegistry.BLOCK_NITRIC_ACID);
        registerBlockModel(FluidsRegistry.BLOCK_LIQUID_SODIUM);
        registerBlockModel(FluidsRegistry.BLOCK_LIQUID_LEAD_BISMUTH);
        registerBlockModel(FluidsRegistry.BLOCK_MOLTEN_SALT);
        registerBlockModel(FluidsRegistry.BLOCK_URANIUM_HEXAFLUORIDE);
        registerBlockModel(FluidsRegistry.BLOCK_MOLTEN_URANIUM);
        registerBlockModel(FluidsRegistry.BLOCK_MOLTEN_THORIUM);
        registerBlockModel(FluidsRegistry.BLOCK_NUCLEAR_WASTE);
        
        registerItemModel(RAW_URANIUM); registerItemModel(RAW_THORIUM); registerItemModel(RAW_ZIRCONIUM);
        registerItemModel(RAW_FLUORITE); registerItemModel(RAW_SULFUR); registerItemModel(RAW_LEAD);
        registerItemModel(RAW_BERYLLIUM); registerItemModel(RAW_BORON); registerItemModel(RAW_GRAPHITE); registerItemModel(RAW_RADIUM);
        
        registerItemModel(ENRICHED_URANIUM); registerItemModel(ENRICHED_THORIUM); registerItemModel(ENRICHED_ZIRCONIUM);
        registerItemModel(ENRICHED_LEAD); registerItemModel(ENRICHED_BERYLLIUM); registerItemModel(ENRICHED_BORON);
        
        registerItemModel(INGOT_URANIUM); registerItemModel(INGOT_THORIUM); registerItemModel(INGOT_ZIRCONIUM);
        registerItemModel(INGOT_LEAD); registerItemModel(INGOT_STEEL); registerItemModel(INGOT_BERYLLIUM);
        registerItemModel(INGOT_BORON); registerItemModel(INGOT_GRAPHITE); registerItemModel(INGOT_RADIUM);
        
        registerItemModel(HALEU); registerItemModel(TRISO_PARTICLES); registerItemModel(FUEL_ROD_HALEU_LEGACY);
        registerItemModel(FUEL_ROD); registerItemModel(SPENT_FUEL_ROD); registerItemModel(YELLOWCAKE); registerItemModel(URANIUM_HEXAFLUORIDE_ITEM);
        
        registerItemModel(HAZMAT_SPRAY); registerItemModel(POTASSIUM_IODIDE); registerItemModel(RADAWAY_LEGACY); registerItemModel(RADX);
        
        registerItemModel(GEIGER_COUNTER); registerItemModel(DOSIMETER);
        registerItemModel(RADAWAY_TABLET); registerItemModel(RADAWAY_INJECTOR); registerItemModel(RADAWAY_ADVANCED);
        
        registerItemModel(HAZMAT_HELMET); registerItemModel(HAZMAT_CHESTPLATE); registerItemModel(HAZMAT_LEGGINGS); registerItemModel(HAZMAT_BOOTS);
        registerItemModel(LEAD_LINED_HELMET); registerItemModel(LEAD_LINED_CHESTPLATE); registerItemModel(LEAD_LINED_LEGGINGS); registerItemModel(LEAD_LINED_BOOTS);
        registerItemModel(HEV_HELMET); registerItemModel(HEV_CHESTPLATE); registerItemModel(HEV_LEGGINGS); registerItemModel(HEV_BOOTS);
    }
    
    @SideOnly(Side.CLIENT)
    private static void registerBlockModel(Block block) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }
    
    @SideOnly(Side.CLIENT)
    private static void registerItemModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}