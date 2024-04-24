package me.fzzyhmstrs.gearifiers.compat.rei

import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
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
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier


@Suppress("UnstableApiUsage")
class RerollAltarCategory: DisplayCategory<RerollAltarDisplay> {
    override fun getIcon(): Renderer {
        return getIconEntryStack()
    }
    fun getIconEntryStack(): EntryStack<ItemStack> {
        return EntryStacks.of(ItemStack(Gearifiers.REROLL_ALTAR))
    }

    override fun getTitle(): Text {
        return AcText.translatable("recipe.reroll_altar")
    }

    override fun getCategoryIdentifier(): CategoryIdentifier<RerollAltarDisplay> {
        return CategoryIdentifier.of(Identifier(Gearifiers.MOD_ID, "rerolling"))
    }

    override fun setupDisplay(display: RerollAltarDisplay, bounds: Rectangle): MutableList<Widget> {
        val widgets: MutableList<Widget> = mutableListOf()
        val xOffset = 5
        val yOffset = 5
        widgets.add(Widgets.createCategoryBase(bounds))

        val slot1: Slot = Widgets.createSlot(Point(bounds.x + xOffset + 12, bounds.y + yOffset)).markInput()
        slot1.entries(display.inputEntries[0])
        widgets.add(slot1)

        val slot2: Slot = Widgets.createSlot(Point(bounds.x + xOffset + 38, bounds.y + yOffset)).markInput()
        slot2.entries(display.inputEntries[1])
        widgets.add(slot2)

        val arrow1 = Widgets.withTooltip(Widgets.createArrow(Point(bounds.x + xOffset + 64, bounds.y + yOffset + 1)),AcText.translatable("emi.category.gearifiers.reroll_altar.tooltip_3"))
        widgets.add(arrow1)

/*        val outputSlotBackground =
            Widgets.createSlot(Point(bounds.x + xOffset + 107, bounds.y + yOffset))
        widgets.add(outputSlotBackground)*/

        val outputSlot =
            Widgets.createSlot(Point(bounds.x + xOffset + 96, bounds.y + yOffset)).markOutput()
        outputSlot.entries(display.outputEntries[0])
        widgets.add(outputSlot)

        if (GearifiersConfig.getInstance().modifiers.rerollCosts.enabled){
            val text1 = Widgets.createLabel(Point(bounds.x + xOffset + 33, bounds.y + yOffset + 23),AcText.translatable("emi.category.gearifiers.reroll_altar.cost_text_1").formatted(Formatting.GREEN))
            widgets.add(text1)
            val text2Text = AcText.translatable("emi.category.gearifiers.reroll_altar.cost_text_2").formatted(Formatting.GREEN)
            val text2TextWidth = MinecraftClient.getInstance().textRenderer.getWidth(text2Text)
            val text2 = Widgets.createLabel(Point(bounds.x + xOffset + 62 + (text2TextWidth/2), bounds.y + yOffset + 23),text2Text)
            widgets.add(text2)
            val orb1 = Widgets.withTooltip(XpOrbWidget(bounds.x + xOffset + 10, bounds.y + yOffset + 20,GearifiersConfig.getInstance().modifiers.rerollCosts.firstRerollCost),AcText.translatable("emi.category.gearifiers.reroll_altar.tooltip_1"))
            widgets.add(orb1)
            val orb2 = Widgets.withTooltip(XpOrbWidget(bounds.x + xOffset + 41, bounds.y + yOffset + 20,GearifiersConfig.getInstance().modifiers.rerollCosts.addedPerReroll),AcText.translatable("emi.category.gearifiers.reroll_altar.tooltip_2"))
            widgets.add(orb2)
        }

        return widgets
    }

    override fun getDisplayHeight(): Int {
        return if (GearifiersConfig.getInstance().modifiers.rerollCosts.enabled){46}else{28}
    }

    override fun getDisplayWidth(display: RerollAltarDisplay): Int {
        return 135
    }


}