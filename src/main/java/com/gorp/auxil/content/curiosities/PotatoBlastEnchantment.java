package com.gorp.auxil.content.curiosities;

import com.simibubi.create.content.curiosities.weapons.PotatoCannonItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class PotatoBlastEnchantment extends Enchantment {
    public PotatoBlastEnchantment(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {
        super(rarityIn, typeIn, slots);
    }
    
    public int getMaxLevel() {
        return 3;
    }
    
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof PotatoCannonItem;
    }
    
}

