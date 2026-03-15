# ☢️ Atomic Horizons: 2026

**Next-generation nuclear technology mod for Minecraft 1.12.2**

Based on real 2025-2026 nuclear trends: SMR (Small Modular Reactors), Generation IV reactors, AI-powered control systems, and advanced fuel cycles.

---

## 🎯 Features

### Current Implementation (Stage 1 - Core)

- **New Ores**: Uranium, Thorium (Monazite), Zirconium
- **Materials**: HALEU, TRISO Particles, Graphite, Steel, Lead
- **SMR Core**: Basic Small Modular Reactor generating 5,000 RF/t
- **Fuel System**: HALEU fuel rods with 10-hour burn time
- **World Generation**: Realistic ore distribution
- **Passive Safety**: Automatic SCRAM on overheating

### Planned Features (Roadmap)

#### Stage 2 - Multiblock SMR
- Full 3x3x4 SMR structure
- Steam generator integration
- Control rod mechanics
- Temperature monitoring

#### Stage 3 - Chemistry & Processing
- Molten Salt Reactor (MSR)
- Thorium fuel cycle
- Centrifuge for waste processing
- Chemical reactor

#### Stage 4 - Disasters
- Radiation system (chunk-based contamination)
- Corium (meltdown material)
- Hazmat cleanup
- Mutations

#### Stage 5 - AI & Advanced Systems
- Neural Core Controller
- Predictive maintenance
- Cyber attack events
- Tokamak fusion reactor

---

## 🛠️ Building from Source

### Prerequisites

- Java 8 JDK
- Gradle (wrapper included)

### Build Instructions

```bash
# Clone the repository
git clone https://github.com/yourusername/AtomicHorizons2026.git
cd AtomicHorizons2026

# Build the mod
./gradlew build

# The compiled mod will be in:
# build/libs/AtomicHorizons2026-1.0.0-beta.jar
```

### Development Setup

```bash
# Setup development environment
./gradlew setupDecompWorkspace

# For IntelliJ IDEA
./gradlew idea

# For Eclipse
./gradlew eclipse

# Run client
./gradlew runClient
```

---

## 📋 Dependencies

### Required
- Minecraft Forge 1.12.2-14.23.5.2847+

### Optional (Integration)
- JEI (Just Enough Items) - Recipe viewing
- Mekanism - Gas/energy compatibility
- Thermal Expansion - RF/Fluid compatibility
- ComputerCraft/OpenComputers - Reactor control API

---

## 🎮 Gameplay Guide

### Getting Started

1. **Find Uranium Ore** (Y: 5-30) - Rare, deep underground
2. **Mine Thorium Ore** (Y: 20-60) - More common, higher up
3. **Collect Zirconium** (Y: 10-45) - For fuel rod casings

### Crafting Your First Reactor

1. Craft **HALEU** from Raw Uranium + Graphite
2. Make **TRISO Particles** (ceramic-coated fuel)
3. Create **HALEU Fuel Rod** with Zircaloy casing
4. Build **SMR Casing** (Steel + Lead)
5. Assemble **SMR Core**

### Operating the SMR

- Insert fuel rod into SMR Core
- Reactor generates 5,000 RF/t automatically
- Temperature is monitored (max 350°C)
- Passive safety: auto-SCRAM on overheating
- Spent fuel must be cooled before reprocessing

---

## 🔬 Technical Details

### Energy System
- Uses Forge Energy (FE/RF) API
- Compatible with most tech mods
- Energy output: 5,000 RF/t (SMR Core)

### Fuel Cycle
- HALEU Fuel Rod: 720,000 ticks (10 hours real time)
- Spent Fuel: Converted automatically when depleted
- Future: Reprocessing in Centrifuge

### Safety Systems
- Passive cooling (no pumps required)
- Automatic SCRAM at 350°C
- No explosion (realistic SMR safety)

---

## 📁 Project Structure

```
AtomicHorizons2026/
├── src/main/java/com/atomichorizons2026/
│   ├── AtomicHorizons2026.java      # Main mod class
│   ├── blocks/                       # Block definitions
│   ├── items/                        # Item definitions
│   ├── tileentities/                 # Tile entity logic
│   ├── worldgen/                     # Ore generation
│   ├── handlers/                     # Registry handlers
│   └── proxy/                        # Client/Server proxies
├── src/main/resources/
│   ├── assets/atomichorizons2026/
│   │   ├── blockstates/              # Block state JSONs
│   │   ├── models/                   # Block/Item models
│   │   ├── textures/                 # Textures (placeholders)
│   │   ├── recipes/                  # Crafting recipes
│   │   └── lang/                     # Localization
│   └── mcmod.info                    # Mod metadata
└── build.gradle                      # Build configuration
```

---

## 🤝 Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Submit a pull request

### Areas Needing Help

- **Textures**: All blocks/items need 16x16 textures
- **Models**: Advanced multiblock structures
- **Sounds**: Reactor hum, alarm sounds
- **Documentation**: In-game guide book
- **Balance**: Fuel burn times, energy output

---

## 📜 License

This project is licensed under the MIT License.

---

## 🙏 Credits

- Inspired by real SMR technology: NuScale, RITM-200
- Generation IV reactor concepts from GIF (Generation IV International Forum)
- Original concept and design by the Atomic Horizons team

---

## 📞 Support

- GitHub Issues: [Report bugs here]
- Discord: [Community server]
- Wiki: [Coming soon]

---

**⚠️ Disclaimer**: This mod simulates nuclear technology for entertainment purposes. Real nuclear reactors require extensive safety training and regulatory compliance.
