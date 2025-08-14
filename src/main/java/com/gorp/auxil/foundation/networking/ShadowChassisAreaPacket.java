package com.gorp.auxil.foundation.networking;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ShadowChassisAreaPacket extends SimplePacketBase {
    private BlockPos anchor, sizeIndicator;
    private Type type;
    
    public ShadowChassisAreaPacket(BlockPos pos1, BlockPos pos2, Type type) {
        this.anchor = pos1;
        this.sizeIndicator = pos2;
        this.type = type;
    }
    
    public ShadowChassisAreaPacket(PacketBuffer buffer) {
        this.anchor = buffer.readBlockPos();
        this.sizeIndicator = buffer.readBlockPos();
        this.type = buffer.readEnum(Type.class);
    }
    
    public void write(PacketBuffer buffer) {
        buffer.writeBlockPos(this.anchor);
        buffer.writeBlockPos(this.sizeIndicator);
        buffer.writeEnum(this.type);
    
    }
    
    public void handle(Supplier<NetworkEvent.Context> context) {
        if(type == Type.ADD)
            context.get().enqueueWork(() -> ShadowChassisAreaHandler.addProtectedBlocks(anchor, sizeIndicator));
        if(type == Type.REMOVE)
            context.get().enqueueWork(() -> ShadowChassisAreaHandler.removeProtectedBlocks(anchor, sizeIndicator));
        context.get().setPacketHandled(true);
    }
    
    public static enum Type {
        ADD,
        REMOVE;
    }
}
