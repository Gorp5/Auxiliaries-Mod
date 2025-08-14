package com.gorp.auxil.content.logistics.radiant_chassis;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class RadiantChassisItem extends BlockItem {
    public RadiantChassisItem(Block block, Properties props) {
        super(block, props);
    }
    
    @Override
    public ActionResultType useOn(ItemUseContext ctx) {
        return ctx.getPlayer().isCrouching() ? ActionResultType.SUCCESS : super.useOn(ctx);
    }
    
    @Override
    protected boolean placeBlock(BlockItemUseContext pContext, BlockState pState) {
        if (pContext.getLevel().isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RadiantChassisHandler.flushSettings(pContext.getClickedPos()));
        }
        
        return super.placeBlock(pContext, pState);
    }
}
