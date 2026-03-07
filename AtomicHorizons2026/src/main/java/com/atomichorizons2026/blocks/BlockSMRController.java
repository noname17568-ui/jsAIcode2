package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.tileentities.TileEntitySMRController;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

/**
 * Контроллер SMR реактора — "мозг" мультиблочной структуры.
 * Управляет состоянием реактора и отображает информацию.
 */
public class BlockSMRController extends Block {
    
    // Свойство для отображения состояния (активен/неактивен)
    public static final PropertyBool ACTIVE = PropertyBool.create("active");
    
    public BlockSMRController(String name) {
        super(Material.IRON);
        this.setTranslationKey(name);
        this.setRegistryName(AtomicHorizons2026.MODID, name);
        this.setCreativeTab(CreativeTabs.REDSTONE);
        
        // Характеристики
        this.setHardness(15.0F);
        this.setResistance(100.0F);
        this.setHarvestLevel("pickaxe", 2);
        
        // Устанавливаем начальное состояние
        this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, false));
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ACTIVE) ? 1 : 0;
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ACTIVE, meta == 1);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntitySMRController();
    }
    
    /**
     * При размещении блока игроком
     */
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        
        if (!world.isRemote && placer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) placer;
            player.sendMessage(new TextComponentString(
                TextFormatting.YELLOW + "SMR Controller placed. Build a 3x3x4 structure with Housing blocks."
            ));
        }
    }
    
    /**
     * При клике правой кнопкой — показываем статус
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntitySMRController)) return false;
        
        TileEntitySMRController controller = (TileEntitySMRController) tile;
        
        // Показываем информацию о реакторе
        if (player.isSneaking()) {
            // Shift+ПКМ — экстренная остановка
            controller.emergencyScram();
            player.sendMessage(new TextComponentString(
                TextFormatting.RED + "EMERGENCY SCRAM INITIATED!"
            ));
        } else {
            // Обычный ПКМ — статус
            showStatus(player, controller);
        }
        
        return true;
    }
    
    /**
     * Показывает статус реактора игроку
     */
    private void showStatus(EntityPlayer player, TileEntitySMRController controller) {
        TextFormatting color;
        String status;
        
        if (!controller.isFormed()) {
            color = TextFormatting.RED;
            status = "INCOMPLETE STRUCTURE";
        } else if (controller.isMeltdown()) {
            color = TextFormatting.DARK_RED;
            status = "MELTDOWN DETECTED";
        } else if (controller.getCoreTemperature() >= TileEntitySMRController.TEMP_CRITICAL) {
            color = TextFormatting.RED;
            status = "CRITICAL TEMPERATURE";
        } else if (controller.getCoreTemperature() >= TileEntitySMRController.TEMP_HIGH) {
            color = TextFormatting.GOLD;
            status = "HIGH TEMPERATURE";
        } else if (controller.getCoreTemperature() >= TileEntitySMRController.TEMP_OPTIMAL) {
            color = TextFormatting.GREEN;
            status = "OPTIMAL";
        } else {
            color = TextFormatting.BLUE;
            status = "COLD START";
        }
        
        player.sendMessage(new TextComponentString(
            TextFormatting.BOLD + "=== SMR Status ==="
        ));
        player.sendMessage(new TextComponentString(
            "Status: " + color + status
        ));
        player.sendMessage(new TextComponentString(
            "Temperature: " + TextFormatting.YELLOW + String.format("%.1f", controller.getCoreTemperature()) + "°C"
        ));
        player.sendMessage(new TextComponentString(
            "Energy Output: " + TextFormatting.GREEN + controller.getCurrentEnergyOutput() + " RF/t"
        ));
        player.sendMessage(new TextComponentString(
            "Energy Buffer: " + TextFormatting.AQUA + controller.getEnergyStored() + " / " + controller.getMaxEnergyStored() + " RF"
        ));
        player.sendMessage(new TextComponentString(
            "Control Rods: " + TextFormatting.GRAY + String.format("%.0f%%", controller.getControlRodInsertion() * 100)
        ));
        
        // Предупреждения
        if (controller.getCoreTemperature() >= TileEntitySMRController.TEMP_CRITICAL) {
            player.sendMessage(new TextComponentString(
                TextFormatting.RED + TextFormatting.BOLD.toString() + "⚠ WARNING: RISK OF MELTDOWN!"
            ));
        }
        
        player.sendMessage(new TextComponentString(
            TextFormatting.GRAY + "[Shift+Click for Emergency SCRAM]"
        ));
    }
    
    /**
     * Обновление состояния блока на основе TileEntity
     */
    public static void updateBlockState(boolean active, World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockSMRController) {
            world.setBlockState(pos, state.withProperty(ACTIVE, active), 3);
        }
    }
    
    /**
     * При разрушении блока
     */
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntitySMRController) {
            TileEntitySMRController controller = (TileEntitySMRController) tile;
            
            // Если реактор активен — предупреждаем
            if (controller.isFormed() && !world.isRemote) {
                AtomicHorizons2026.LOGGER.warn("SMR Controller broken at " + pos + "! Reactor shutting down.");
            }
        }
        
        super.breakBlock(world, pos, state);
    }
    
    /**
     * Светится, когда активен
     */
    @Override
    public int getLightValue(IBlockState state) {
        return state.getValue(ACTIVE) ? 10 : 0;
    }
    
    /**
     * Блок не пропускает свет
     */
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }
    
    /**
     * Полный куб
     */
    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }
}
