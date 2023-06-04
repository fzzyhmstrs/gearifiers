package me.fzzyhmstrs.gearifiers.registry

import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.gearifiers.item.*
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry

object RegisterItem {

    internal val regItem:MutableMap<String, Item> = mutableMapOf()

    val REPAIR_KIT = RepairKitItem(FabricItemSettings().maxCount(1).group(ItemGroup.TOOLS)).also { regItem["repair_kit"] = it }
    val SEAL_OF_AWAKENING = SealOfAwakeningItem(FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON).group(ItemGroup.TOOLS)).also { regItem["seal_of_awakening"] = it }
    val SEAL_OF_CHAOS = SealOfChaosItem(FabricItemSettings().maxCount(1).rarity(Rarity.RARE).group(ItemGroup.TOOLS)).also { regItem["seal_of_chaos"] = it }
    val SEAL_OF_CLEANSING = SealOfCleansingItem(FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON).group(ItemGroup.TOOLS)).also { regItem["seal_of_cleansing"] = it }
    val SEAL_OF_FATE = SealOfFateItem(FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON).group(ItemGroup.TOOLS)).also { regItem["seal_of_fate"] = it }
    val SEAL_OF_LEGENDS = SealOfLegendsItem(FabricItemSettings().maxCount(1).rarity(Rarity.EPIC).group(ItemGroup.TOOLS)).withGlint().also { regItem["seal_of_legends"] = it }
    val SEAL_OF_TRANSFERAL = SealOfTransferalItem(FabricItemSettings().maxCount(1).rarity(Rarity.EPIC).group(ItemGroup.TOOLS)).withGlint().also { regItem["seal_of_transferal"] = it }
    val SEAL_OF_WIPING = SealOfWipingItem(FabricItemSettings().maxCount(1).rarity(Rarity.EPIC).group(ItemGroup.TOOLS)).withGlint().also { regItem["seal_of_wiping"] = it }
    val WHETSTONE = WhetstoneItem(FabricItemSettings().maxCount(1).group(ItemGroup.TOOLS)).also { regItem["whetstone"] = it }

    fun registerAll(){
        for (entry in regItem){
            Registry.register(Registry.ITEM, Identifier(Gearifiers.MOD_ID,entry.key), entry.value)
        }
    }


}