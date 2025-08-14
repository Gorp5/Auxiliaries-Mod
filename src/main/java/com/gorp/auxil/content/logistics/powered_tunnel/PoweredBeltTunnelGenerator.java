package com.gorp.auxil.content.logistics.powered_tunnel;

import com.simibubi.create.content.logistics.block.belts.tunnel.BeltTunnelBlock;
import com.simibubi.create.content.logistics.block.belts.tunnel.BeltTunnelItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.simibubi.create.repack.registrate.builders.BlockBuilder;
import com.simibubi.create.repack.registrate.providers.DataGenContext;
import com.simibubi.create.repack.registrate.providers.RegistrateBlockstateProvider;
import com.simibubi.create.repack.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

public class PoweredBeltTunnelGenerator extends SpecialBlockStateGen {
    public PoweredBeltTunnelGenerator() {
    }
    
    @Override
    protected int getXRotation(BlockState blockState) {
        return 0;
    }
    
    @Override
    protected int getYRotation(BlockState blockState) {
        return blockState.getValue(BeltTunnelBlock.HORIZONTAL_AXIS) == Direction.Axis.X ? 0 : 90;
    }
    
    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> dataGenContext, RegistrateBlockstateProvider prov, BlockState blockState) {
        String id = "block/powered_tunnel";
        BeltTunnelBlock.Shape shape = blockState.getValue(BeltTunnelBlock.SHAPE);
        if (shape == BeltTunnelBlock.Shape.CLOSED) {
            shape = BeltTunnelBlock.Shape.STRAIGHT;
        }
        boolean power = blockState.getValue(PoweredBeltTunnelBlock.POWERED);
        String powerName = power ? "_powered" : "_unpowered";
        String variantName = shape.getSerializedName() + powerName;
        return prov.models().getExistingFile(prov.modLoc("block/" + dataGenContext.getName() + "/" + variantName));
    }
}
