package com.gorp.auxil.content.computing.reader;

import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.simibubi.create.repack.registrate.providers.DataGenContext;
import com.simibubi.create.repack.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class ReaderGenerator extends SpecialBlockStateGen {

    public ReaderGenerator() {
    }

    protected int getXRotation(BlockState state) {
        return 0;
    }

    protected int getYRotation(BlockState state) {
        return (int)state.getValue(ReaderBlock.HORIZONTAL_FACING).toYRot() + 270;
    }

    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        String variant = "block_";
        boolean powering = state.getValue(ReaderBlock.POWERING);

        if(powering)
            variant += "powered";
        else
            variant += "unpowered";

        return prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/" + variant));
    }
}
