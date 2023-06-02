package me.fzzyhmstrs.gearifiers.item

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class RepairKitItem(settings: Settings): ModifierAffectingItem(settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        val stack2 = user.getStackInHand(if(hand == Hand.MAIN_HAND) Hand.OFF_HAND else Hand.MAIN_HAND)
        if (!stack2.isDamageable) return TypedActionResult.fail(stack)
        return modifyOnUse(stack2, stack, world, user, hand)
    }

    override fun modifyOnUse(
        stack: ItemStack,
        modifierAffectingItem: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        if (world.isClient) return TypedActionResult.pass(modifierAffectingItem)
        if (!stack.isDamaged) return TypedActionResult.fail(modifierAffectingItem)
        val l: Int = stack.damage.coerceAtMost(stack.maxDamage / 4)
        if (l <= 0) return TypedActionResult.fail(modifierAffectingItem)
        val n: Int = stack.damage - l
        stack.damage = n
        world.playSound(null,user.blockPos, SoundEvents.BLOCK_ANVIL_USE,SoundCategory.PLAYERS,1.0f, world.random.nextFloat()*0.4f + 0.8f)
        val nbt = modifierAffectingItem.orCreateNbt
        val uses = nbt.getInt("repair_uses")
        if (uses == 7){
            modifierAffectingItem.decrement(modifierAffectingItem.count)
            user.incrementStat(Stats.BROKEN.getOrCreateStat(modifierAffectingItem.item))
            user.sendToolBreakStatus(hand)
            world.playSound(null,user.blockPos, SoundEvents.BLOCK_ANVIL_BREAK,SoundCategory.PLAYERS,1.0f, world.random.nextFloat()*0.4f + 0.8f)
        } else {
            nbt.putInt("repair_uses",uses + 1)
        }

        return TypedActionResult.pass(modifierAffectingItem)
    }
}