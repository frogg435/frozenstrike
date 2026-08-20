package com.freeze.strike.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class FreezeStrikeEnchantment extends Enchantment {
    public FreezeStrikeEnchantment() {
        // 1.20.1 中 EnchantmentCategory 是枚举类，不能被实例化。
        // 传入原版最宽松的 BREAKABLE 分类，并在下面重写 canEnchant 彻底放开限制。
        super(Rarity.RARE, EnchantmentCategory.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    // 重写判定，使其在铁砧和附魔台中适用于任何物品
    @Override
    public boolean canEnchant(ItemStack stack) {
        return true; 
    }
}
