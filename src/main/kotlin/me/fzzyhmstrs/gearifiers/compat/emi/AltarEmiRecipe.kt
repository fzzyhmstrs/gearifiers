package me.fzzyhmstrs.gearifiers.compat.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import net.minecraft.item.Item
import net.minecraft.util.Identifier

class AltarEmiRecipe(private val input: Item, private val cost: EmiIngredient): EmiRecipe{

    private val inputStack = EmiStack.of(input)
    private val recipeId: Identifier by lazy {
        prepareId()
    }
    private val costText1 = AcText.translatable("emi.category.gearifiers.reroll_altar.cost_text_1").asOrderedText()
    private val costText2 = AcText.translatable("emi.category.gearifiers.reroll_altar.cost_text_2").asOrderedText()
    private val rerollText = AcText.translatable("emi.category.gearifiers.reroll_altar.tooltip_3")

    private fun prepareId(): Identifier{
        val itemId1 = FzzyPort.ITEM.getId(input)
        var itemId2 = ""
        val ids = cost.emiStacks.stream().map { stack -> stack.id }
        ids.forEach{
            itemId2 += "/"
            itemId2 += (it.namespace + "." + it.path)

        }
        return Identifier(Gearifiers.MOD_ID, itemId1.namespace + "." + itemId1.path + "/paid_with" + itemId2)
    }


    override fun getCategory(): EmiRecipeCategory{
        return EmiClientPlugin.ALTAR_CATEGORY
    }

    override fun getId(): Identifier{
        return recipeId
    }

    override fun getInputs(): List<EmiIngredient>{
        return listOf(inputStack,cost)
    }

    override fun getOutputs(): List<EmiStack>{
        return listOf(inputStack)
    }

    override fun getDisplayWidth(): Int{
        return 125
    }

    override fun getDisplayHeight(): Int{
        return if (GearifiersConfig.getInstance().modifiers.rerollCosts.enabled){36}else{18}
    }

    override fun supportsRecipeTree(): Boolean{
        return false
    }

    override fun addWidgets(widgets: WidgetHolder){
        widgets.addTexture(EmiTexture.PLUS, 27, 2)
		    widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1)
		    widgets.addSlot(inputStack, 0, 0)
		    widgets.addSlot(cost, 49, 0)
		    widgets.addSlot(inputStack, 107, 0).recipeContext(this).appendTooltip(rerollText)
        if (GearifiersConfig.getInstance().modifiers.rerollCosts.enabled){
            widgets.add(XpOrbWidget(10,20,GearifiersConfig.getInstance().modifiers.rerollCosts.firstRerollCost,"emi.category.gearifiers.reroll_altar.tooltip_1"))
            widgets.addText(costText1,31,23,0x55FF55,true)
            widgets.add(XpOrbWidget(41,20,GearifiersConfig.getInstance().modifiers.rerollCosts.addedPerReroll,"emi.category.gearifiers.reroll_altar.tooltip_2"))
            widgets.addText(costText2,62,23,0x55FF55,true)
        }
    }

}