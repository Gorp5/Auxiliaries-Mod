package com.gorp.auxil.content.computing.etcher;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class EtcherRenderer extends KineticTileEntityRenderer {
    public EtcherRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    protected void renderSafe(KineticTileEntity te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
        if(te instanceof EtcherTileEntity && !((EtcherTileEntity)te).isTop())
            super.renderSafe(te, partialTicks, ms, buffer, light, overlay);
    }

    protected SuperByteBuffer getRotatedModel(KineticTileEntity te) {
        return CreateClient.BUFFER_CACHE.renderPartial(AllBlockPartials.SHAFTLESS_COGWHEEL, te.getBlockState());
    }
}
