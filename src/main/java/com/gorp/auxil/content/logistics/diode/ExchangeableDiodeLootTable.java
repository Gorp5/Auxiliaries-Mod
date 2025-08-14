package com.gorp.auxil.content.logistics.diode;

public class ExchangeableDiodeLootTable {
    /*public static <T extends Block> void generateLoot(RegistrateBlockLootTables lt, T block) {
        lt.dropSelf(block);
        LootTable.Builder builder = LootTable.lootTable();
        ILootCondition.IBuilder none = BlockStateProperty.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ExchangeableDiodeBlock.TUBE, Tube.EMPTY));
        ILootCondition.IBuilder electron = BlockStateProperty.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ExchangeableDiodeBlock.TUBE, Tube.ELECTRON));
        ILootCondition.IBuilder photo = BlockStateProperty.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ExchangeableDiodeBlock.TUBE, Tube.PHOTO));
        ILootCondition.IBuilder discharge = BlockStateProperty.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ExchangeableDiodeBlock.TUBE, Tube.DISCHARGE));
        ILootCondition.IBuilder radiant = BlockStateProperty.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ExchangeableDiodeBlock.TUBE, Tube.RADIANT));
        lt.add(block, builder.withPool(LootPool.lootPool()
                .when(none).setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(AllBlocks.DIODE_BLOCK.get().asItem()))
                .when(electron).setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(AllItems.ELECTRON_DIODE.get()))
                .when(photo).setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(AllItems.PHOTO_DIODE.get()))
                .when(discharge).setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(AllItems.DISCHARGE_DIODE.get()))
                .when(radiant).setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(AllItems.RADIANT_DIODE.get()))
        ));
    }*/
}
