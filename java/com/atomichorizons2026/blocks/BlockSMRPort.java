package com.atomichorizons2026.blocks;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.tileentities.TileEntitySMRController;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
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
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

/**
 * Порт SMR реактора для:
 * - Вывода энергии (RF/FE)
 * - Ввода охлаждающей жидкости
 * - Ввода/вывода топливных стержней
 */
public class BlockSMRPort extends Block {
    
    // Направление порта (куда смотрит)
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    
    public BlockSMRPort(String name) {
        super(Material.IRON);
        this.setUnlocalizedName(name);
        this.setRegistryName(AtomicHorizons2026.MODID, name);
        this.setCreativeTab(CreativeTabs.REDSTONE);
        
        this.setHardness(20.0F);
        this.setResistance(100.0F);
        this.setHarvestLevel("pickaxe", 2);
        
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }
    
    /**
     * При размещении блока — устанавливаем направление к игроку
     */
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing facing = EnumFacing.fromAngle(placer.rotationYaw).getOpposite();
        world.setBlockState(pos, state.withProperty(FACING, facing), 2);
    }
    
    /**
     * При клике — пытаемся взаимодействовать с контроллером
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        
        // Ищем контроллер рядом
        TileEntitySMRController controller = findController(world, pos);
        
        if (controller == null) {
            player.sendMessage(new TextComponentString(
                TextFormatting.RED + "No controller found! Port must be connected to SMR structure."
            ));
            return true;
        }
        
        ItemStack heldItem = player.getHeldItem(hand);
        
        // Пробуем залить жидкость
        if (!heldItem.isEmpty() && FluidUtil.getFluidHandler(heldItem) != null) {
            IFluidHandler tank = controller.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (tank != null) {
                FluidUtil.interactWithFluidHandler(player, hand, tank);
                return true;
            }
        }
        
        // Показываем информацию
        player.sendMessage(new TextComponentString(
            TextFormatting.GREEN + "=== SMR Port Connected ==="
        ));
        
        IEnergyStorage energy = controller.getCapability(CapabilityEnergy.ENERGY, null);
        if (energy != null) {
            player.sendMessage(new TextComponentString(
                "Energy: " + TextFormatting.YELLOW + energy.getEnergyStored() + " / " + energy.getMaxEnergyStored() + " RF"
            ));
        }
        
        IFluidHandler fluid = controller.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (fluid != null && fluid.getTankProperties().length > 0) {
            net.minecraftforge.fluids.FluidStack stack = fluid.getTankProperties()[0].getContents();
            if (stack != null) {
                player.sendMessage(new TextComponentString(
                    "Coolant: " + TextFormatting.AQUA + stack.amount + " mB of " + stack.getFluid().getName()
                ));
            } else {
                player.sendMessage(new TextComponentString(
                    "Coolant: " + TextFormatting.GRAY + "Empty"
                ));
            }
        }
        
        return true;
    }
    
    /**
     * Ищет контроллер SMR в радиусе 5 блоков
     */
    @Nullable
    private TileEntitySMRController findController(World world, BlockPos portPos) {
        for (int x = -5; x <= 5; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -5; z <= 5; z++) {
                    BlockPos checkPos = portPos.add(x, y, z);
                    TileEntity tile = world.getTileEntity(checkPos);
                    if (tile instanceof TileEntitySMRController) {
                        return (TileEntitySMRController) tile;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Предоставляем capabilities контроллера через порт
     */
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return false; // У порта нет своего TileEntity, он проксирует контроллер
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }
}
