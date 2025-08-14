package com.gorp.auxil.content.logistics.diode;

import com.google.common.collect.ImmutableMap;
import com.gorp.auxil.content.logistics.Tube;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.ColorHelper;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.MatrixStacker;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.DyeColor;
import net.minecraft.util.Direction;
import net.minecraft.util.text.Style;

import java.util.Map;
import java.util.Random;

public class ExchangeableDiodeRenderer extends SafeTileEntityRenderer<ExchangeableDiodeTileEntity> {
    private Random r = new Random();
    public static final Map<DyeColor, Couple<Integer>> DYE_TABLE;
    
    public ExchangeableDiodeRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }
    
    protected void renderSafe(ExchangeableDiodeTileEntity te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
        ms.pushPose();
        BlockState blockState = te.getBlockState();
        MatrixStacker.of(ms).centre().rotateY(AngleHelper.horizontalAngle(blockState.getValue(ExchangeableDiodeBlock.FACING)));
        float height = 4.0F;//(Boolean)blockState.getValue(NixieTubeBlock.CEILING) ? 2.0F : 6.0F;
        float scale = 0.05F;
        String s = te.getDisplayedStrings();
        DyeColor color = DyeColor.ORANGE;//NixieTubeBlock.colorOf(te.getBlockState());
        ms.pushPose();
        ms.scale(scale, -scale, scale);
        ms.translate(-1.0D, 0.0D, 0.0D);
        if(te.getBlockState().getValue(ExchangeableDiodeBlock.TUBE) == Tube.ELECTRON)
        this.drawTube(ms, buffer, s, height, color);
        ms.popPose();
        ms.popPose();
    }
    
    private void drawTube(MatrixStack ms, IRenderTypeBuffer buffer, String c, float height, DyeColor color) {
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        float charWidth = (float)fontRenderer.width(c);
        float shadowOffset = 0.5F;
        float flicker = this.r.nextFloat();
        Couple<Integer> couple = DYE_TABLE.get(color);
        int brightColor = couple.getFirst();
        int darkColor = couple.getSecond();
        int flickeringBrightColor = ColorHelper.mixColors(brightColor, darkColor, flicker / 4.0F);
        ms.pushPose();
        ms.translate((charWidth - shadowOffset) / -2.0F, -height, 0.0D);
        drawChar(ms, buffer, c, flickeringBrightColor);
        ms.pushPose();
        ms.translate(shadowOffset, shadowOffset, -0.0625D);
        drawChar(ms, buffer, c, darkColor);
        ms.popPose();
        ms.popPose();
        ms.pushPose();
        ms.scale(-1.0F, 1.0F, 1.0F);
        ms.translate((charWidth - shadowOffset) / -2.0F, -height, 0.0D);
        drawChar(ms, buffer, c, darkColor);
        ms.pushPose();
        ms.translate(-shadowOffset, shadowOffset, -0.0625D);
        drawChar(ms, buffer, c, ColorHelper.mixColors(darkColor, 0, 0.35F));
        ms.popPose();
        ms.popPose();
    }
    
    private static void drawChar(MatrixStack ms, IRenderTypeBuffer buffer, String c, int color) {
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        fontRenderer.drawInBatch(c, 0.0F, 0.0F, color, false, ms.last().pose(), buffer, false, 0, 15728880);
        if (buffer instanceof IRenderTypeBuffer.Impl) {
            ((IRenderTypeBuffer.Impl)buffer).endBatch(RenderType.text(Style.DEFAULT_FONT));
        }
    }
    
    static {
        DYE_TABLE = (new ImmutableMap.Builder()).put(DyeColor.BLACK, Couple.create(4538427, 2170911)).put(DyeColor.RED, Couple.create(11614519, 6498103)).put(DyeColor.GREEN, Couple.create(2132550, 1925189)).put(DyeColor.BROWN, Couple.create(11306332, 6837054)).put(DyeColor.BLUE, Couple.create(5476833, 5262224)).put(DyeColor.GRAY, Couple.create(6121071, 3224888)).put(DyeColor.LIGHT_GRAY, Couple.create(9803419, 7368816)).put(DyeColor.PURPLE, Couple.create(10441902, 6501996)).put(DyeColor.CYAN, Couple.create(4107188, 3962994)).put(DyeColor.PINK, Couple.create(14002379, 12086165)).put(DyeColor.LIME, Couple.create(10739541, 5222767)).put(DyeColor.YELLOW, Couple.create(15128406, 15313961)).put(DyeColor.LIGHT_BLUE, Couple.create(6934226, 5278373)).put(DyeColor.ORANGE, Couple.create(15635014, 14240039)).put(DyeColor.MAGENTA, Couple.create(15753904, 12600456)).put(DyeColor.WHITE, Couple.create(15592165, 12302000)).build();
    }
}
