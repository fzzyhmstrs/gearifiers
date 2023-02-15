@file:Suppress("LocalVariableName")

package me.fzzyhmstrs.gearifiers.compat.rei

import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry


object ReiPlugin: REIClientPlugin {

    private val REROLL_CATEGORY = RerollAltarCategory()

    override fun registerCategories(registry: CategoryRegistry) {
        registry.add(REROLL_CATEGORY)
        registry.addWorkstations(REROLL_CATEGORY.categoryIdentifier,REROLL_CATEGORY.getIconEntryStack())
    }

    override fun registerDisplays(registry: DisplayRegistry) {
        for (key in Registries.ITEM.entrySet){
            val item = key.value
            if (item !is Modifiable) continue
            if (item.modifierInitializer != EquipmentModifierHelper) continue
            val costs = ClientItemCostLoader.getItemCosts(item)
            if (costs.isEmpty()){
                registry.add(RerollAltarDisplay(item,GearifiersConfig.fallbackCost))
            } else {
                for (cost in costs){
                    registry.add(RerollAltarDisplay(item,cost))
                }
            }
        }
    }
}
