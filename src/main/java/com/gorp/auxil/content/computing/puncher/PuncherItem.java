package com.gorp.auxil.content.computing.puncher;

import com.gorp.auxil.content.computing.PunchCardItem;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nonnull;

public class PuncherItem extends Item {

    public PuncherItem(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if (context.getPlayer() != null && context.getPlayer()
                .isCrouching()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> openGui(context.getPlayer(), context.getHand() == Hand.OFF_HAND));
            return ActionResultType.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity.isCrouching() && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            ItemStack cards = player.getItemInHand(Hand.OFF_HAND);
            if(cards.getItem() instanceof PunchCardItem) {
                cards.setTag(new CompoundNBT());
                player.setItemInHand(Hand.OFF_HAND, cards);
            }
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (player.isCrouching()) {
            if (world.isClientSide && player.getItemInHand(Hand.OFF_HAND).getItem() instanceof PunchCardItem) {
                if(((PunchCardItem)player.getItemInHand(Hand.OFF_HAND).getItem()).small)
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> openGuiSmall(player, hand == Hand.OFF_HAND));
                else
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> openGui(player, hand == Hand.OFF_HAND));
            }
        }
        return super.use(world, player, hand);
    }

    @OnlyIn(Dist.CLIENT)
    protected void openGui(PlayerEntity player, boolean offhand) {
        ScreenOpener.open(new PuncherScreen(player, offhand));
    }

    @OnlyIn(Dist.CLIENT)
    protected void openGuiSmall(PlayerEntity player, boolean offhand) {
        ScreenOpener.open(new SmallPuncherScreen(player, offhand));
    }
}

