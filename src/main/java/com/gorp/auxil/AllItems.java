package com.gorp.auxil;

import com.gorp.auxil.content.computing.PunchCardItem;
import com.gorp.auxil.content.computing.puncher.PuncherItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.DataIngredient;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import static com.simibubi.create.content.AllSections.*;

public class AllItems {
    private static final CreateRegistrate REGISTRATE = Auxiliaries.registrate();
    public static ItemEntry<Item> CHORUS_ICE_CREAM, BLAZE_ICE_CREAM, SWEET_BERRY_ICE_CREAM, GUNPOWDER_ICE_CREAM, JIGGLE_O, FUDGE_POPSICLE, MEATLOAF;
    public static ItemEntry<Item> POLISHED_QUARTZ, PHOTOTUBE, RADIANT_TUBE, DISCHARGE_TUBE;
    public static ItemEntry<? extends Item> PUNCH_CARD, SMALL_PUNCH_CARD, PUNCHER;

    static {
        REGISTRATE.startSection(MATERIALS);

        //================================================================================
        // Foods
        //================================================================================

        CHORUS_ICE_CREAM = REGISTRATE
                .item("chorus_ice_cream", Item::new)
                .properties(p -> p.food(new Food.Builder()
                        .nutrition(5)
                        .saturationMod(1)
                        .alwaysEat()
                        .fast()
                        .effect(() -> new EffectInstance(Effects.LEVITATION, 80), .1f)
                        .build()))
                .lang("Chorus Fruit Ice Cream")
                .register();

        BLAZE_ICE_CREAM = REGISTRATE
                .item("blaze_ice_cream", Item::new)
                .properties(p -> p.food(new Food.Builder()
                        .nutrition(5)
                        .saturationMod(1)
                        .alwaysEat()
                        .fast()
                        .effect(() -> new EffectInstance(Effects.FIRE_RESISTANCE, 400), .6f)
                        .build()))
                .lang("Blaze Ice Cream")
                .register();

        SWEET_BERRY_ICE_CREAM = REGISTRATE
                .item("sweet_berry_ice_cream", Item::new)
                .properties(p -> p.food(new Food.Builder()
                        .nutrition(5)
                        .saturationMod(1)
                        .alwaysEat()
                        .fast()
                        .effect(() -> new EffectInstance(Effects.DAMAGE_BOOST, 400), .6f)
                        .build()))
                .lang("Sweet Berry Ice Cream")
                .register();

        GUNPOWDER_ICE_CREAM = REGISTRATE
                .item("gunpowder_ice_cream", Item::new)
                .properties(p -> p.food(new Food.Builder()
                        .nutrition(5)
                        .saturationMod(1)
                        .alwaysEat()
                        .fast()
                        .effect(() -> new EffectInstance(Effects.DIG_SPEED, 400), .6f)
                        .build()))
                .lang("Gunpowder Ice Cream")
                .register();

        JIGGLE_O = REGISTRATE
                .item("jiggle-o", Item::new)
                .properties(p -> p.food(new Food.Builder()
                        .nutrition(5)
                        .saturationMod(1)
                        .alwaysEat()
                        .fast()
                        .effect(() -> new EffectInstance(Effects.JUMP, 200), .4f)
                        .build()))
                .lang("Jiggle-O")
                .register();

        FUDGE_POPSICLE = REGISTRATE
                .item("fudge_popsicle", Item::new)
                .properties(p -> p.food(new Food.Builder()
                        .nutrition(4)
                        .saturationMod(1.2F)
                        .alwaysEat()
                        .fast()
                        .effect(() -> new EffectInstance(Effects.SATURATION, 100), .4f)
                        .build()))
                .lang("Fudge Popsicle")
                .register();

        MEATLOAF = REGISTRATE
                .item("meatloaf", Item::new)
                .properties(p -> p.food(new Food.Builder()
                        .nutrition(10)
                        .saturationMod(.8F)
                        .build()))
                .lang("Meatloaf")
                .register();

        //================================================================================
        // Materials
        //================================================================================

        POLISHED_QUARTZ = REGISTRATE
                .item("polished_quartz", Item::new)
                .lang("Polished Quartz")
                .register();

        PHOTOTUBE = REGISTRATE
                .item("phototube", Item::new)
                .lang("Photo-Tube")
                .register();
    
        RADIANT_TUBE = REGISTRATE
                .item("radiant_tube", Item::new)
                .lang("Radiant Tube")
                .register();
    
        DISCHARGE_TUBE = REGISTRATE
                .item("discharge_tube", Item::new)
                .lang("Discharge Tube")
                .register();

        //================================================================================
        // Curiosities
        //================================================================================
        REGISTRATE.startSection(LOGISTICS);

        PUNCHER = REGISTRATE
                .item("puncher", PuncherItem::new)
                .properties((p) -> p.stacksTo(1))
                .model(AssetLookup.itemModelWithPartials())
                .lang("Puncher")
                .register();

        PUNCH_CARD = REGISTRATE
                .item("punch_card", (p) -> new PunchCardItem(p, false))
                .lang("Punch Card")
                .properties((p) -> p.stacksTo(1))
                .register();

        SMALL_PUNCH_CARD = REGISTRATE
                .item("small_punch_card", (p) -> new PunchCardItem(p, true))
                .lang("Quarter Punch Card")
                .properties((p) -> p.stacksTo(1))
                .register();
    }

    public static void register() {}
}
