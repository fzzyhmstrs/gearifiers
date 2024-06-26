package me.fzzyhmstrs.gearifiers.config

import com.google.common.collect.HashMultimap
import com.google.gson.JsonParser
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import me.fzzyhmstrs.gearifiers.Gearifiers
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.item.Item
import net.minecraft.network.PacketByteBuf
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier

object ItemCostLoader: SimpleSynchronousResourceReloadListener {

    /*
    Vanilla MC equipment to consider:
    Lapis:
    leather armor
    gold armor
    wood tools
    stone tools

    Emerald:
    chain armor
    iron armor
    iron tools
    gold tools
    bow
    crossbow

    Diamond:
    diamond armor
    turtle shell
    trident
    diamond tools

    Netherite Scrap:
    netherite armor
    netherite tools
    */

    private val rawItemCosts: HashMultimap<Identifier,String> = HashMultimap.create()
    private val rawOverrideCosts: HashMultimap<Identifier,String> = HashMultimap.create()
    internal val ITEM_COSTS: HashMultimap<Item,Item> = HashMultimap.create()
    private val BLANK = Identifier("blank")

    fun loadItemCosts(resourceManager: ResourceManager){
        rawItemCosts.clear()
        rawOverrideCosts.clear()
        ITEM_COSTS.clear()
        resourceManager.findResources("reroll_costs") { path -> path.path.endsWith(".json") }
            .forEach { (t, u) ->
                loadItemCost(t,u)
        }
        /*println("Loaded Gearifiers reroll costs:")
        for (entry in rawItemCosts.entries()){
            println(entry)
        }*/
        //println("other maps")
        //println(rawOverrideCosts)
        //println(ITEM_COSTS)
    }

    private fun loadItemCost(id: Identifier,resource: Resource){
        try{
            val reader = resource.reader
            val json = JsonParser.parseReader(reader).asJsonObject
            val jsonOverride = json.get("override")
            val override = if (jsonOverride == null){
                false
            } else if (!jsonOverride.isJsonPrimitive){
                false
            } else {
                jsonOverride.asBoolean
            }
            for (el in json.entrySet()){
                if (el.key == "override") continue
                if (!el.value.isJsonArray){
                    Gearifiers.LOGGER.warn("Json object [${el.key}] not properly formatted, needs to be a JsonArray, skipping!")
                    continue
                }
                val paymentName = Identifier.tryParse(el.key) ?: BLANK
                if (paymentName == BLANK){
                    Gearifiers.LOGGER.warn("Couldn't parse payment item id [${el.key}], skipping!")
                    continue
                }
                if (!FzzyPort.ITEM.containsId(paymentName)){
                    Gearifiers.LOGGER.warn("Item id [${el.key}] not found in the Item Registry, skipping!")
                    continue
                }
                val jsonItems = el.value.asJsonArray
                val items: MutableList<String> = mutableListOf()
                for (el2 in jsonItems){
                    if (!el2.isJsonPrimitive){
                        Gearifiers.LOGGER.warn("Json object [$el2] at [${el.key}] not properly formatted, needs to be a JsonPrimitive, skipping!")
                        continue
                    }
                    items.add(el2.asString)
                }
                if (!override){
                    rawItemCosts.putAll(paymentName,items)
                } else {
                    rawOverrideCosts.putAll(paymentName,items)
                }
            }
        } catch (e: Exception){
            Gearifiers.LOGGER.error("failed to open or read item cost file: $id")
            e.printStackTrace()
        }
    }

    internal fun itemCostMatches(item: Item, payment: Item): Boolean{
        if (ITEM_COSTS.isEmpty){
            //println("preparing map...")
            processItemCostsMap()
        }
        val list = ITEM_COSTS.get(item)
        return GearifiersConfig.getInstance().modifiers.getRepairIngredients(item,list.toSet()).contains(payment)
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

    fun writeRawDataToClient(buf: PacketByteBuf){
        //println(">>>>>>>>>>>>> writing to client <<<<<<<<<<<<<<<<")
        //println(rawItemCosts)
        writeMultimapToBuf(buf, rawItemCosts)
        writeMultimapToBuf(buf, rawOverrideCosts)
    }

    private fun writeMultimapToBuf(buf: PacketByteBuf, map: HashMultimap<Identifier,String>){
        buf.writeShort(map.keySet().size)
        for (key in map.keySet()){
            buf.writeIdentifier(key)
            val values = map[key]
            buf.writeShort(values.size)
            for (value in values){
                buf.writeString(value)
            }
        }
    }

    override fun reload(manager: ResourceManager) {
        loadItemCosts(manager)
    }

    override fun getFabricId(): Identifier {
        return Identifier(Gearifiers.MOD_ID,"reroll_cost_loader")
    }

}