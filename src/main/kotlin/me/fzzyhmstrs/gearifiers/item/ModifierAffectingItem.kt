package me.fzzyhmstrs.gearifiers.item

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.fzzy_core.item_util.CustomFlavorItem
import me.fzzyhmstrs.fzzy_core.item_util.interfaces.Flavorful
import me.fzzyhmstrs.gear_core.GC
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

abstract class ModifierAffectingItem(settings: Settings): Item(settings), Flavorful<ModifierAffectingItem> {

    override var glint = false
    override var flavor: String = ""
    override var flavorDesc: String = ""

    private val flavorText: MutableText by lazy{
        makeFlavorText()
    }

    private val flavorTextDesc: MutableText by lazy{
        makeFlavorTextDesc()
    }

    private fun makeFlavorText(): MutableText {
        val id = Registries.ITEM.getId(this)
        val key = "item.${id.namespace}.${id.path}.flavor"
        val text = AcText.translatable(key).formatted(Formatting.WHITE, Formatting.ITALIC)
        if (text.string == key) return AcText.empty()
        return text
    }

    private fun makeFlavorTextDesc(): MutableText {
        val id = Registries.ITEM.getId(this)
        val key = "item.${id.namespace}.${id.path}.flavor.desc"
        val text = AcText.translatable(key).formatted(Formatting.WHITE)
        if (text.string == key) return AcText.empty()
        return text
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        addFlavorText(tooltip, context)
    }

    override fun hasGlint(stack: ItemStack): Boolean {
        return if (glint) {
            true
        } else {
            super.hasGlint(stack)
        }
    }

    override fun flavorText(): MutableText {
        return flavorText
    }
    override fun flavorDescText(): MutableText {
        return flavorTextDesc
    }

    override fun getFlavorItem(): ModifierAffectingItem {
        return this
    }

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