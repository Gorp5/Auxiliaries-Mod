package com.gorp.auxil.content.computing.puncher;

import com.gorp.auxil.Auxiliaries;
import com.gorp.auxil.AllGuiTextures;
import com.gorp.auxil.AllItems;
import com.gorp.auxil.content.computing.CardData;
import com.gorp.auxil.content.computing.PunchCardItem;
import com.gorp.auxil.foundation.gui.ToggleButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.GuiGameElement;
import com.simibubi.create.foundation.gui.widgets.IconButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Vector;

public class PuncherScreen extends AbstractSimiScreen {
    private final ItemStack renderedItem;
    private final AllGuiTextures background;
    private Vector<Vector<ToggleButton>> buttons;
    private IconButton confirmButton, toggleButton;
    private final ITextComponent title;
    private PlayerEntity player;
    private Hand cardHand;
    private boolean toggle = true;

    public PuncherScreen(PlayerEntity player, boolean offhand) {
        this.renderedItem = AllItems.PUNCHER.asStack();
        this.background = AllGuiTextures.PUNCH_CARD_PROGRAMMER;
        this.title = new TranslationTextComponent(Auxiliaries.MODID + ".gui.punch_card.title", new Object[0]);
        this.player = player;
        this.cardHand = offhand ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    protected void init() {
        this.setWindowSize(this.background.width + 50, this.background.height);
        super.init();
        this.widgets.clear();
        buttons = new Vector<>();
        ItemStack stack = player.getItemInHand(cardHand);
        int offsetX = this.guiLeft + 17; int offsetY = this.guiTop + 27;
        if(stack.getItem() instanceof PunchCardItem && stack.getOrCreateTag().contains(CardData.PROGRAM_TAG)) {
            CardData program = CardData.deserialize(stack.getOrCreateTag(), false);
            if(program != null) {
                for(int x = 0; x < 8; x++) {
                    Vector<ToggleButton> vector = new Vector<>();
                    for(int y = 0; y < 4; y++)
                        vector.add(new ToggleButton(offsetX + y * 14, offsetY + x * 15, ((program.getData()[x] >> y) & 1) == 1));
                    buttons.add(vector);
                }
            }
        } else {
            for(int x = 0; x < 8; x++) {
                Vector<ToggleButton> vector = new Vector<>();
                for(int y = 0; y < 4; y++)
                    vector.add(new ToggleButton(offsetX + y * 14, offsetY + x * 15, false));
                buttons.add(vector);
            }
        }

        for(int x = 0; x < buttons.size(); x++)
            this.widgets.addAll(buttons.get(x));

        this.confirmButton = new IconButton(this.guiLeft + 19, this.guiTop + 165, AllIcons.I_CONFIRM);
        this.toggleButton = new IconButton(this.guiLeft + 48, this.guiTop + 165, AllIcons.I_MTD_REPLAY);
        this.widgets.add(this.confirmButton);
        this.widgets.add(this.toggleButton);
    }

    protected void renderWindow(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.background.draw(matrixStack, this, this.guiLeft, this.guiTop);
        this.font.draw(matrixStack, this.title, (float)(this.guiLeft + 6), (float)(this.guiTop + 2), 16777215);
        GuiGameElement.of(this.renderedItem).scale(5.0D).at(this.guiLeft + this.background.width + 10, this.guiTop + this.background.height / 2 - 8 * 5, -150.0F).render(matrixStack);
    }

    public boolean mouseClicked(double x, double y, int button) {
        if (this.confirmButton.isHovered()) {
            if (!player.getItemInHand(cardHand).isEmpty()) {
                this.confirmButton.onClick(x, y);
                ItemStack stack = player.getItemInHand(cardHand).copy();
                CompoundNBT nbt = new CompoundNBT();
                CardData.serialize(nbt, CardData.fromButtons(buttons));
                stack.setTag(nbt);
                player.setItemInHand(cardHand, stack);
                this.onClose();
            }
            return true;
        } else if (this.toggleButton.isHovered()) {
            for (Vector<ToggleButton> data : buttons)
                for (ToggleButton datum : data)
                    datum.pressed = toggle;
            this.toggleButton.onClick(x, y);
            toggle = !toggle;
            return true;
        }
        return super.mouseClicked(x, y, button);
    }
}
