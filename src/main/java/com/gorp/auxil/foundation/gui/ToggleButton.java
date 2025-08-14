package com.gorp.auxil.foundation.gui;

import com.gorp.auxil.AllGuiTextures;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.widgets.AbstractSimiWidget;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class ToggleButton extends AbstractSimiWidget {

    private AllGuiTextures normal_hover = AllGuiTextures.PUNCH_CARD_PROGRAMMER_HOVERED;
    private AllGuiTextures normal = AllGuiTextures.PUNCH_CARD_PROGRAMMER_NOT_PUNCHED;
    private AllGuiTextures press = AllGuiTextures.PUNCH_CARD_PROGRAMMER_PUNCHED;
    public boolean pressed;
    private boolean hovered;

    public ToggleButton(int x, int y, boolean toggle) {
        super(x, y, 10, 10);
        this.pressed = toggle;
    }

    @Override
    public void renderButton(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.hovered =
                    mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            if(hovered) {
                normal_hover.draw(matrixStack, this, x, y);
            } else {

                if (pressed) {
                    press.draw(matrixStack, this, x, y);
                } else {
                    normal.draw(matrixStack, this, x, y);
                }
            }
            RenderSystem.disableAlphaTest();
        }
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        super.onClick(p_onClick_1_, p_onClick_3_);
        this.pressed = !pressed;
    }

    @Override
    public void onRelease(double p_onRelease_1_, double p_onRelease_3_) {
        super.onRelease(p_onRelease_1_, p_onRelease_3_);
    }

    public void setToolTip(ITextComponent text) {
        toolTip.clear();
        toolTip.add(text);
    }
}