package me.fzzyhmstrs.gearifiers.modifier

import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifier
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents

object ModifierFunctions {

    val INDOMITABLE_DAMAGE_FUNCTION: EquipmentModifier.DamageFunction =
        EquipmentModifier.DamageFunction { _, user, _, _, amount ->
            if (user.world.random.nextFloat() < 0.15f){
                EffectQueue.addStatusToQueue(user, StatusEffects.REGENERATION,80,0)
            }
            if (user.world.random.nextFloat() < 0.15f){
                EffectQueue.addStatusToQueue(user, StatusEffects.RESISTANCE,80,0)
            }
            amount
        }

    val SHIELDING_DAMAGE_FUNCTION: EquipmentModifier.DamageFunction =
        EquipmentModifier.DamageFunction { _, user, _, _, amount ->
            if (user.world.random.nextFloat() < 0.025f){
                user.world.playSound(null,user.blockPos,SoundEvents.ITEM_SHIELD_BLOCK,SoundCategory.PLAYERS,1.0f, 0.8f + user.world.random.nextFloat() * 0.4f)
                0f
            } else {
                amount
            }
        }
        
    class PoisonousDamageFunction(private val dur: Int, private val amp: Int): EquipmentModifier.DamageFunction{
        override fun test(
            stack: ItemStack,
            user: LivingEntity,
            attacker: LivingEntity?,
            source: DamageSource,
            amount: Float
        ): Float {
            if(attacker != null){
                EffectQueue.addStatusToQueue(attacker, StatusEffects.POISON,dur,amp)
            }
            return amount
        }
    }
    
    class DesecratedDamageFunction(private val dur: Int, private val amp: Int): EquipmentModifier.DamageFunction{
        override fun test(
            stack: ItemStack,
            user: LivingEntity,
            attacker: LivingEntity?,
            source: DamageSource,
            amount: Float
        ): Float {
            if(attacker != null){
                EffectQueue.addStatusToQueue(attacker, StatusEffects.WITHER,dur,amp)
            }
            return amount
        }
    }

    class CrumblingDamageFunction(private val chance: Float): EquipmentModifier.DamageFunction{
        override fun test(
            stack: ItemStack,
            user: LivingEntity,
            attacker: LivingEntity?,
            source: DamageSource,
            amount: Float
        ): Float {
            if(user.world.random.nextFloat() < chance){
                var found = false
                for (slot in EquipmentSlot.values()){
                    if (user.getEquippedStack(slot) == stack){
                        stack.damage(1,user) { player -> player.sendEquipmentBreakStatus(slot) }
                        found = true
                        break
                    }
                }
                if (!found){
                    stack.damage(1,user) { player -> player.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND) }
                }
            }
            return amount
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
