package com.gorp.auxil.content.computing.sequenced_reader;

import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.simibubi.create.repack.registrate.providers.DataGenContext;
import com.simibubi.create.repack.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class SequencedReaderGenerator extends SpecialBlockStateGen {

    public SequencedReaderGenerator() {
    }

    protected int getXRotation(BlockState state) {
        return 0;
    }

    protected int getYRotation(BlockState state) {
        return (int)state.getValue(SequencedReaderBlock.HORIZONTAL_FACING).toYRot() + 270;
    }

    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        String variant = "block_";
        boolean powered = state.getValue(SequencedReaderBlock.POWERING);
        boolean on = state.getValue(SequencedReaderBlock.ON);

        if(powered) {
            variant += "powered";
            if (on)
                variant += "_on";
            else
                variant += "_off";
        } else {
            variant += "unpowered";
            if(on)
                variant += "_on";
            else
                variant += "_off";
        }

        return prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/" + variant));
    }
}
