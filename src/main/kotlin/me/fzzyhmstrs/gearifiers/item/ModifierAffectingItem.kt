package me.fzzyhmstrs.gearifiers.item

import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.fzzy_core.item_util.CustomFlavorItem
import me.fzzyhmstrs.gear_core.GC
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

abstract class ModifierAffectingItem(settings: Settings): CustomFlavorItem(settings) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        val stack2 = user.getStackInHand(if(hand == Hand.MAIN_HAND) Hand.OFF_HAND else Hand.MAIN_HAND)
        val item2 = stack2.item
        if (item2 !is Modifiable) return TypedActionResult.fail(stack)
        if (!item2.canBeModifiedBy(GC.EquipmentModifierType)) return TypedActionResult.fail(stack)
        return modifyOnUse(stack2, stack, world, user, hand)
    }

    abstract fun modifyOnUse(stack: ItemStack, modifierAffectingItem: ItemStack, world: World, user: PlayerEntity, hand:Hand): TypedActionResult<ItemStack>
}