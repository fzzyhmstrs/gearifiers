package me.fzzyhmstrs.gearifiers.item

import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class SealOfWipingItem(settings: Settings): ModifierAffectingItem(settings) {
    override fun modifyOnUse(
        stack: ItemStack,
        modifierAffectingItem: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        if (world.isClient) return TypedActionResult.pass(modifierAffectingItem)

        val list = EquipmentModifierHelper.getModifiers(stack)
        if (list.isEmpty()) return TypedActionResult.fail(modifierAffectingItem)
        var fail = true
        for (modId in list){
            val mod = EquipmentModifierHelper.getModifierByType(modId)?:continue
            if (!mod.rarity.beneficial) {
                EquipmentModifierHelper.removeModifier(modId, stack)
                fail = false
                break
            }
        }
        if (fail) return TypedActionResult.fail(modifierAffectingItem)
        modifierAffectingItem.decrement(modifierAffectingItem.count)
        user.incrementStat(Stats.BROKEN.getOrCreateStat(modifierAffectingItem.item))
        user.sendToolBreakStatus(hand)
        world.playSound(null,user.blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH,SoundCategory.PLAYERS,1.0f, world.random.nextFloat()*0.4f + 0.8f)
        world.playSound(null,user.blockPos, SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR,SoundCategory.PLAYERS,1.0f, world.random.nextFloat()*0.4f + 0.8f)
        return TypedActionResult.success(modifierAffectingItem)
    }
}