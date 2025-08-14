package com.gorp.auxil;

import com.gorp.auxil.content.computing.etcher.EtcherInstance;
import com.gorp.auxil.content.computing.etcher.EtcherRenderer;
import com.gorp.auxil.content.computing.etcher.EtcherTileEntity;
import com.gorp.auxil.content.computing.reader.ReaderTileEntity;
import com.gorp.auxil.content.computing.sequenced_reader.SequencedReaderTileEntity;
import com.gorp.auxil.content.logistics.powered_tunnel.PoweredBeltTunnelTileEntity;
import com.gorp.auxil.content.logistics.radiant_chassis.RadiantChassisTileEntity;
import com.gorp.auxil.content.logistics.diode.ExchangeableDiodeRenderer;
import com.gorp.auxil.content.logistics.diode.ExchangeableDiodeTileEntity;
import com.gorp.auxil.content.logistics.shadow_chassis.ShadowChassisTileEntity;
import com.gorp.auxil.content.logistics.turbine.TurbineInstance;
import com.gorp.auxil.content.logistics.turbine.TurbineRenderer;
import com.gorp.auxil.content.logistics.turbine.TurbineTileEntity;
import com.simibubi.create.content.contraptions.relays.encased.EncasedShaftRenderer;
import com.simibubi.create.content.contraptions.relays.encased.ShaftInstance;
import com.simibubi.create.content.logistics.block.belts.tunnel.BeltTunnelInstance;
import com.simibubi.create.content.logistics.block.belts.tunnel.BeltTunnelRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;
import net.minecraft.tileentity.TileEntity;

public class AllTileEntities {
    private static final CreateRegistrate REGISTRATE = Auxiliaries.registrate();
    public static TileEntityEntry<? extends TileEntity> ETCHER, READER, SEQUENCED_READER, DIODE_SOCKET, RADIANT_CHASSIS, SHADOW_CHASSIS, TURBINE, POWERED_TUNNEL;

    static {
        //================================================================================
        // Blocks
        //================================================================================

        ETCHER =
                REGISTRATE
                        .tileEntity("etcher", EtcherTileEntity::new)
                        .instance(() -> EtcherInstance::new)
                        .validBlocks(AllBlocks.ETCHER)
                        .renderer(() -> EtcherRenderer::new)
                        .register();

        READER =
                REGISTRATE
                        .tileEntity("reader", ReaderTileEntity::new)
                        .instance(() -> ShaftInstance::new)
                        .validBlocks(AllBlocks.READER)
                        .renderer(() -> EncasedShaftRenderer::new)
                        .register();
        
        SEQUENCED_READER =
                REGISTRATE
                        .tileEntity("sequenced_reader", SequencedReaderTileEntity::new)
                        .instance(() -> ShaftInstance::new)
                        .validBlocks(AllBlocks.SEQUENCED_READER)
                        .renderer(() -> EncasedShaftRenderer::new)
                        .register();
    
        DIODE_SOCKET =
                REGISTRATE
                        .tileEntity("exchangeable_diode", ExchangeableDiodeTileEntity::new)
                        .validBlocks(AllBlocks.DIODE_BLOCK)
                        .renderer(() -> ExchangeableDiodeRenderer::new)
                        .register();
    
        RADIANT_CHASSIS =
                REGISTRATE
                        .tileEntity("radiant_chassis", RadiantChassisTileEntity::new)
                        .validBlocks(AllBlocks.RADIANT_CHASSIS)
                        .register();
    
        SHADOW_CHASSIS =
                REGISTRATE
                        .tileEntity("shadow_chassis", ShadowChassisTileEntity::new)
                        .validBlocks(AllBlocks.SHADOW_CHASSIS)
                        .register();
    
        TURBINE =
                REGISTRATE
                        .tileEntity("turbine", TurbineTileEntity::new)
                        .instance(() -> TurbineInstance::new)
                        .validBlocks(AllBlocks.TURBINE)
                        .renderer(() -> TurbineRenderer::new)
                        .register();
    
        POWERED_TUNNEL =
                REGISTRATE
                        .tileEntity("powered_tunnel", PoweredBeltTunnelTileEntity::new)
                        .instance(() -> BeltTunnelInstance::new)
                        .validBlocks(AllBlocks.POWERED_TUNNEL)
                        .renderer(() -> BeltTunnelRenderer::new)
                        .register();
    }

    public static void register() {}
}
