package com.freeze.strike.events;

import com.freeze.strike.FreezeMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = FreezeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FreezeStrikeHandler {

    private static final String TIMER_KEY = "freeze_strike_timer";
    private static final String DAMAGE_KEY = "freeze_strike_damage";
    private static final String ATTACKER_KEY = "freeze_strike_attacker";
    private static final String SETTLING_KEY = "freeze_strike_settling";

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        CompoundTag data = target.getPersistentData();

        if (data.getBoolean(SETTLING_KEY)) {
            return;
        }

        DamageSource source = event.getSource();
        Entity attackerEntity = source.getEntity();

        if (data.contains(TIMER_KEY)) {
            if (attackerEntity instanceof LivingEntity attackerLiving) {
                ItemStack weapon = attackerLiving.getMainHandItem();
                int enchLevel = EnchantmentHelper.getItemEnchantmentLevel(FreezeMod.FREEZE_STRIKE.get(), weapon);

                if (enchLevel > 0) {
                    float currentDamage = data.getFloat(DAMAGE_KEY);
                    data.putFloat(DAMAGE_KEY, currentDamage + event.getAmount());
                    data.putInt(TIMER_KEY, 30);
                    data.putUUID(ATTACKER_KEY, attackerLiving.getUUID());

                    event.setCanceled(true);
                    damageWeapon(attackerLiving, weapon);
                    return;
                }
            }
            event.setCanceled(true);
            return;
        }

        if (attackerEntity instanceof LivingEntity attackerLiving) {
            ItemStack weapon = attackerLiving.getMainHandItem();
            int enchLevel = EnchantmentHelper.getItemEnchantmentLevel(FreezeMod.FREEZE_STRIKE.get(), weapon);

            if (enchLevel > 0) {
                data.putInt(TIMER_KEY, 30);
                data.putFloat(DAMAGE_KEY, event.getAmount());
                data.putUUID(ATTACKER_KEY, attackerLiving.getUUID());

                if (target instanceof Mob mob) {
                    mob.setNoAi(true);
                } else if (target instanceof Player targetPlayer) {
                    targetPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 255, false, false));
                    targetPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 255, false, false));
                    targetPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 255, false, false));
                }

                event.setCanceled(true);
                damageWeapon(attackerLiving, weapon);
            }
        }
    }

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        LivingEntity target = event.getEntity();
        CompoundTag data = target.getPersistentData();

        if (data.contains(TIMER_KEY)) {
            int timer = data.getInt(TIMER_KEY);
            timer--;
            data.putInt(TIMER_KEY, timer);

            if (target instanceof Player player && timer > 0) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 255, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 255, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 255, false, false));
            }

            if (timer <= 0) {
                float damage = data.getFloat(DAMAGE_KEY);
                UUID attackerUUID = data.getUUID(ATTACKER_KEY);

                data.remove(TIMER_KEY);
                data.remove(DAMAGE_KEY);
                data.remove(ATTACKER_KEY);

                if (target instanceof Mob mob) {
                    mob.setNoAi(false);
                } else if (target instanceof Player player) {
                    player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                    player.removeEffect(MobEffects.WEAKNESS);
                    player.removeEffect(MobEffects.DIG_SLOWDOWN);
                }

                if (target.level() instanceof ServerLevel serverLevel) {
                    Entity attackerEntity = serverLevel.getEntity(attackerUUID);

                    if (attackerEntity instanceof LivingEntity attacker) {
                        DamageSource source;
                        if (attacker instanceof Player p) {
                            source = target.level().damageSources().playerAttack(p);
                        } else {
                            source = target.level().damageSources().mobAttack(attacker);
                        }

                        data.putBoolean(SETTLING_KEY, true);
                        target.invulnerableTime = 0;
                        target.hurt(source, damage);
                        data.remove(SETTLING_KEY);
                    } else {
                        data.putBoolean(SETTLING_KEY, true);
                        target.invulnerableTime = 0;
                        target.hurt(target.level().damageSources().generic(), damage);
                        data.remove(SETTLING_KEY);
                    }
                }
            }
        }
    }

    private static void damageWeapon(LivingEntity attacker, ItemStack weapon) {
        if (!weapon.isEmpty() && weapon.isDamageableItem()) {
            if (attacker instanceof Player player && player.getAbilities().instabuild) {
                return; 
            }
            weapon.hurtAndBreak(1, attacker, (e) -> {
                if (e instanceof Player p) {
                    p.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                }
            });
        }
    }
}
