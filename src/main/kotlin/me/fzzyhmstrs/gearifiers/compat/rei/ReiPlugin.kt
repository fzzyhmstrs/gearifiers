@file:Suppress("LocalVariableName")

package me.fzzyhmstrs.gearifiers.compat.rei

import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper
import me.fzzyhmstrs.gearifiers.compat.ClientItemCostLoader
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import net.minecraft.registry.Registries


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
            if (GearifiersConfig.blackList.isItemBlackListed(item)) continue
            if (!item.canBeModifiedBy(EquipmentModifierHelper.getType())) continue
            val costs = ClientItemCostLoader.getItemCosts(item)
            registry.add(RerollAltarDisplay(item, costs))
        }
    }
}
