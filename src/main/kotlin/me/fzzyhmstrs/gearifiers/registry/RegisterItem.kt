package me.fzzyhmstrs.gearifiers.registry

import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.gearifiers.item.RepairKitItem
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object RegisterItem {

    internal val regItem:MutableMap<String, Item> = mutableMapOf()

    val REPAIR_KIT = RepairKitItem(FabricItemSettings().maxCount(1)).also { regItem["repair_kit"] = it }


    fun registerAll(){
        for (entry in regItem){
            Registry.register(Registries.ITEM, Identifier(Gearifiers.MOD_ID,entry.key), entry.value)
        }
    }


}