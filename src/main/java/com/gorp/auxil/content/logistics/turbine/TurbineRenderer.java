package com.gorp.auxil.content.logistics.turbine;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.render.PartialBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

public class TurbineRenderer extends KineticTileEntityRenderer {
    public TurbineRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }
    
    protected void renderSafe(KineticTileEntity te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
        if (!Backend.getInstance().canUseInstancing(te.getLevel())) {
            BlockState blockState = te.getBlockState();
            Direction direction = te.getBlockState().getValue(BlockStateProperties.FACING);
            IVertexBuilder vb = buffer.getBuffer(RenderType.cutoutMipped());
            int lightBehind = WorldRenderer.getLightColor(te.getWorld(), te.getBlockPos().relative(direction.getOpposite()));
            SuperByteBuffer cog = PartialBufferer.get(AllBlockPartials.SHAFTLESS_COGWHEEL, blockState);
            float time = AnimationTickHolder.getRenderTime(te.getWorld());
            float cogsSpeed = te.getGeneratedSpeed();
            float angle = time * cogsSpeed * 3.0F / 10.0F % 360.0F;
            angle = angle / 180.0F * 3.1415927F;
            kineticRotationTransform(cog , te, direction.getAxis(), angle, lightBehind).renderInto(ms, vb);
        }
    }
}

