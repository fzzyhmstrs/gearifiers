package me.fzzyhmstrs.gearifiers.registry

import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.gearifiers.item.*
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity

object RegisterItem {


    internal val regItem: MutableList<Item> = mutableListOf()

    private fun <T: Item> register(item: T, name: String): T {
        regItem.add(item)
        return FzzyPort.ITEM.register(Identifier(Gearifiers.MOD_ID,name), item)
    }

    val REPAIR_KIT = register(RepairKitItem(FabricItemSettings().maxCount(1)), "repair_kit")
    val SEAL_OF_AWAKENING = register(SealOfAwakeningItem(FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON)), "seal_of_awakening")
    val SEAL_OF_CHAOS = register(SealOfChaosItem(FabricItemSettings().maxCount(1).rarity(Rarity.RARE)), "seal_of_chaos")
    val SEAL_OF_CLEANSING = register(SealOfCleansingItem(FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON)), "seal_of_cleansing")
    val SEAL_OF_FATE = register(SealOfFateItem(FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON)), "seal_of_fate")
    val SEAL_OF_LEGENDS = register(SealOfLegendsItem(FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)).withGlint(), "seal_of_legends")
    val SEAL_OF_TRANSFERAL = register(SealOfTransferalItem(FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)).withGlint(), "seal_of_transferal")
    val SEAL_OF_WIPING = register(SealOfWipingItem(FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)).withGlint(), "seal_of_wiping")
    val WHETSTONE = register(WhetstoneItem(FabricItemSettings().maxCount(1)), "whetstone")

    fun registerAll(){
    }


}