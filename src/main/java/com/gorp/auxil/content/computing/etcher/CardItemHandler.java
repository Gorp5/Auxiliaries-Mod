package com.gorp.auxil.content.computing.etcher;

import com.gorp.auxil.content.computing.PunchCardItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class CardItemHandler extends ItemStackHandler {

    public CardItemHandler() {
        super();
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        return stack.getItem() instanceof PunchCardItem;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 1;
    }
}
