package me.fzzyhmstrs.gearifiers.item

import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class SealOfTransferalItem(settings: Settings): ModifierAffectingItem(settings) {
    override fun modifyOnUse(
        stack: ItemStack,
        modifierAffectingItem: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        if (world.isClient) return TypedActionResult.pass(modifierAffectingItem)
        val nbt = modifierAffectingItem.nbt
        if (nbt == null || !nbt.contains("modifier_list")) {
            val list = EquipmentModifierHelper.getModifiers(stack)
            val nbtList = NbtList()
            for (modifier in list){
                nbtList.add(NbtString.of(modifier.toString()))
            }
            modifierAffectingItem.orCreateNbt.put("modifier_list",nbtList)
            EquipmentModifierHelper.removeAllModifiers(stack)
            world.playSound(null,user.blockPos, SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK,SoundCategory.PLAYERS,1.0f, world.random.nextFloat()*0.4f + 0.8f)
            return TypedActionResult.success(modifierAffectingItem)
        }
        val storedModifiers = modifierAffectingItem.orCreateNbt.getList("modifier_list",NbtElement.STRING_TYPE.toInt())
        for (modifier in storedModifiers){
            val modId = Identifier(modifier.asString())
            EquipmentModifierHelper.addModifierAsIs(modId,stack,false)
        }
        modifierAffectingItem.decrement(modifierAffectingItem.count)
        user.incrementStat(Stats.BROKEN.getOrCreateStat(modifierAffectingItem.item))
        user.sendToolBreakStatus(hand)
        world.playSound(null,user.blockPos, SoundEvents.ENTITY_EVOKER_CAST_SPELL,SoundCategory.PLAYERS,1.0f, world.random.nextFloat()*0.4f + 0.8f)
        return TypedActionResult.success(modifierAffectingItem)
    }
}