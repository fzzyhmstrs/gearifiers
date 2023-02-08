package me.fzzyhmstrs.gearifiers.compat.emi

import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiStack
import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.gearifiers.config.ItemCostLoader
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper
import me.fzzyhmstrs.gearifiers.Gearifiers
import net.minecraft.util.Identifier

object EmiClientPlugin: EmiPlugin {

    private val ALTAR_ID = Identifier(Gearifiers.MOD_ID,"reroll_altar")
    private val ALTAR_WORKSTATION = EmiStack.of(Gearifiers.REROLL_ALTAR.asItem())
    internal val ALTAR_CATEGORY = new EmiRecipeCategory(ALTAR_ID, ALTAR_WORKSTATION)
    

    override fun register(registry: EmiRegistry) {

        registry.addCategory(ALTAR_CATEGORY)
        registry.addWorkstation(ALTAR_CATEGORY, ALTAR_WORKSTATION)

        for (key in Registry.ITEM.getEntrySet()){
            val item = key.value()
            if (item !is Modifiable) continue
            if (item.getModifierInitializer() != EquipmentModifierHelper) continue
            val costs = ItemCostLoader.getItemCosts(item)
            if (costs.isEmpty()){
                registry.addRecipe(AltarEmiRecipe(item,GearifiersConfig.fallbackCost))
            } else {
                for (cost in costs){
                    registry.addRecipe(AltarEmiRecipe(item,cost))
                }
            }
            TODO()
        }
    }
}
