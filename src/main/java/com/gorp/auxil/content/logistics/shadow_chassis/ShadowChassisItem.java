package com.gorp.auxil.content.logistics.shadow_chassis;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.DistExecutor;

public class ShadowChassisItem extends BlockItem {

    public ShadowChassisItem(Block block, Properties properties) {
        super(block, properties);
    }
    
    @Override
    public ActionResultType useOn(ItemUseContext ctx) {
        return ctx.getPlayer().isCrouching() ? ActionResultType.SUCCESS : super.useOn(ctx);
    }

    @Override
    public void releaseUsing(ItemStack pStack, World pWorldIn, LivingEntity pEntityLiving, int pTimeLeft) {
        super.releaseUsing(pStack, pWorldIn, pEntityLiving, pTimeLeft);
    }
    
    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        return super.getDestroySpeed(pStack, pState);
    }
    
    @Override
    protected boolean placeBlock(BlockItemUseContext pContext, BlockState pState) {
        if (pContext.getLevel().isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ShadowChassisHandler.flushSettings(pContext.getClickedPos()));
        }
        return super.placeBlock(pContext, pState);
    }
}
