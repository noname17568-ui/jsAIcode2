# MR1 - Stabilization and Critical Fixes

## Overview

This merge request addresses critical bugs and stabilization issues identified in the technical audit (Rewiew.md). All fixes maintain backward compatibility and ensure the mod compiles and runs correctly.

## Critical Bugs Fixed

### 1. Ore Generation Incomplete (Bug #1)

**Problem**: Only 3 out of 11 registered ores were generating in the world, making 8 ores unobtainable in survival mode.

**Impact**: Players could not progress through the mod's tech tree without creative mode.

**Fix**: Added world generation for all 8 missing ores:
- Fluorite (glowing ore, Y: 10-50, vein size: 6, 4 veins/chunk)
- Sulfur (common, Y: 5-40, vein size: 8, 6 veins/chunk)
- Lead (radiation shielding, Y: 10-45, vein size: 7, 5 veins/chunk)
- Beryllium (reactor material, Y: 5-30, vein size: 3, 2 veins/chunk)
- Boron (control rods, Y: 10-40, vein size: 5, 3 veins/chunk)
- Graphite (moderator, Y: 20-60, vein size: 7, 5 veins/chunk)
- Radium (highly radioactive, glowing, Y: 5-20, vein size: 2, 1 vein/chunk)
- Monazite (thorium source, Y: 15-50, vein size: 5, 4 veins/chunk)

**Files Modified**:
- `AtomicHorizons2026/src/main/java/com/atomichorizons2026/worldgen/OreWorldGen.java`

---

### 2. Radiation Capability Not Registered (Bug #4)

**Problem**: PlayerRadiationCapability was used but never registered with Forge's CapabilityManager, causing NullPointerException when accessing player radiation data.

**Impact**: Radiation system completely broken, game crashes when players enter contaminated areas.

**Fix**: 
- Created `RadiationCapabilityStorage` for NBT serialization
- Created `RadiationCapabilityFactory` for capability instantiation
- Created `RadiationCapabilityProvider` for attaching to players
- Created `CapabilityHandler` to manage capability lifecycle
- Registered capability in preInit before any usage
- Added capability cloning on player death/respawn (preserves 50% radiation)

**Files Created**:
- `AtomicHorizons2026/src/main/java/com/atomichorizons2026/radiation/RadiationCapabilityStorage.java`
- `AtomicHorizons2026/src/main/java/com/atomichorizons2026/radiation/RadiationCapabilityFactory.java`
- `AtomicHorizons2026/src/main/java/com/atomichorizons2026/radiation/RadiationCapabilityProvider.java`
- `AtomicHorizons2026/src/main/java/com/atomichorizons2026/handlers/CapabilityHandler.java`

**Files Modified**:
- `AtomicHorizons2026/src/main/java/com/atomichorizons2026/AtomicHorizons2026.java`

---

### 3. Translation Key Format Issues

**Problem**: BlockBase and ItemBase used incorrect translation key formats, causing localization issues and potential conflicts with other mods.

**Impact**: Blocks and items displayed with wrong names or missing translations.

**Fix**:
- BlockBase: Changed from `"tile." + name` to `modid + "." + name`
- ItemBase: Changed from `name` to `modid + "." + name`

**Files Modified**:
- `AtomicHorizons2026/src/main/java/com/atomichorizons2026/blocks/BlockBase.java`
- `AtomicHorizons2026/src/main/java/com/atomichorizons2026/items/ItemBase.java`

---

### 4. Fluid Registration Order (Bug #5)

**Problem**: Fluids were initialized AFTER blocks that reference them, causing potential null pointer exceptions.

**Impact**: Fluid blocks may not render correctly or cause crashes during initialization.

**Fix**: Moved `FluidsRegistry.init()` to the beginning of `preInitRegistries()` to ensure fluids are created before any blocks that reference them.

**Initialization Order** (now correct):
1. FluidsRegistry.init() - FIRST
2. Block initialization - SECOND
3. Item initialization - THIRD

**Files Modified**:
- `AtomicHorizons2026/src/main/java/com/atomichorizons2026/handlers/RegistryHandler.java`

---

## Architecture Improvements

### Capability System

Implemented proper Forge capability pattern:
- Storage class for NBT serialization
- Factory class for instantiation
- Provider class for attachment
- Handler class for lifecycle management
- Event-based attachment to players
- Proper cloning on death/respawn

### Initialization Order

Documented and enforced proper initialization order:
1. Capabilities (preInit, before everything)
2. Fluids (preInit, before blocks)
3. Blocks (preInit)
4. Items (preInit)
5. TileEntities (block registration event)
6. Recipes (init)

---

## Testing Checklist

- [x] Mod compiles without errors
- [x] All 11 ores generate in new worlds
- [x] Radiation capability works without crashes
- [x] Fluid blocks render correctly
- [x] All blocks/items appear in creative tab
- [x] Translation keys follow proper format
- [x] No duplicate registry entries
- [x] TileEntities register correctly

---

## Files Changed Summary

### Created (4 files)
- `radiation/RadiationCapabilityStorage.java` - NBT serialization for capability
- `radiation/RadiationCapabilityFactory.java` - Capability factory
- `radiation/RadiationCapabilityProvider.java` - Capability provider
- `handlers/CapabilityHandler.java` - Capability lifecycle management

### Modified (5 files)
- `worldgen/OreWorldGen.java` - Added 8 missing ore generations
- `AtomicHorizons2026.java` - Added capability registration in preInit
- `blocks/BlockBase.java` - Fixed translation key format
- `items/ItemBase.java` - Fixed translation key format
- `handlers/RegistryHandler.java` - Fixed fluid initialization order

---

## Backward Compatibility

✅ **Fully backward compatible**
- No API changes
- No breaking changes to existing systems
- Existing worlds will work (new ores will generate in unexplored chunks)
- Radiation data preserved on player death (50% decay)

---
## Next Steps (MR2)

After this MR is merged, the next phase will implement:
- Heat System (HeatCapability, HeatStorage, HeatNetwork)
- Chemical Reaction Framework (ChemicalReaction, ChemicalRecipe)
- Typed Radiation System (Alpha, Beta, Gamma, Neutron)

See workflow document for full roadmap.
