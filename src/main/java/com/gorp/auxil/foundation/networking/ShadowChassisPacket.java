package com.gorp.auxil.foundation.networking;

import com.gorp.auxil.content.logistics.shadow_chassis.ShadowChassisTileEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ShadowChassisPacket extends SimplePacketBase {
    private BlockPos targetPos1, targetPos2, pos;
    
    public ShadowChassisPacket(BlockPos pos, BlockPos targetPos1, BlockPos targetPos2) {
        this.pos = pos;
        this.targetPos1 = targetPos1;
        this.targetPos2 = targetPos2;
    }
    
    public ShadowChassisPacket(PacketBuffer buffer) {
        this.pos = buffer.readBlockPos();
        this.targetPos1 = buffer.readBlockPos();
        this.targetPos2 = buffer.readBlockPos();
    }
    
    public void write(PacketBuffer buffer) {
        buffer.writeBlockPos(this.pos);
        if(this.targetPos1 != null)
            buffer.writeBlockPos(this.targetPos1);
        else {
            buffer.writeBlockPos(this.pos);
        }
        
        if(this.targetPos2 != null)
            buffer.writeBlockPos(this.targetPos2);
        else {
            buffer.writeBlockPos(this.pos);
        }
    }
    
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null) {
                World world = player.level;
                if (world != null && !world.isEmptyBlock(this.pos)) {
                    TileEntity tileEntity = world.getBlockEntity(this.pos);
                    BlockState state = world.getBlockState(this.pos);
                    if (tileEntity instanceof ShadowChassisTileEntity) {
                        ((ShadowChassisTileEntity)tileEntity).removeTarget();
                        boolean worked = ((ShadowChassisTileEntity)tileEntity).setTarget(tileEntity.getBlockPos().subtract(targetPos1), tileEntity.getBlockPos().subtract(targetPos2));
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}

