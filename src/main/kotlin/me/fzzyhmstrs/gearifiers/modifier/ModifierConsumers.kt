package me.fzzyhmstrs.gearifiers.modifier

import me.fzzyhmstrs.amethyst_core.modifier_util.EquipmentModifier
import me.fzzyhmstrs.amethyst_core.trinket_util.EffectQueue
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack

object ModifierConsumers {

    val DEMONIC_HIT_CONSUMER: EquipmentModifier.ToolConsumer =
        EquipmentModifier.ToolConsumer { _: ItemStack, user: LivingEntity, target: LivingEntity? ->
            if (target == null) return@ToolConsumer
            if (user.world.random.nextFloat() < 0.3333333f){
                EffectQueue.addStatusToQueue(target,StatusEffects.WEAKNESS,50,1)
            }
        }

    val INDOMITABLE_DAMAGE_FUNCTION: EquipmentModifier.DamageFunction =
        EquipmentModifier.DamageFunction { _, user, _, _, amount ->
            if (user.world.random.nextFloat() < 0.15f){
                EffectQueue.addStatusToQueue(user,StatusEffects.REGENERATION,80,0)
            }
            if (user.world.random.nextFloat() < 0.15f){
                EffectQueue.addStatusToQueue(user,StatusEffects.RESISTANCE,80,0)
            }
            amount
        }

    val MANIC_HIT_CONSUMER: EquipmentModifier.ToolConsumer =
        EquipmentModifier.ToolConsumer { _: ItemStack, user: LivingEntity, target: LivingEntity? ->
            if (target == null) return@ToolConsumer
            if (user.world.random.nextFloat() < 0.3333333f){
                EffectQueue.addStatusToQueue(target,StatusEffects.WEAKNESS,50,1)
            }
        }

    class DamageMultiplierFunction(private val multiplier: Float): EquipmentModifier.DamageFunction{
        override fun test(
            stack: ItemStack,
            user: LivingEntity,
            attacker: LivingEntity?,
            source: DamageSource,
            amount: Float
        ): Float {
            return amount * (1f + multiplier)
        }
    }

    class DamageAdderFunction(private val adder: Float): EquipmentModifier.DamageFunction{
        override fun test(
            stack: ItemStack,
            user: LivingEntity,
            attacker: LivingEntity?,
            source: DamageSource,
            amount: Float
        ): Float {
            return amount + adder
        }
    }
}