package com.gorp.auxil;

import com.gorp.auxil.content.curiosities.PotatoBlastEnchantment;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.RegistryEntry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class AllEnchantments {
    private static final CreateRegistrate REGISTRATE = Auxiliaries.registrate();
    public static final RegistryEntry<PotatoBlastEnchantment> POTATO_BLAST;
    
    public AllEnchantments() {
    }
    
    public static void register() {
    }
    
    static {
        POTATO_BLAST = REGISTRATE.object("potato_blast").enchantment(EnchantmentType.BOW, PotatoBlastEnchantment::new).addSlots(new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND}).lang("Potato Blast").rarity(Enchantment.Rarity.UNCOMMON).register();
    }
}
