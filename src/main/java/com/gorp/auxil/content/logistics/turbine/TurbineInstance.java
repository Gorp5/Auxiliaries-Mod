package com.gorp.auxil.content.logistics.turbine;

import com.jozufozu.flywheel.backend.instancing.InstanceMaterial;
import com.jozufozu.flywheel.backend.instancing.MaterialManager;
import com.jozufozu.flywheel.core.materials.IFlatLight;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.base.KineticTileInstance;
import com.simibubi.create.content.contraptions.base.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.MatrixStacker;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.function.Supplier;

public class TurbineInstance extends KineticTileInstance<TurbineTileEntity> {
    protected final RotatingData cog;
    final Direction direction;
    TurbineTileEntity tile;
    
    public TurbineInstance(MaterialManager<?> modelManager, TurbineTileEntity tile) {
        super(modelManager, tile);
        this.tile = tile;
        this.direction = (Direction)this.blockState.getValue(BlockStateProperties.FACING);
        Supplier<MatrixStack> ms = () -> {
            MatrixStack stack = new MatrixStack();
            MatrixStacker.of(stack).centre().rotateY((double) AngleHelper.horizontalAngle(direction)).rotateX(direction.getAxis().isVertical() ? 0 : 90).unCentre();
            return stack;
        };
        this.cog = this.materialManager.getMaterial(AllMaterialSpecs.ROTATING).getModel(AllBlockPartials.SHAFTLESS_COGWHEEL, this.tile.getBlockState(), direction, ms).createInstance();
        this.setup(this.cog, this.direction.getAxis());
    }
    
    public void update() {
        this.updateRotation(this.cog, this.direction.getAxis(), this.getTileSpeed());
    }
    
    public void updateLight() {
        BlockPos behind = this.pos.relative(this.direction.getOpposite());
        this.relight(this.pos.relative(Direction.UP), this.cog);
    }
    
    public void remove() {
        this.cog.delete();
    }
}
