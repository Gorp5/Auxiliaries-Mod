package com.gorp.auxil.content.logistics.turbine;

import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.content.contraptions.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import java.util.List;

public class TurbineTileEntity extends GeneratingKineticTileEntity {
    private float generationSpeed = 0;
    public TurbineTileEntity(TileEntityType<?> typeIn) {
        super(typeIn);
    }
    
    @Override
    public float getGeneratedSpeed() {
        return generationSpeed;
    }
    
    public float calculateAddedStressCapacity() {
        return 4;
    }
    
    public float calculateStressApplied() {
        return 0;
    }
    
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(new TurbinePipeFluidTransportBehaviour(this));
    }
    
    public void addGenerationSpeed(float speed) {
        generationSpeed += speed;
        sendData();
        updateGeneratedRotation();
    }
    
    public void resetSpeed() {
        generationSpeed = 0;
        sendData();
        updateGeneratedRotation();
    }
    
    public boolean isSideAccessible(Direction side) {
        BlockState blockState = this.getBlockState();
        if (!(blockState.getBlock() instanceof TurbineBlock)) {
            return false;
        } else {
            return blockState.getValue(TurbineBlock.FACING).getAxis() == side.getAxis();
        }
    }
    
    final String SPEED_KEY = "speed_key";
    protected void write(CompoundNBT compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat(SPEED_KEY, generationSpeed);
    }
    
    @Override
    protected void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
        super.fromTag(state, compound, clientPacket);
        if(compound.contains(SPEED_KEY)) {
            generationSpeed = compound.getFloat(SPEED_KEY);
        }
    }
    
    @Override
    public World getWorld() {
        return getLevel();
    }
    
    class TurbinePipeFluidTransportBehaviour extends FluidTransportBehaviour {
        
        public TurbinePipeFluidTransportBehaviour(SmartTileEntity te) {
            super(te);
        }
        
        @Override
        public void addPressure(Direction side, boolean inbound, float pressure) {
            super.addPressure(side, inbound, pressure);
            if(level != null)
                level.getBlockTicks().scheduleTick(getBlockPos(), getBlockState().getBlock(), 5, TickPriority.HIGH);
        }
        
        @Override
        public void wipePressure() {
            super.wipePressure();
            if(level != null)
                level.getBlockTicks().scheduleTick(getBlockPos(), getBlockState().getBlock(), 5, TickPriority.HIGH);
        }
        
        public boolean canHaveFlowToward(BlockState state, Direction direction) {
            return TurbineTileEntity.this.isSideAccessible(direction);
        }
    }
}

