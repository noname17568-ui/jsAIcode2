package com.atomichorizons2026.items;

import com.atomichorizons2026.AtomicHorizons2026;
import com.atomichorizons2026.radiation.PlayerRadiationCapability;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Portable Decontamination Unit - одноразовый портативный дезактиватор.
 * Снимает 100 RAD за одно использование.
 * После использования исчезает.
 */
public class ItemPortableDecontaminationUnit extends ItemBase {
    
    public static final double RADIATION_REDUCTION = 100.0; // RAD
    
    public ItemPortableDecontaminationUnit() {
        super("portable_decontamination_unit");
        setMaxStackSize(16);
        // Creative tab already set in ItemBase constructor
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (world.isRemote) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        
        PlayerRadiationCapability cap = player.getCapability(
            PlayerRadiationCapability.RADIATION_CAPABILITY, null);
        
        if (cap != null && cap.getRadiationDose() > 0) {
            double oldDose = cap.getRadiationDose();
            cap.reduceRadiation(RADIATION_REDUCTION);
            double newDose = cap.getRadiationDose();
            double reduced = oldDose - newDose;
            
            player.sendMessage(new TextComponentString(
                TextFormatting.GREEN + "Decontamination complete! Removed " + 
                    String.format("%.1f", reduced) + " RAD"));
            
            // Одноразовый - исчезает
            stack.shrink(1);
            
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else {
            player.sendMessage(new TextComponentString(
                TextFormatting.YELLOW + "No radiation detected"));
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        tooltip.add(TextFormatting.AQUA + I18n.format("portable_decon.reduction", 
            (int)RADIATION_REDUCTION));
        
        tooltip.add(TextFormatting.GRAY + I18n.format("portable_decon.desc"));
        tooltip.add(TextFormatting.RED + I18n.format("portable_decon.single_use"));
    }
}
