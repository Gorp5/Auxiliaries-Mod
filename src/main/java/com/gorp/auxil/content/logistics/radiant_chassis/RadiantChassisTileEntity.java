package com.gorp.auxil.content.logistics.radiant_chassis;

import com.gorp.auxil.foundation.networking.Channel;
import com.gorp.auxil.foundation.networking.ShadowChassisAreaPacket;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.AbstractChassisBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.ChassisRangeDisplay;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.ChassisTileEntity;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.*;

public class RadiantChassisTileEntity extends ChassisTileEntity implements IHaveGoggleInformation {
    public BlockPos linkedOffset1, linkedOffset2;
    private RadiantLinkBehavior radiantLinkBehavior;
    private List<BlockPos> positions = new ArrayList<>();
    
    public RadiantChassisTileEntity(TileEntityType<? extends RadiantChassisTileEntity> type) {
        super(type);
    }
    
    @Override
    public boolean addAttachedChasses(Queue<BlockPos> frontier, Set<BlockPos> visited) {
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof AbstractChassisBlock)) {
            return false;
        } else {
            Direction.Axis axis = state.getValue(AbstractChassisBlock.AXIS);
            int var6;
            int var7;
            int[] var12 = new int[]{-1, 1};
            var6 = var12.length;
            
            for(var7 = 0; var7 < var6; ++var7) {
                int offset = var12[var7];
                Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
                BlockPos currentPos = this.getBlockPos().relative(direction, offset);
                if (!this.level.isEmptyBlock(currentPos)) {
                    return false;
                }
                
                BlockState neighbourState = this.level.getBlockState(currentPos);
                if (AllBlocks.RADIAL_CHASSIS.has(neighbourState) && !visited.contains(currentPos)) {
                    frontier.add(currentPos);
                }
            }
            return true;
        }
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    public boolean setTarget(BlockPos offsetPos1, BlockPos offsetPos2) {
        if(offsetPos1 != null && offsetPos2 != null) {
            boolean distance =
                    BlockPos.ZERO.closerThan(offsetPos1, 16) &&
                            BlockPos.ZERO.closerThan(offsetPos2, 16);
            if (distance) {
                linkedOffset1 = offsetPos1;
                linkedOffset2 = offsetPos2;
                setPositions();
            }
            sendData();
            return distance;
        }
        sendData();
        return false;
    }
    
    private void setPositions() {
        positions.clear();
        AxisAlignedBB box = new AxisAlignedBB(linkedOffset1, linkedOffset2);
        BlockPos sizeIndicator = new BlockPos(box.getXsize() + 1, box.getYsize() + 1, box.getZsize() + 1);
        for(int x = 0; x < sizeIndicator.getX(); x++) {
            for(int y = 0; y < sizeIndicator.getY(); y++) {
                for(int z = 0; z < sizeIndicator.getZ(); z++) {
                    positions.add(getBlockPos().offset(box.minX + x, box.minY + y, box.minZ + z));
                }
            }
        }
    }
    
    @Override
    public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
        ChassisRangeDisplay.display(this);
        return false;
    }
    
    protected void fromTag(BlockState blockState, CompoundNBT compound, boolean clientPacket) {
        super.fromTag(blockState, compound, clientPacket);
    }
    
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        radiantLinkBehavior = new RadiantLinkBehavior(this);
        behaviours.add(radiantLinkBehavior);
    }
    
    public void initialize() {
        super.initialize();
    }
    
    public List<BlockPos> getIncludedBlockPositions(Direction forcedMovement, boolean visualize) {
        if (!(this.getBlockState().getBlock() instanceof AbstractChassisBlock)) {
            return Collections.emptyList();
        } else {
            LinkedList list = new LinkedList<>();
            if(linkedOffset1 != null && linkedOffset2 != null && !getBlockState().getValue(RadiantChassisBlock.POWERED))
                list.addAll(positions);
            return list;
        }
    }
}
