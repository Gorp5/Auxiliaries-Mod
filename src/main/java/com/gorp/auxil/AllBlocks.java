package com.gorp.auxil;

import com.gorp.auxil.content.computing.etcher.EtcherBlock;
import com.gorp.auxil.content.computing.etcher.EtcherGenerator;
import com.gorp.auxil.content.computing.reader.ReaderBlock;
import com.gorp.auxil.content.computing.reader.ReaderGenerator;
import com.gorp.auxil.content.computing.sequenced_reader.SequencedReaderBlock;
import com.gorp.auxil.content.computing.sequenced_reader.SequencedReaderGenerator;
import com.gorp.auxil.content.logistics.powered_tunnel.PoweredBeltTunnelBlock;
import com.gorp.auxil.content.logistics.powered_tunnel.PoweredBeltTunnelGenerator;
import com.gorp.auxil.content.logistics.radiant_chassis.RadiantChassisBlock;
import com.gorp.auxil.content.logistics.radiant_chassis.RadiantChassisItem;
import com.gorp.auxil.content.logistics.diode.ExchangeableDiodeBlock;
import com.gorp.auxil.content.logistics.diode.ExchangeableDiodeGenerator;
import com.gorp.auxil.content.logistics.shadow_chassis.ShadowChassisBlock;
import com.gorp.auxil.content.logistics.shadow_chassis.ShadowChassisItem;
import com.gorp.auxil.content.logistics.turbine.TurbineBlock;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.block.belts.tunnel.BeltTunnelItem;
import com.simibubi.create.content.logistics.block.belts.tunnel.BrassTunnelCTBehaviour;
import com.simibubi.create.foundation.config.StressConfigDefaults;
import com.simibubi.create.foundation.data.*;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;

public class AllBlocks {
    private static final CreateRegistrate REGISTRATE = Auxiliaries.registrate();
    public static BlockEntry<? extends Block> ETCHER, READER, SEQUENCED_READER, DIODE_BLOCK, RADIANT_CHASSIS, SHADOW_CHASSIS, TURBINE, POWERED_TUNNEL;

