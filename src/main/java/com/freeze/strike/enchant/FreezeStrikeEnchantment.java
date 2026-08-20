package com.freeze.strike.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class FreezeStrikeEnchantment extends Enchantment {
    public FreezeStrikeEnchantment() {
        // 1.20.1 中 EnchantmentCategory 不是函数式接口，必须使用匿名内部类
        super(Rarity.RARE, new EnchantmentCategory() {
            @Override
            public boolean canEnchant(ItemStack stack) {
                return true; // 适用于所有物品
            }
        }, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return true; 
    }
}
