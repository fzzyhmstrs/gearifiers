package me.fzzyhmstrs.gearifiers.compat.emi

import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper
import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.gearifiers.compat.ClientItemCostLoader
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object EmiClientPlugin: EmiPlugin {

    private val ALTAR_ID = Identifier(Gearifiers.MOD_ID,"reroll_altar")
    private val ALTAR_WORKSTATION = EmiStack.of(Gearifiers.REROLL_ALTAR.asItem())
    internal val ALTAR_CATEGORY = EmiRecipeCategory(ALTAR_ID, ALTAR_WORKSTATION)
    

    override fun register(registry: EmiRegistry) {

        registry.addCategory(ALTAR_CATEGORY)
        registry.addWorkstation(ALTAR_CATEGORY, ALTAR_WORKSTATION)

        for (key in Registry.ITEM.entrySet){
            val item = key.value
            if (item !is Modifiable) continue
            if (GearifiersConfig.blackList.isItemBlackListed(item)) continue
            if (!item.canBeModifiedBy(EquipmentModifierHelper.getType())) continue
            val costs = ClientItemCostLoader.getItemCosts(item)
            val ingredient = EmiIngredient.of(costs.stream().map { cost -> EmiStack.of(cost) }.toList())
            registry.addRecipe(AltarEmiRecipe(item,ingredient))
        }
    }
}
