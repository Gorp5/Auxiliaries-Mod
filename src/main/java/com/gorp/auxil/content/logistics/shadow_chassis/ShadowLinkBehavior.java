package com.gorp.auxil.content.logistics.shadow_chassis;

import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class ShadowLinkBehavior extends TileEntityBehaviour {

    public static BehaviourType<ShadowLinkBehavior> TYPE = new BehaviourType<>();
    ShadowChassisTileEntity tile;

    public ShadowLinkBehavior(ShadowChassisTileEntity te) {
        super(te);
        tile = te;
    }

    @Override
    public void write(CompoundNBT nbt, boolean clientPacket) {
        if (tile.linkedOffset1 != null) {
            nbt.putInt("xOff1", tile.linkedOffset1.getX());
            nbt.putInt("yOff1", tile.linkedOffset1.getY());
            nbt.putInt("zOff1", tile.linkedOffset1.getZ());
        }

        if (tile.linkedOffset2 != null) {
            nbt.putInt("xOff2", tile.linkedOffset2.getX());
            nbt.putInt("yOff2", tile.linkedOffset2.getY());
            nbt.putInt("zOff2", tile.linkedOffset2.getZ());
        }

        super.write(nbt, clientPacket);
    }

    @Override
    public void read(CompoundNBT compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        int x1 = compound.getInt("xOff1");
        int y1 = compound.getInt("yOff1");
        int z1 = compound.getInt("zOff1");

        int x2 = compound.getInt("xOff2");
        int y2 = compound.getInt("yOff2");
        int z2 = compound.getInt("zOff2");
        
        if (!(x1 == 0 && y1 == 0 && z1 == 0 && x2 == 0 && y2 == 0 && z2 == 0)) {
            tile.removeTarget();
            tile.setTarget(new BlockPos(x1, y1, z1), (new BlockPos(x2, y2, z2)));
        }
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
