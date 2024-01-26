package me.fzzyhmstrs.gearifiers.item

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifier
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class SealOfLegendsItem(settings: Settings): ModifierAffectingItem(settings) {
    override fun modifyOnUse(
        stack: ItemStack,
        modifierAffectingItem: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        if (world.isClient) return TypedActionResult.pass(modifierAffectingItem)
        val uses = stack.nbt?.getInt("seal_legend_uses") ?: 0
        if (uses >= GearifiersConfig.modifiers.maxLegendarySealUses){
            world.playSound(null,user.blockPos, SoundEvents.BLOCK_LAVA_EXTINGUISH,SoundCategory.PLAYERS,1.0f, world.random.nextFloat()*0.4f + 0.8f)
            user.sendMessage(AcText.translatable(""))
            return TypedActionResult.fail(modifierAffectingItem)
        }
        val list = EquipmentModifierHelper.getTargetsForItem(stack).stream().filter { it.rarity.beneficial &&( it.rarity == EquipmentModifier.Rarity.LEGENDARY || it.rarity == EquipmentModifier.Rarity.EPIC) }.toList()
        var success = false
        var tries = 0
        while(!success && tries < 10){
            success = EquipmentModifierHelper.addModifier(list[world.random.nextInt(list.size)].modifierId,stack)
            tries++
        }
        if (!success){
            world.playSound(null,user.blockPos, SoundEvents.BLOCK_LAVA_EXTINGUISH,SoundCategory.PLAYERS,1.0f, world.random.nextFloat()*0.4f + 0.8f)
            return TypedActionResult.fail(modifierAffectingItem)
        } else{
            stack.orCreateNbt.putInt("seal_legend_uses",uses + 1)
        }
        modifierAffectingItem.decrement(modifierAffectingItem.count)
        user.incrementStat(Stats.BROKEN.getOrCreateStat(modifierAffectingItem.item))
        user.sendToolBreakStatus(hand)
        world.playSound(null,user.blockPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,SoundCategory.PLAYERS,1.0f, world.random.nextFloat()*0.4f + 0.8f)
        world.playSound(null,user.blockPos, SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR,SoundCategory.PLAYERS,1.0f, world.random.nextFloat()*0.4f + 0.8f)
        return TypedActionResult.success(modifierAffectingItem)
    }
}