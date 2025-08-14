package com.gorp.auxil;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.foundation.gui.IScreenRenderable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum AllGuiTextures implements IScreenRenderable {

    PUNCH_CARD_PROGRAMMER("punch_card_programmer.png", 85, 190),
    SMALL_PUNCH_CARD_PROGRAMMER("small_punch_card_programmer.png", 85, 99),
    PUNCH_CARD_PROGRAMMER_PUNCHED("punch_card_programmer.png", 85, 0, 10, 10),
    PUNCH_CARD_PROGRAMMER_NOT_PUNCHED("punch_card_programmer.png", 96, 0, 10, 10),
    PUNCH_CARD_PROGRAMMER_HOVERED("punch_card_programmer.png",85, 11, 10, 10);

    public final ResourceLocation location;
    public int width;
    public int height;
    public int startX;
    public int startY;

    private AllGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    private AllGuiTextures(String location, int startX, int startY, int width, int height) {
        this.location = new ResourceLocation("auxil", "textures/gui/" + location);
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @OnlyIn(Dist.CLIENT)
    public void bind() {
        Minecraft.getInstance().getTextureManager().bind(this.location);
    }

    @OnlyIn(Dist.CLIENT)
    public void draw(MatrixStack ms, AbstractGui screen, int x, int y) {
        this.bind();
        screen.blit(ms, x, y, this.startX, this.startY, this.width, this.height);
    }
}
