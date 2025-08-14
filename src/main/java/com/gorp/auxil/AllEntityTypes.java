package com.gorp.auxil;

import com.gorp.auxil.content.curiosities.BlastingPotatoProjectileEntity;
import com.simibubi.create.content.curiosities.weapons.PotatoProjectileRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.EntityEntry;
import net.minecraft.entity.EntityClassification;

public class AllEntityTypes {
    public static final EntityEntry<BlastingPotatoProjectileEntity> BLASTING_PROJECTILE;
    public static final CreateRegistrate REGISTRATE = Auxiliaries.registrate();
    
    static {
        BLASTING_PROJECTILE = REGISTRATE.entity("blasting_projectile", BlastingPotatoProjectileEntity::new, EntityClassification.MISC).properties((b) -> {
            b.setTrackingRange(4).setUpdateInterval(20).setShouldReceiveVelocityUpdates(true);
        }).properties(BlastingPotatoProjectileEntity::build).renderer(() -> PotatoProjectileRenderer::new).register();
    }
    
    public AllEntityTypes() {}
    
    public static void register() {}
}
