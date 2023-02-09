package me.fzzyhmstrs.gearifiers.compat

import com.google.common.collect.HashMultimap
import com.google.gson.Gson
import me.fzzyhmstrs.gearifiers.Gearifiers
import net.minecraft.item.Item
import net.minecraft.network.PacketByteBuf
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object ClientItemCostLoader {

    private val rawItemCosts: HashMultimap<Identifier,String> = HashMultimap.create()
    private val rawOverrideCosts: HashMultimap<Identifier,String> = HashMultimap.create()
    internal val ITEM_COSTS: HashMultimap<Item, Item> = HashMultimap.create()


    internal fun getItemCosts(item: Item): Set<Item>{
        if (ITEM_COSTS.isEmpty){
            processItemCostsMap()
        }
        return ITEM_COSTS.get(item)
    }

    private fun processItemCostsMap(){
        println(rawItemCosts)
        for (entry in rawItemCosts.entries()){
            val costItem = Registry.ITEM.get(entry.key)
            println("cost item: $costItem")
            val targetItemString = entry.value
            if (targetItemString.startsWith('#') && targetItemString.length > 1){
                val tagId = Identifier.tryParse(targetItemString.substring(1))
                if (tagId != null){
                    val tagKey = TagKey.of(Registry.ITEM_KEY,tagId)
                    val entries = Registry.ITEM.getEntryList(tagKey)
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
                println("parsing targetItemString $targetItemString into identifier $itemId")
                if (itemId != null){
                    if (Registry.ITEM.containsId(itemId)){
                        ITEM_COSTS.put(Registry.ITEM.get(itemId),costItem)
                    } else {
                        Gearifiers.LOGGER.warn("Item id $itemId referenced from reroll cost ${entry.key} couldn't be found in the Item Registry!")
                    }
                } else {
                    Gearifiers.LOGGER.warn("Item id $itemId referenced from reroll cost ${entry.key} couldn't be found in the Item Registry!")
                }
            }
        }
        for (entry in rawOverrideCosts.entries()){
            val costItem = Registry.ITEM.get(entry.key)
            val targetItemString = entry.value
            if (targetItemString.startsWith('#') && targetItemString.length > 1){
                val tagId = Identifier.tryParse(targetItemString.substring(1))
                if (tagId != null){
                    val tagKey = TagKey.of(Registry.ITEM_KEY,tagId)
                    val entries = Registry.ITEM.getEntryList(tagKey)
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
                    if (Registry.ITEM.containsId(itemId)){
                        ITEM_COSTS.removeAll(Registry.ITEM.get(itemId))
                        ITEM_COSTS.put(Registry.ITEM.get(itemId),costItem)
                    } else {
                        Gearifiers.LOGGER.warn("Item id $itemId referenced from reroll cost ${entry.key} couldn't be found in the Item Registry!")
                    }
                } else {
                    Gearifiers.LOGGER.warn("Item id $itemId referenced from reroll cost ${entry.key} couldn't be found in the Item Registry!")
                }
            }
        }
        println("prepared map:")
        println(ITEM_COSTS)
    }

    fun readRawDataFromServer(buf: PacketByteBuf){
        ITEM_COSTS.clear()
        rawItemCosts.clear()
        rawOverrideCosts.clear()
        println(">>>>>>>>>>>>> reading from server <<<<<<<<<<<<<<<<")
        readMultimapFromBuf(buf, rawItemCosts)
        readMultimapFromBuf(buf, rawOverrideCosts)
        println(rawItemCosts)
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