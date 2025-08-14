package com.gorp.auxil.content.computing;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PunchCardItem extends Item {
    public boolean small = false;
    public PunchCardItem(Properties properties, boolean small) {
        super(properties);
        this.small = small;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if(stack.getItem() instanceof PunchCardItem) {
            if(stack.getOrCreateTag().contains(CardData.PROGRAM_TAG))
                tooltip.addAll(CardData.deserialize(stack.getTag(), small).getLabel());
            else
                tooltip.addAll(CardData.deserialize(new CompoundNBT(), small).getLabel());
        }
    }
}
