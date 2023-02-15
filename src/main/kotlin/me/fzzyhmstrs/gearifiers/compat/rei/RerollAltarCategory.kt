package me.fzzyhmstrs.amethyst_imbuement.compat.rei

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import me.fzzyhmstrs.gearifiers.Gearifiers
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Slot
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting


@Suppress("UnstableApiUsage")
class RerollAltarCategory: DisplayCategory<RerollAltarDisplay> {
    override fun getIcon(): Renderer {
        return getIconEntryStack()
    }
    fun getIconEntryStack(): EntryStack<ItemStack> {
        return EntryStacks.of(ItemStack(RegisterBlock.IMBUING_TABLE.asItem()))
    }

    override fun getTitle(): Text {
        return AcText.translatable("recipe.reroll_altar")
    }

    override fun getCategoryIdentifier(): CategoryIdentifier<RerollAltarDisplay> {
        return CategoryIdentifier.of(Identifier(Gearifiers.MOD_ID, "rerolling"))
    }

    override fun setupDisplay(display: RerollAltarDisplay, bounds: Rectangle): MutableList<Widget> {
        val widgets: MutableList<Widget> = mutableListOf()
        val xOffset = 0
        val yOffset = 0
        widgets.add(Widgets.createCategoryBase(bounds))

        val slot1: Slot = Widgets.createSlot(Point(bounds.x + xOffset, bounds.y + yOffset))
        slot1.entries(display.inputEntries[0])
        widgets.add(slot1)

        val slot2: Slot = Widgets.createSlot(Point(bounds.x + xOffset + 49, bounds.y + yOffset))
        slot2.entries(display.inputEntries[1])
        widgets.add(slot2)
        
        val arrow1 = Widgets.createArrow(Point(bounds.x + xOffset + 75, bounds.y + yOffset))
        widgets.add(arrow1)

        val outputSlotBackground =
            Widgets.createResultSlotBackground(Point(bounds.x + xOffset + 107, bounds.y + yOffset))
        widgets.add(outputSlotBackground)

        val outputSlot =
            Widgets.createSlot(Point(bounds.x + xOffset + 107, bounds.y + yOffset))
        outputSlot.entries(display.outputEntries[0])
        widgets.add(outputSlot)

        if (GearifiersConfig.modifiers.enableRerollXpCost){
            val text1 = Widgets.createLabel(Point(bounds.x + xOffset + 10, bounds.y + yOffset + 23),AcText.translatable("emi.category.gearifiers.reroll_altar.cost_text_1").Formatted(Formatting.GREEN))
            widgets.add(text1)
            val text1 = Widgets.createLabel(Point(bounds.x + xOffset + 62, bounds.y + yOffset + 23),AcText.translatable("emi.category.gearifiers.reroll_altar.cost_text_2").Formatted(Formatting.GREEN))
            widgets.add(text1)
        }

        return widgets
    }

    override fun getDisplayHeight(): Int {
        return if (GearifiersConfig.modifiers.enableRerollXpCost){36}else{18}
    }

    override fun getDisplayWidth(display: ImbuingTableDisplay): Int {
        return 125
    }


}
