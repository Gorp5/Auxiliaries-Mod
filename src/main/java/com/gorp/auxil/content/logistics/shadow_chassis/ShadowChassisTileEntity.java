package com.gorp.auxil.content.logistics.shadow_chassis;

import com.gorp.auxil.foundation.networking.Channel;
import com.gorp.auxil.foundation.networking.ShadowChassisAreaPacket;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.AbstractChassisBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.ChassisRangeDisplay;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.ChassisTileEntity;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.*;

public class ShadowChassisTileEntity extends ChassisTileEntity implements IHaveGoggleInformation {
    public BlockPos linkedOffset1, linkedOffset2;
    private ShadowLinkBehavior shadowLinkBehavior;
    private List<BlockPos> positions = new ArrayList<>();
    
    public ShadowChassisTileEntity(TileEntityType<? extends ShadowChassisTileEntity> type) {
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
    public void setRemoved() {
        if(linkedOffset1 != null && linkedOffset2 != null && level != null && level.isClientSide)
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Channel.channel.sendToServer(new ShadowChassisAreaPacket(linkedOffset1, linkedOffset2, ShadowChassisAreaPacket.Type.REMOVE)));
        super.setRemoved();
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    public void removeTarget() {
        if(linkedOffset1 != null && linkedOffset2 != null && level != null && level.isClientSide)
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Channel.channel.sendToServer(new ShadowChassisAreaPacket(linkedOffset1, linkedOffset2, ShadowChassisAreaPacket.Type.REMOVE)));
    }
    
    public boolean setTarget(BlockPos offsetPos1, BlockPos offsetPos2) {
        if(offsetPos1 != null && offsetPos2 != null) {
            boolean distance =
                    BlockPos.ZERO.closerThan(offsetPos1.subtract(getBlockPos()), 16) &&
                            BlockPos.ZERO.closerThan(offsetPos2.subtract(getBlockPos()), 16);
            if (distance) {
                if(level != null && level.isClientSide)
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Channel.channel.sendToServer(new ShadowChassisAreaPacket(offsetPos1, offsetPos2, ShadowChassisAreaPacket.Type.ADD)));
                linkedOffset1 = getBlockPos().subtract(offsetPos1);
                linkedOffset2 = getBlockPos().subtract(offsetPos2);
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
                    positions.add(getBlockPos().offset(box.minX - x, box.minY - y, box.minZ - z));
                }
            }
        }
    }
    
    @Override
    public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
        ChassisRangeDisplay.display(this);
        return false;
    }
    
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        shadowLinkBehavior = new ShadowLinkBehavior(this);
        behaviours.add(shadowLinkBehavior);
    }
    
    public void initialize() {
        super.initialize();
    }
    
    public List<BlockPos> getIncludedBlockPositions(Direction forcedMovement, boolean visualize) {
        if (!(this.getBlockState().getBlock() instanceof AbstractChassisBlock)) {
            return Collections.emptyList();
        } else {
            LinkedList list = new LinkedList<>();
            if(linkedOffset1 != null && linkedOffset2 != null)
                list.addAll(positions);
            return list;
        }
    }
}
