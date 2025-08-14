package com.gorp.auxil.content.computing.sequenced_reader;

import com.gorp.auxil.content.computing.CardData;
import com.gorp.auxil.content.computing.PunchCardItem;
import com.gorp.auxil.content.computing.etcher.CardItemHandler;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SequencedReaderTileEntity extends KineticTileEntity {

    private CardItemHandler cardsIn = new CardItemHandler();
    private CardItemHandler cardsOut = new CardItemHandler();
    private LazyOptional<IItemHandler> capIn = LazyOptional.of(() -> cardsIn);
    private LazyOptional<IItemHandler> capOut = LazyOptional.of(() -> cardsOut);

    private int output = 0;
    private byte[] data = new byte[8];
    private boolean isAnalog = false;
    private int timer = 0;
    private int dataIndex = -1;
    private boolean needSetData = true;
    private boolean prevPower = false, step = false;

    public SequencedReaderTileEntity(TileEntityType<?> typeIn) {
        super(typeIn);
    }

    public void setAnalog(boolean isAnalog) {
        this.isAnalog = isAnalog;
        notifyUpdate();
    }

    public void tryStep(int power) {
        if(power > 0 != prevPower) {
            prevPower = power > 0;
            if(power > 0 && !cardsIn.getStackInSlot(0).isEmpty() && Math.abs(getSpeed()) > 0) {
                step = true;
                sendData();
            }
        }
    }

    @Override
    public boolean isVirtual() {
        return super.isVirtual();
    }

    public void setPower(int x) {
        output = x;
        notifyUpdate();
        if(level != null) {
            BlockPos position = getBlockPos().relative(getBlockState().getValue(SequencedReaderBlock.HORIZONTAL_FACING));
            BlockPos positionOpposite = getBlockPos().relative(getBlockState().getValue(SequencedReaderBlock.HORIZONTAL_FACING).getOpposite());
            level.getBlockState(position).neighborChanged(level, position, getBlockState().getBlock(), getBlockPos(), false);
            level.getBlockState(positionOpposite).neighborChanged(level, positionOpposite, getBlockState().getBlock(), getBlockPos(), false);
        }
    }

    public int getPower() {
        return output;
    }

    @Override
    public void tick() {
        super.tick();

        if (cardsIn.getStackInSlot(0).isEmpty() || Math.abs(getSpeed()) <= 0) {
            if(!level.isClientSide() && getBlockState().getValue(SequencedReaderBlock.POWERING))
                level.setBlock(getBlockPos(), getBlockState().setValue(SequencedReaderBlock.POWERING, false), 51);
            return;
        }

        if(needSetData) {
            ItemStack card = cardsIn.getStackInSlot(0);
            data = CardData.deserialize(card.getOrCreateTag(), ((PunchCardItem)card.getItem()).small).getData().clone();
            needSetData = false;
            dataIndex = 0;
        }

        if(dataIndex >= 8) {
            tryPrintCard();
            return;
        }

        if(!level.isClientSide())
            if(level != null && dataIndex >= 0 && step) {
                if (isAnalog) {
                    byte power =  (byte)(data[dataIndex] & 0xf);
                    setPower(power);
                    dataIndex++;
                    timer = 0;
                } else {
                    byte power = data[dataIndex];
                    setPower((power & 1) == 1 ? 15 : 0);
                    data[dataIndex] = (byte)(data[dataIndex] >> 1 & 0xf);
                    if(timer >= 4) {
                        dataIndex++;
                        timer = 0;
                    }
                }
                step = false;
                ++timer;
                notifyUpdate();
            }
    }

    private void tryPrintCard() {
        if(!cardsIn.getStackInSlot(0).isEmpty()) {
            cardsOut.setStackInSlot(0, cardsIn.extractItem(0, 1, false));
            data = new byte[8];
            dataIndex = -1;
            needSetData = true;
            setPower(0);
            notifyUpdate();
        }
    }

    final String OUTPUT_KEY = "output_key";
    final String IN_KEY = "cardsIn_key";
    final String TIMER_KEY = "timer_key";
    final String STEP_KEY = "step_key";
    final String OUT_KEY = "cardsOut_key";
    final String INDEX_KEY = "index_key";
    final String DATA_KEY = "data_key";
    final String ANALOG_KEY = "is_analog_key";
    final String PREV_POWER_KEY = "prev_power_key";
    @Override
    protected void write(CompoundNBT compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put(IN_KEY, cardsIn.serializeNBT());
        compound.put(OUT_KEY, cardsOut.serializeNBT());
        compound.putInt(OUTPUT_KEY, output);
        compound.putInt(INDEX_KEY, dataIndex);
        compound.putInt(TIMER_KEY, timer);
        compound.putByteArray(DATA_KEY, data);
        compound.putBoolean(ANALOG_KEY, isAnalog);
        compound.putBoolean(STEP_KEY, step);
        compound.putBoolean(PREV_POWER_KEY, prevPower);
    }

    @Override
    protected void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
        super.fromTag(state, compound, clientPacket);
        if (compound.contains(IN_KEY))
            cardsIn.deserializeNBT((CompoundNBT) compound.get(IN_KEY));
        if (compound.contains(OUT_KEY))
            cardsOut.deserializeNBT((CompoundNBT) compound.get(OUT_KEY));
        if (compound.contains(INDEX_KEY))
            dataIndex = compound.getInt(INDEX_KEY);
        if (compound.contains(OUTPUT_KEY))
            output = compound.getInt(OUTPUT_KEY);
        if (compound.contains(TIMER_KEY))
            timer = compound.getInt(TIMER_KEY);
        if (compound.contains(DATA_KEY))
            data = compound.getByteArray(DATA_KEY);
        if (compound.contains(ANALOG_KEY))
            isAnalog = compound.getBoolean(ANALOG_KEY);
        if (compound.contains(STEP_KEY))
            step = compound.getBoolean(STEP_KEY);
        if (compound.contains(PREV_POWER_KEY))
            prevPower = compound.getBoolean(PREV_POWER_KEY);
    }



    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != null) {
            return side == Direction.UP ? capIn.cast() : side == Direction.DOWN ? capOut.cast() : super.getCapability(cap, side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public World getWorld() {
        return getLevel();
    }
}
