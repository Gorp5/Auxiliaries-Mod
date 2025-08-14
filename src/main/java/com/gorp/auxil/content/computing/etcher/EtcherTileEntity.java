package com.gorp.auxil.content.computing.etcher;

import com.gorp.auxil.content.computing.CardData;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EtcherTileEntity extends KineticTileEntity implements IInventory {

    private CardItemHandler cardsIn = new CardItemHandler();
    private CardItemHandler cardsOut = new CardItemHandler();
    private LazyOptional<IItemHandler> capIn = LazyOptional.of(() -> cardsIn);
    private LazyOptional<IItemHandler> capOut = LazyOptional.of(() -> cardsOut);

    private int powerTop = 0, powerBottom = 0;
    private byte[] data = new byte[8];
    private byte tempByte = 0;
    private boolean isAnalog = false;
    private int timer = 0;
    private int dataIndex = -1;

    private boolean isTop = false, shouldCheckSignal = false, previousOverflowState = false;

    public EtcherTileEntity(TileEntityType<?> typeIn) {
        super(typeIn);
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
    }

    public void setTop(boolean isTop) {
        this.isTop = isTop;
        sendData();
    }

    public boolean isTop() {
        return this.isTop;
    }

    public void setAnalog(boolean analog) {
        isAnalog = analog;
        sendData();
    }

    public void notifySignal(int power) {
        powerBottom = power;
        sendData();
    }

    @Override
    public void tick() {
        super.tick();

        if(isTop) {
            if(cardsOut.getStackInSlot(0).isEmpty() == previousOverflowState) {
                boolean state = !cardsOut.getStackInSlot(0).isEmpty();
                level.setBlock(getBlockPos(), getBlockState().setValue(EtcherBlock.OVERFLOW, state), 51);
                previousOverflowState = state;
            }

            if (isIdle())
                return;

            if(dataIndex < 0)
                dataIndex = 0;

            if(shouldCheckSignal && powerBottom == 0)
                return;
            else
                shouldCheckSignal = false;

            if (level != null) {
                if (dataIndex >= 8) {
                    tryPrintCard(true);
                    shouldCheckSignal = true;
                    return;
                }
                ++timer;
                if (isAnalog && timer % 2 == 0) {
                    if (timer >= 8) {
                        data[dataIndex] = reverseFourBits(powerTop & 0xf);
                        dataIndex++;
                        timer = 0;
                        sendData();
                    }
                } else if (timer % 2 == 0) {
                    tempByte = (byte) (tempByte << 1);
                    tempByte += powerTop > 0 ? 1 : 0;
                    if (timer >= 8) {
                        data[dataIndex] = reverseFourBits(tempByte & 0xf);
                        dataIndex++;
                        timer = 0;
                        sendData();
                    }
                }
            }
        }
    }

    public byte reverseFourBits(int n)
    {
        int reversed = 0;

        n &= 0xf;
        while (n > 0)
        {
            reversed <<= 1;
            if ((n & 1) == 1)
                reversed ^= 1;
            n >>= 1;
        }
        return (byte)(reversed & 0xf);
    }

    final String TOP_KEY = "top_key";
    final String BOTTOM_KEY = "bottom_key";
    final String IN_KEY = "cardsIn_key";
    final String OUT_KEY = "cardsOut_key";
    final String INDEX_KEY = "index_key";
    final String DATA_KEY = "data_key";
    final String ANALOG_KEY = "is_analog_key";
    @Override
    protected void write(CompoundNBT compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        if (isTop) {
            compound.put(IN_KEY, cardsIn.serializeNBT());
            compound.put(OUT_KEY, cardsOut.serializeNBT());
            compound.putInt(TOP_KEY, powerTop);
            compound.putInt(BOTTOM_KEY, powerBottom);
            compound.putInt(INDEX_KEY, dataIndex);
            compound.putByteArray(DATA_KEY, data);
            compound.putBoolean(ANALOG_KEY, isAnalog);
        }
    }

    @Override
    protected void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
        super.fromTag(state, compound, clientPacket);
        if (isTop) {
            if (compound.contains(IN_KEY))
                cardsIn.deserializeNBT((CompoundNBT) compound.get(IN_KEY));
            if (compound.contains(OUT_KEY))
                cardsOut.deserializeNBT((CompoundNBT) compound.get(OUT_KEY));
            if (compound.contains(INDEX_KEY))
                dataIndex = compound.getInt(INDEX_KEY);
            if (compound.contains(BOTTOM_KEY))
                powerBottom = compound.getInt(BOTTOM_KEY);
            if (compound.contains(TOP_KEY))
                powerTop = compound.getInt(TOP_KEY);
            if (compound.contains(DATA_KEY))
                data = compound.getByteArray(DATA_KEY);
            if (compound.contains(ANALOG_KEY))
                isAnalog = compound.getBoolean(ANALOG_KEY);
        }
    }

    private void tryPrintCard(boolean shouldRecur) {
        if(cardsOut.getStackInSlot(0).isEmpty()) {
            ItemStack stack = cardsIn.getStackInSlot(0).split(1);
            CompoundNBT nbt = new CompoundNBT();
            CardData.serialize(nbt, new CardData(data));
            stack.setTag(nbt);
            cardsOut.insertItem(0, stack, false);
            data = new byte[8];
            dataIndex = -1;
            tempByte = 0;
            timer = 0;
            sendData();
        } else if(shouldRecur) {
            BlockPos pos = getBlockPos().relative(getBlockState().getValue(EtcherBlock.HORIZONTAL_FACING));
            ItemEntity entity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), cardsOut.extractItem(0, 1, false));
            level.addFreshEntity(entity);
            level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundCategory.BLOCKS, .2F, 1F, true);
            tryPrintCard(false);
        }
    }

    public boolean isIdle() {
        return cardsIn.getStackInSlot(0).isEmpty() || powerBottom == 0 || Math.abs(getBottomSpeed()) <= 0;
    }

    private float getBottomSpeed() {
        TileEntity ent = level.getBlockEntity(isTop ? getBlockPos().below() : getBlockPos());
        if(ent instanceof EtcherTileEntity)
            return ((EtcherTileEntity)ent).speed;
        return 0;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return cardsIn.getStackInSlot(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int i) {
        return cardsOut.getStackInSlot(i);
    }

    @Override
    public ItemStack removeItem(int i, int i1) {
        return cardsOut.extractItem(i, i1, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return cardsOut.extractItem(i, 1, false);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        cardsIn.setStackInSlot(i, itemStack);
    }

    @Override
    public boolean stillValid(PlayerEntity playerEntity) {
        return true;
    }

    @Override
    public void clearContent() {
        cardsIn.setStackInSlot(0, ItemStack.EMPTY);
        cardsOut.setStackInSlot(0, ItemStack.EMPTY);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (isTop) {
            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != null) {
                return side == Direction.UP ? capIn.cast() : side == getBlockState().getValue(EtcherBlock.HORIZONTAL_FACING) ? capOut.cast() : super.getCapability(cap, side);
            }
        }
        return super.getCapability(cap, side);
    }

    /*@Override
    public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
        ITextComponent text = new StringTextComponent(isAnalog ? "Analog" : "Pulse").withStyle(isAnalog ? TextFormatting.GOLD : TextFormatting.DARK_RED);
        ITextComponent running = new StringTextComponent(dataIndex < 0 ? "Idle" : "Currently Running").withStyle(dataIndex < 0 ? TextFormatting.DARK_RED : TextFormatting.DARK_GREEN);
        tooltip.add(new StringTextComponent(""));
        tooltip.add(text);
        tooltip.add(running);
        return true;
    }*/

    @Override
    public World getWorld() {
        return this.getLevel();
    }
}
