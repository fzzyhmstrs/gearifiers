package me.fzzyhmstrs.gearifiers.item

import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.loot.provider.number.BinomialLootNumberProvider
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class SealOfAwakeningItem(settings: Settings): ModifierAffectingItem(settings) {

    private val DEFAULT_MODIFIER_TOLL = BinomialLootNumberProvider.create(25,0.24f)

    override fun modifyOnUse(
        stack: ItemStack,
        modifierAffectingItem: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        if (world !is ServerWorld) return TypedActionResult.pass(modifierAffectingItem)
        val list = EquipmentModifierHelper.getTargetsForItem(stack).stream().filter { it.rarity.beneficial }.toList()
        val parameters = LootContextParameterSet.Builder(world).luck(user.luck).build(LootContextTypes.EMPTY)
        val seed = world.random.nextLong().takeIf { it != 0L }?:1L
        val contextBuilder = LootContext.Builder(parameters).random(seed)
        val context = contextBuilder.build(null)
        val result: MutableList<Identifier> = mutableListOf()
        do{
            var tollRemaining = (DEFAULT_MODIFIER_TOLL.nextFloat(context) + context.luck).toInt()
            while (tollRemaining > 0){
                val modChk = list[context.random.nextInt(list.size)]
                tollRemaining -= modChk.toll.nextFloat(context).toInt()
                if (tollRemaining >= 0) {
                    result.add(modChk.modifierId)
                }
            }
        } while (result.isEmpty())
        var success = false
        var tries = 0
        while(!success && tries < 10){
            success = EquipmentModifierHelper.addModifier(result[world.random.nextInt(result.size)],stack)
            tries++
        }

        modifierAffectingItem.decrement(modifierAffectingItem.count)
        user.incrementStat(Stats.BROKEN.getOrCreateStat(modifierAffectingItem.item))
        user.sendToolBreakStatus(hand)
        world.playSound(null,user.blockPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,SoundCategory.PLAYERS,1.0f, world.random.nextFloat()*0.4f + 0.8f)
        world.playSound(null,user.blockPos, SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR,SoundCategory.PLAYERS,1.0f, world.random.nextFloat()*0.4f + 0.8f)
        return TypedActionResult.success(modifierAffectingItem)
    }
}