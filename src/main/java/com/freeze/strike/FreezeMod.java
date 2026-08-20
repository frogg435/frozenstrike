package com.freeze.strike;

import com.freeze.strike.enchant.FreezeStrikeEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(FreezeMod.MOD_ID)
public class FreezeMod {
    public static final String MOD_ID = "freezestrike";

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = 
        DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MOD_ID);

    public static final RegistryObject<Enchantment> FREEZE_STRIKE = 
        ENCHANTMENTS.register("freeze_strike", FreezeStrikeEnchantment::new);

    public FreezeMod(IEventBus modBus) {
        ENCHANTMENTS.register(modBus);
    }
}
