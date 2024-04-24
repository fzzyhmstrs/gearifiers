package me.fzzyhmstrs.gearifiers.item

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper
import me.fzzyhmstrs.gearifiers.config.GearifiersConfigNew
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class SealOfChaosItem(settings: Settings): ModifierAffectingItem(settings) {
    override fun modifyOnUse(
        stack: ItemStack,
        modifierAffectingItem: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        if (world.isClient) return TypedActionResult.pass(modifierAffectingItem)
        if (GearifiersConfigNew.getInstance().isItemBlackListed(stack)) {
            user.sendMessage(AcText.translatable("item.gearifiers.seals.blacklisted"))
            return TypedActionResult.pass(modifierAffectingItem)
        }
        val list = GearifiersConfigNew.getInstance().modifiers.getApplicableModifiers(stack)
        var success = false
        var tries = 0
        while(!success && tries < 10){
            success = EquipmentModifierHelper.addModifier(list[world.random.nextInt(list.size)].modifierId,stack)
            tries++
        }
        modifierAffectingItem.decrement(modifierAffectingItem.count)
        user.incrementStat(Stats.BROKEN.getOrCreateStat(modifierAffectingItem.item))
        user.sendToolBreakStatus(hand)
        world.playSound(null,user.blockPos, SoundEvents.ENTITY_GENERIC_EXPLODE,SoundCategory.PLAYERS,1.0f, world.random.nextFloat()*0.4f + 0.8f)
        world.playSound(null,user.blockPos, SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST_FAR,SoundCategory.PLAYERS,1.0f, world.random.nextFloat()*0.4f + 0.8f)
        return TypedActionResult.success(modifierAffectingItem)
    }
}
