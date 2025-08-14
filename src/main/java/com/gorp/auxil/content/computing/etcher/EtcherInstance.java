package com.gorp.auxil.content.computing.etcher;

import com.jozufozu.flywheel.backend.instancing.Instancer;
import com.jozufozu.flywheel.backend.instancing.MaterialManager;
import com.jozufozu.flywheel.core.materials.IFlatLight;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileInstance;
import com.simibubi.create.content.contraptions.base.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;

public class EtcherInstance extends KineticTileInstance<EtcherTileEntity> {

    protected RotatingData rotatingModel = !tile.isTop() ? this.setup(this.getModel().createInstance()) : null;

    public EtcherInstance(MaterialManager<?> modelManager, EtcherTileEntity tile) {
        super(modelManager, tile);
    }

    public void update() {
        if(rotatingModel != null)
            this.updateRotation(this.rotatingModel);
    }

    public void updateLight() {
        if(rotatingModel != null)
            this.relight(this.pos, new IFlatLight[]{this.rotatingModel});
    }

    public void remove() {
        if(rotatingModel != null)
            this.rotatingModel.delete();
    }

    private Instancer<RotatingData> getModel() {
        return this.materialManager.getMaterial(AllMaterialSpecs.ROTATING).getModel(AllBlockPartials.SHAFTLESS_COGWHEEL, this.tile.getBlockState());
    }
}
