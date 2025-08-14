package com.gorp.auxil.content.computing;

import com.gorp.auxil.foundation.gui.ToggleButton;
import com.simibubi.create.foundation.item.ItemDescription;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class CardData {
    private byte[] data;

    public CardData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public List<IFormattableTextComponent> getLabel() {
        List<IFormattableTextComponent> list = new LinkedList<>();
        list.add(new StringTextComponent("Data:  ").withStyle(TextFormatting.GOLD));
        for(byte datum : data) {
            StringBuilder bar = new StringBuilder(" ");
            for(int x = 0; x < 4; x++)
                bar.append(((datum >> x) & 1) == 1 ? "" + ItemDescription.makeProgressBar(2, 0).charAt(2) : ItemDescription.makeProgressBar(1, 1).charAt(1));
            list.add(new StringTextComponent(bar.toString()).withStyle(TextFormatting.YELLOW));
        }
        return list;
    }

    public void forAllBytes(BooleanConsumer consumer) {
        for(byte datum : data)
            forEachBit(consumer, datum);
    }

    public void forEachBit(BooleanConsumer consumer, byte datum) {
        for(int x = 0; x < 4; x++)
            consumer.accept(((datum >> x) & 1) == 1);
    }

    public static boolean isBit(byte datum) {
        return (datum & 1) == 1;
    }

    public static final String PROGRAM_TAG = "program_data_string";
    public static void serialize(CompoundNBT nbt, CardData program) {
        nbt.putByteArray(PROGRAM_TAG, program.getData());
    }

    public static CardData deserialize(CompoundNBT nbt, boolean small) {
        if (nbt != null) {
            if (nbt.contains(PROGRAM_TAG))
                return new CardData(nbt.getByteArray(PROGRAM_TAG));
        }
        return new CardData(new byte[small ? 2 : 8]);
    }

    public static CardData fromButtons(Vector<Vector<ToggleButton>> vector) {
        byte[] bytes = new byte[vector.size()];
        for(int x = 0; x < vector.size(); x++) {
            byte temp = 0;
            for (int y = 3; y >= 0; y--) {
                temp = (byte)(temp << 1);
                temp += vector.get(x).get(y).pressed ? 1 : 0;
            }
            bytes[x] = temp;
        }
        return new CardData(bytes);
    }

    @FunctionalInterface
    public interface BooleanConsumer {
        void accept(boolean bool);
    }
}
