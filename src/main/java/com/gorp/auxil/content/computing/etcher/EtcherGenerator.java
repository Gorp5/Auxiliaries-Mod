package com.gorp.auxil.content.computing.etcher;

import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.simibubi.create.repack.registrate.providers.DataGenContext;
import com.simibubi.create.repack.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class EtcherGenerator extends SpecialBlockStateGen {

    public EtcherGenerator() {
    }

    protected int getXRotation(BlockState state) {
        return 0;
    }

    protected int getYRotation(BlockState state) {
        return (int)state.getValue(EtcherBlock.HORIZONTAL_FACING).toYRot() + 270;
    }

    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        String variant = "";
        boolean powered = state.getValue(EtcherBlock.POWERED);

        if (powered)
            variant += "powered";
        else
            variant += "unpowered";
        if (state.getValue(EtcherBlock.OVERFLOW))
            variant += "_overflow";

        return prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/" + variant));
    }
}
