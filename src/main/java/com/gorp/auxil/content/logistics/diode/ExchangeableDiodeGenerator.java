package com.gorp.auxil.content.logistics.diode;

import com.gorp.auxil.content.logistics.Tube;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.simibubi.create.repack.registrate.providers.DataGenContext;
import com.simibubi.create.repack.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class ExchangeableDiodeGenerator extends SpecialBlockStateGen {

    public ExchangeableDiodeGenerator() {
    }

    protected int getXRotation(BlockState state) {
        return 0;
    }

    protected int getYRotation(BlockState state) {
        return 0;
    }

    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        String variant = "";
        Tube type = state.getValue(ExchangeableDiodeBlock.TUBE);
        boolean on = state.getValue(ExchangeableDiodeBlock.POWER) > 0;

        variant += type.getSerializedName();
        variant += "_tube_";
        variant += on ? "on" : "off";

        return prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/" + variant));
    }
}
