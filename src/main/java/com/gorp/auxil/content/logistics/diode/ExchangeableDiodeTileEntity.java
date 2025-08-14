package com.gorp.auxil.content.logistics.diode;

import com.gorp.auxil.content.logistics.Tube;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;

import java.util.List;

public class ExchangeableDiodeTileEntity extends SmartTileEntity {

    private Tube tubeState = Tube.EMPTY;
    private int previousLight = 0, count = 0;
    private float outputPower = 0;
    private float inputPower = 0;

    public ExchangeableDiodeTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public Tube getTubeState() {
        return tubeState;
    }

    /**
     * Sets the tube state and automatically syncs data
     * @param tubeState
     */
    public void setTubeState(Tube tubeState) {
        if(this.tubeState != tubeState)
            clearData();
        this.tubeState = tubeState;
        sendData();
    }
    
    private void clearData() {
        previousLight = 0;
        outputPower = 0;
        tubeState = Tube.EMPTY;
        inputPower = 0;
        count = 0;
    }
    
    public float getInputPower() {
        return inputPower;
    }
    
    public int getComparatorOutput() {
        if (tubeState == Tube.ELECTRON) {
            return count;
        }
        return 0;
    }
    
    public String getDisplayedStrings() {
        return "" + count;
    }
    
    public float getOutputPower() {
        return outputPower;
    }
    
    /**
     * Sets output power and automatically syncs data
     * @param outputPower
     */
    public void setOutputPower(float outputPower) {
        this.outputPower = outputPower;
        sendData();
    }

    @Override
    public void tick() {
        super.tick();

        switch(tubeState) {
            default:
            case ELECTRON:
            case DISCHARGE:
            case EMPTY:
                return;
            case PHOTO:
                photoTick();
                break;
            case RADIANT:
                radiantTick();
                break;
        }
    }
    
    private void radiantTick() {
        if(level != null && Create.RANDOM.nextFloat() <= .1F) {
            BlockPos pos = getBlockPos();
            Vector3d motion = VecHelper.offsetRandomly(Vector3d.ZERO, level.random, 0.5F);
            level.addParticle(ParticleTypes.END_ROD, pos.getX() + motion.x + 0.5F, pos.getY() + motion.y + 0.5F, pos.getZ() + motion.z + 0.5F, 0, 0, 0);
        }
    }

    private void photoTick() {
        if(level != null) {
            float time = level.getDayTime();
            float phase = Math.abs(4 - level.getMoonPhase());
            float skyLight;
            if(level.isDay()) {
                skyLight = (float)Math.abs(15 * Math.sin((Math.PI * (time + 1000)) / 14000F));
            } else {
                skyLight = (float)Math.abs((10F * phase)/8F * Math.sin((Math.PI * (time - 2000)) / 11000F));
            }
            
            skyLight = skyLight / 15.0F * level.getBrightness(LightType.SKY, getBlockPos());
            
            float light = Math.min(Math.max(skyLight, level.getBrightness(LightType.BLOCK, getBlockPos())), 15);
            if(previousLight != (int)light) {
                previousLight = (int)light;
                setOutputPower((int)light);
                level.setBlock(getBlockPos(), getBlockState().setValue(ExchangeableDiodeBlock.POWER, (int)outputPower), 3);
                level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
            }
        }
    }

    private static final String LIGHT_KEY = "light_key";
    private static final String OUTPUT_KEY = "output_key";
    private static final String STATE_KEY = "state_key";
    private static final String COUNT_KEY = "count_key";
    private static final String INPUT_KEY = "input_key";
    @Override
    protected void write(CompoundNBT compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt(LIGHT_KEY, previousLight);
        compound.putInt(COUNT_KEY, count);
        compound.putInt(INPUT_KEY, (int)inputPower);
        compound.putFloat(OUTPUT_KEY, outputPower);
        compound.putString(STATE_KEY, tubeState.getSerializedName());
    }

    @Override
    protected void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
        super.fromTag(state, compound, clientPacket);
        if(compound.contains(LIGHT_KEY))
            previousLight = compound.getInt(LIGHT_KEY);
        if(compound.contains(OUTPUT_KEY))
            outputPower =  compound.getFloat(OUTPUT_KEY);
        if(compound.contains(COUNT_KEY))
            count =  compound.getInt(COUNT_KEY);
        if(compound.contains(INPUT_KEY))
            inputPower =  compound.getInt(INPUT_KEY);
        if(compound.contains(STATE_KEY))
            tubeState = Tube.fromString(compound.getString(STATE_KEY));
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> list) {

    }
}
