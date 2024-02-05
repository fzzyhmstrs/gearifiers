package me.fzzyhmstrs.gearifiers.modifier

import me.fzzyhmstrs.gear_core.modifier_util.BaseFunctions
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifier
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World

object ModifierFunctions {

    val VORPAL_ATTACK_FUNCTION: EquipmentModifier.DamageFunction =
        EquipmentModifier.DamageFunction { _, user, _, _, amount ->
            if (user.world.random.nextFloat() < GearifiersConfig.chances.vorpalChance){
                amount * 10f
            } else {
                amount
            }
        }


    val INDOMITABLE_DAMAGE_FUNCTION: EquipmentModifier.DamageFunction =
        EquipmentModifier.DamageFunction { _, user, _, _, amount ->
            if (user.world.random.nextFloat() < GearifiersConfig.chances.indomitableChance){
                user.addStatusEffect(StatusEffectInstance(StatusEffects.REGENERATION,80,0))
            }
            if (user.world.random.nextFloat() < GearifiersConfig.chances.indomitableChance){
                user.addStatusEffect(StatusEffectInstance(StatusEffects.RESISTANCE,80,0))
            }
            amount
        }

    val SHIELDING_DAMAGE_FUNCTION: EquipmentModifier.DamageFunction =
        EquipmentModifier.DamageFunction { _, user, _, _, amount ->
            if (user.world.random.nextFloat() < GearifiersConfig.chances.shieldingChance){
                user.world.playSound(null,user.blockPos,SoundEvents.ITEM_SHIELD_BLOCK,SoundCategory.PLAYERS,1.0f, 0.8f + user.world.random.nextFloat() * 0.4f)
                0f
            } else {
                amount
            }
        }

    val EXPLOSIVE_ATTACK_FUNCTION: EquipmentModifier.DamageFunction =
        EquipmentModifier.DamageFunction{ _, user, _, source, amount ->
            if (user.world.random.nextFloat() < 0.33f) {
                val projectile = source.source as? PersistentProjectileEntity ?: return@DamageFunction amount
                user.world.createExplosion(
                    projectile,
                    projectile.x,
                    projectile.y,
                    projectile.z,
                    1f,
                    World.ExplosionSourceType.NONE
                )
            }
            amount
        }

    val VAMPIRIC_ATTACK_FUNCTION: EquipmentModifier.DamageFunction =
        EquipmentModifier.DamageFunction{ _, user, _, _, amount ->
            user.heal(amount * 0.075f)
            amount
        }

    class DemonBarbedAttackFunction(multiplier: Float = 1f): BaseFunctions.RangedAttackFunction(multiplier){
        override fun test(
            stack: ItemStack,
            user: LivingEntity,
            attacker: LivingEntity?,
            source: DamageSource,
            amount: Float
        ): Float {
            attacker?.addStatusEffect(StatusEffectInstance(StatusEffects.WEAKNESS,100,1))
            if (user.world.random.nextFloat() < 0.25f)
                attacker?.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS,100))
            return super.test(stack, user, attacker, source, amount)
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
            attacker?.addStatusEffect(StatusEffectInstance(StatusEffects.POISON,dur,amp))
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
            attacker?.addStatusEffect(StatusEffectInstance(StatusEffects.WITHER,dur,amp))
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
}
