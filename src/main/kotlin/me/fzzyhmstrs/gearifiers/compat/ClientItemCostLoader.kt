package me.fzzyhmstrs.gearifiers.compat

import com.google.common.collect.HashMultimap
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import net.minecraft.item.ArmorItem
import net.minecraft.item.Item
import net.minecraft.item.ToolItem
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object ClientItemCostLoader {

    private val rawItemCosts: HashMultimap<Identifier,String> = HashMultimap.create()
    private val rawOverrideCosts: HashMultimap<Identifier,String> = HashMultimap.create()
    internal val ITEM_COSTS: HashMultimap<Item, Item> = HashMultimap.create()


    internal fun getItemCosts(item: Item): Set<Item>{
        if (ITEM_COSTS.isEmpty){
            processItemCostsMap()
        }
        val set = ITEM_COSTS.get(item)
        return if (set.isEmpty()) {
            if (GearifiersConfig.modifiers.useRepairIngredientAsRerollCost) {
                val list = getRepairIngredient(item)
                if (list.isEmpty()){
                    setOf(GearifiersConfig.modifiers.fallbackItem())
                } else {
                    setOf(*list.toTypedArray())
                }
            } else {
                setOf(GearifiersConfig.modifiers.fallbackItem())
            }
        } else {
            if (GearifiersConfig.modifiers.useRepairIngredientAsRerollCost && GearifiersConfig.modifiers.repairIngredientOverrideDefinedCosts) {
                val list = getRepairIngredient(item)
                if (list.isEmpty()){
                    set
                } else {
                    setOf(*list.toTypedArray())
                }
            } else {
                set
            }
        }
    }

    private fun getRepairIngredient(item: Item): List<Item>{
        if (item is ArmorItem){
            return item.material.repairIngredient.matchingItemIds.stream().map { id -> FzzyPort.ITEM.get(id) }.toList()
        }
        if (item is ToolItem){
            return item.material.repairIngredient.matchingItemIds.stream().map { id -> FzzyPort.ITEM.get(id) }.toList()
        }
        return listOf()
    }

    private fun processItemCostsMap(){
        //println(rawItemCosts)
        for (entry in rawItemCosts.entries()){
            val costItem = FzzyPort.ITEM.get(entry.key)
            //println("cost item: $costItem")
            val targetItemString = entry.value
            if (targetItemString.startsWith('#') && targetItemString.length > 1){
                val tagId = Identifier.tryParse(targetItemString.substring(1))
                if (tagId != null){
                    val tagKey = FzzyPort.ITEM.tagOf(tagId)
                    val entries = FzzyPort.ITEM.registry().getEntryList(tagKey)
                    if (entries.isPresent){
                        val entriesList = entries.get()
                        entriesList.forEach {
                            ITEM_COSTS.put(it.value(),costItem)
                        }
                    } else {
                        Gearifiers.LOGGER.warn("Tag $tagId referenced from reroll cost ${entry.key} couldn't be found in the Item Registry!")
                    }
                } else {
                    Gearifiers.LOGGER.warn("tag Id $targetItemString referenced under reroll cost ${entry.key} couldn't be parsed as an identifier")
                }
            } else {
                val itemId = Identifier.tryParse(targetItemString)
                //println("parsing targetItemString $targetItemString into identifier $itemId")
                if (itemId != null){
                    if (FzzyPort.ITEM.containsId(itemId)){
                        ITEM_COSTS.put(FzzyPort.ITEM.get(itemId),costItem)
                    } else {
                        Gearifiers.LOGGER.warn("Item id $itemId referenced from reroll cost ${entry.key} couldn't be found in the Item Registry!")
                    }
                } else {
                    Gearifiers.LOGGER.warn("Item id $itemId referenced from reroll cost ${entry.key} couldn't be found in the Item Registry!")
                }
            }
        }
        for (entry in rawOverrideCosts.entries()){
            val costItem = FzzyPort.ITEM.get(entry.key)
            val targetItemString = entry.value
            if (targetItemString.startsWith('#') && targetItemString.length > 1){
                val tagId = Identifier.tryParse(targetItemString.substring(1))
                if (tagId != null){
                    val tagKey = FzzyPort.ITEM.tagOf(tagId)
                    val entries = FzzyPort.ITEM.registry().getEntryList(tagKey)
                    if (entries.isPresent){
                        val entriesList = entries.get()
                        entriesList.forEach {
                            ITEM_COSTS.removeAll(it.value())
                            ITEM_COSTS.put(it.value(),costItem)
                        }
                    } else {
                        Gearifiers.LOGGER.warn("Tag $tagId referenced from reroll cost ${entry.key} couldn't be found in the Item Registry!")
                    }
                } else {
                    Gearifiers.LOGGER.warn("tag Id $targetItemString referenced under reroll cost ${entry.key} couldn't be parsed as an identifier")
                }
            } else {
                val itemId = Identifier.tryParse(targetItemString)
                if (itemId != null){
                    if (FzzyPort.ITEM.containsId(itemId)){
                        ITEM_COSTS.removeAll(FzzyPort.ITEM.get(itemId))
                        ITEM_COSTS.put(FzzyPort.ITEM.get(itemId),costItem)
                    } else {
                        Gearifiers.LOGGER.warn("Item id $itemId referenced from reroll cost ${entry.key} couldn't be found in the Item Registry!")
                    }
                } else {
                    Gearifiers.LOGGER.warn("Item id $itemId referenced from reroll cost ${entry.key} couldn't be found in the Item Registry!")
                }
            }
        }
        //println("prepared map:")
        //println(ITEM_COSTS)
    }

    fun readRawDataFromServer(buf: PacketByteBuf){
        ITEM_COSTS.clear()
        rawItemCosts.clear()
        rawOverrideCosts.clear()
        //println(">>>>>>>>>>>>> reading from server <<<<<<<<<<<<<<<<")
        readMultimapFromBuf(buf, rawItemCosts)
        readMultimapFromBuf(buf, rawOverrideCosts)
        //println(rawItemCosts)
    }

    private fun readMultimapFromBuf(buf: PacketByteBuf,map: HashMultimap<Identifier,String>){
        val keySize = buf.readShort()
        for (i in 0 until keySize){
            val key = buf.readIdentifier()
            val valueSize = buf.readShort()
            for (j in 0 until valueSize){
                val value = buf.readString()
                map.put(key,value)
            }
        }
    }

}