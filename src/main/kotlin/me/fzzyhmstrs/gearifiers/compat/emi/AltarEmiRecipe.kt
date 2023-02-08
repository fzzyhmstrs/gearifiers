package me.fzzyhmstrs.gearifiers.compat.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.util.Identifier
import net.minecraft.util.Registry

class AltarEmiRecipe(private val input: Item, private val cost: Item): EmiRecipe{

    private val inputStack = EmiStack.of(input)
    private val costStack = EmiStack.of(cost)
    private val id = Identifier(Gearifiers.MOD_ID,Registry.ITEM.getId(input).path + "/paid_with/" + Registry.ITEM.getId(cost))
    private val costText = AcText.translatable("emi.category.gearifiers.reroll_altar.cost_text",GearifiersConfig.modifiers.firstRerollXpCost,GearifiersConfig.modifiers.addedRerollXpCostPerRoll)
  
    override fun getCategory(): EmiRecipeCategory{
        return EmiClientPlugin.ALTAR_CATEGORY
    }
    
    override fun getId(): Identifier{
        return id
    }
    
    override fun getInputs(): List<EmiIngredient>{
        return listOf(inputStack,costStack)
    }
    
    override fun getOutputs(): List<EmiStack>{
        return listOf(inputStack)
    }
    
    override fun getDisplayWidth(): Int{
        return 125
    }
    
    override fun getDisplayHeight(): Int{
        return if (GearifiersConfig.modifiers.enableRerollXpCost){29}else{18}
    }
    
    override fun supportsRecipeTree(): Boolean{
        return false
    }
    
    override fun addWidgets(widgets: WidgetHolder){
        widgets.addTexture(EmiTexture.PLUS, 27, 2)
		    widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1)
		    widgets.addSlot(inputStack, 0, 0)
		    widgets.addSlot(costStack, 49, 0)
		    widgets.addSlot(inputStack, 107, 0).recipeContext(this)
        if (GearifiersConfig.modifiers.enableRerollXpCost){
            widgets.addText(costText,0,19,0x55FF55,true)
        }
    }

}
