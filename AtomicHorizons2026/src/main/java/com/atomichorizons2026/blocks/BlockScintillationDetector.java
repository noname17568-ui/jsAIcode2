package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.radiation.RadiationManager;
import com.atomichorizons2026.tileentities.TileEntityScintillationDetector;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * Scintillation Detector - сцинтилляционный детектор.
 * Определяет ТИП радиации и источник.
 * Требует энергию для работы.
 */
public class BlockScintillationDetector extends Block {
    
    public BlockScintillationDetector() {
        super(Material.IRON);
        setTranslationKey("scintillation_detector");
        setRegistryName(AtomicHorizons2026.MODID, "scintillation_detector");
        setHardness(5.0f);
        setResistance(10.0f);
        setLightLevel(0.3f);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityScintillationDetector();
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityScintillationDetector) {
            TileEntityScintillationDetector detector = (TileEntityScintillationDetector) te;
            
            // Получаем информацию о радиации
            ChunkPos chunkPos = new ChunkPos(pos);
            double radiation = RadiationManager.getInstance().getChunkRadiation(world, chunkPos);
            
            player.sendMessage(new TextComponentString(
                TextFormatting.AQUA + String.format("Radiation: %.2f mRAD/t", radiation)));
            
            // Определяем тип радиации (в реальности это сложнее, но для простоты)
            String radiationType = "Unknown";
            if (radiation > 0) {
                // Простая логика: если есть радиация, определяем тип
                radiationType = detector.detectRadiationType();
            }
            
            player.sendMessage(new TextComponentString(
                TextFormatting.YELLOW + "Type: " + radiationType));
            
            // Показываем энергию
            player.sendMessage(new TextComponentString(
                TextFormatting.GREEN + String.format("Energy: %d / %d RF", 
                    detector.getEnergyStored(), detector.getMaxEnergyStored())));
        }
        
        return true;
    }
}