    static {
        //================================================================================
        // Blocks
        //================================================================================

        ETCHER =
                REGISTRATE
                        .block("etcher", EtcherBlock::new)
                        .initialProperties(SharedProperties::stone)
                        .properties((p) -> p
                                .sound(SoundType.METAL)
                                .isRedstoneConductor((state, world, position) -> false))
                        .defaultLoot()
                        .addLayer(() -> RenderType::cutoutMipped)
                        .blockstate(new EtcherGenerator()::generate)
                        .transform(StressConfigDefaults.setImpact(1.0))
                        .item()
                        .transform(ModelGen.customItemModel())
                        .lang("Card Etcher")
                        .register();

        READER =
                REGISTRATE
                        .block("reader", ReaderBlock::new)
                        .initialProperties(SharedProperties::stone)
                        .properties((p) -> p
                                .sound(SoundType.METAL)
                                .isRedstoneConductor((state, world, position) -> false))
                        .addLayer(() -> RenderType::cutoutMipped)
                        .defaultLoot()
                        .blockstate(new ReaderGenerator()::generate)
                        .transform(StressConfigDefaults.setImpact(1.0))
                        .item()
                        .transform(ModelGen.customItemModel())
                        .lang("Card Reader")
                        .register();

        SEQUENCED_READER =
                REGISTRATE
                        .block("sequenced_reader", SequencedReaderBlock::new)
                        .initialProperties(SharedProperties::stone)
                        .properties((p) -> p
                                .sound(SoundType.METAL)
                                .isRedstoneConductor((state, world, position) -> false))
                        .addLayer(() -> RenderType::cutoutMipped)
                        .defaultLoot()
                        .blockstate(new SequencedReaderGenerator()::generate)
                        .transform(StressConfigDefaults.setImpact(1.0))
                        .item()
                        .transform(ModelGen.customItemModel())
                        .lang("Sequenced Reader")
                        .register();

        DIODE_BLOCK =
                REGISTRATE
                        .block("exchangeable_diode", ExchangeableDiodeBlock::new)
                        .initialProperties(SharedProperties::softMetal)
                        .addLayer(() -> RenderType::translucent)
                        .defaultLoot()
                        .blockstate(new ExchangeableDiodeGenerator()::generate)
                        .item()
                        .transform(ModelGen.customItemModel())
                        .lang("Diode Socket")
                        .register();
        
        RADIANT_CHASSIS =
                REGISTRATE
                        .block("radiant_chassis", RadiantChassisBlock::new)
                        .initialProperties(SharedProperties::softMetal)
                        .properties(p -> p.lightLevel((state) -> 15))
                        .defaultLoot()
                        .blockstate((context, properties) -> BlockStateGen.simpleBlock(context, properties, AssetLookup.forPowered(context, properties)))
                        .item(RadiantChassisItem::new)
                        .transform(ModelGen.customItemModel(new String[]{"_", "block"}))
                        //.transform(casing(TextureSpriteShifter.RADIANT_CHASSIS))
                        .register();
        
        SHADOW_CHASSIS =
                REGISTRATE
                        .block("shadow_chassis", ShadowChassisBlock::new)
                        .initialProperties(SharedProperties::softMetal)
                        .defaultLoot()
                        .blockstate((context, properties) -> BlockStateGen.simpleBlock(context, properties, AssetLookup.forPowered(context, properties)))
                        .item(ShadowChassisItem::new)
                        .transform(ModelGen.customItemModel(new String[]{"_", "block"}))
                        .register();

        TURBINE =
                REGISTRATE
                        .block("turbine", TurbineBlock::new)
                        .initialProperties(SharedProperties::softMetal)
                        .properties(AbstractBlock.Properties::noOcclusion)
                        .defaultLoot()
                        .addLayer(() -> RenderType::cutoutMipped)
                        .blockstate(BlockStateGen.directionalBlockProvider(true))
                        .item()
                        .transform(ModelGen.customItemModel())
                        .lang("Fluid Turbine")
                        .register();
        
        /*ANALOG_GEARSHIFT =
                REGISTRATE
                        .block("analog_gearshift", TurbineBlock::new)
                        .initialProperties(SharedProperties::softMetal)
                        .properties(AbstractBlock.Properties::noOcclusion)
                        .defaultLoot()
                        .addLayer(() -> RenderType::cutoutMipped)
                        .blockstate(BlockStateGen.directionalBlockProvider(true))
                        .item()
                        .transform(ModelGen.customItemModel())
                        .lang("Fluid Turbine")
                        .register();*/
  
        POWERED_TUNNEL =
                REGISTRATE
                        .block("powered_tunnel", PoweredBeltTunnelBlock::new)
                        .initialProperties(SharedProperties::stone)
                        .properties(AbstractBlock.Properties::noOcclusion)
                        .blockstate((context, properties) -> new PoweredBeltTunnelGenerator().generate(context, properties))
                        .defaultLoot()
                        .addLayer(() -> RenderType::cutoutMipped)
                        .onRegister(CreateRegistrate.connectedTextures(new BrassTunnelCTBehaviour()))
                        .item(BeltTunnelItem::new)
                        .transform(ModelGen.customItemModel())
                        .lang("Powered Tunnel")
                        .register();
    }
    
    /*public static <B extends Block> NonNullUnaryOperator<BlockBuilder<B, CreateRegistrate>> casing(CTSpriteShiftEntry ct) {
        return (b) -> (b.onRegister(CreateRegistrate.connectedTextures(new StandardCTBehaviour(ct)))).onRegister(CreateRegistrate.casingConnectivity((block, cc) -> {cc.make(block, ct);}));
    }*/

    public static void register() {}
}
