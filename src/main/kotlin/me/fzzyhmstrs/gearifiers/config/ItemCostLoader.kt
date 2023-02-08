package me.fzzyhmstrs.gearifiers.config

import com.google.common.collect.HashMultimap
import com.google.gson.JsonParser
import me.fzzyhmstrs.gearifiers.Gearifiers
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.command.argument.BlockArgumentParser
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

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
                if (!Registry.ITEM.containsId(paymentName)){
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
        }
    }

    internal fun itemCostMatches(item: Item, payment: Item): Boolean{
        if (ITEM_COSTS.isEmpty()){
            processItemCostsMap()
        }
        val list = ITEM_COSTS.get(item)
        return if (list.isEmpty()){
            payment == GearifiersConfig.fallbackCost
        } else {
            list.contains(payment)
        }
    }
    
    internal fun getItemCosts(item: Item){
        return ITEM_COSTS.get(item)
    }

    private fun processItemCostsMap(){
        for (entry in rawItemCosts.entries()){
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
    }
    
    fun writeRawDataToClient(buf:PacketByteBuf){
        buf.writeString(Gson().toJson(rawItemCosts))
        buf.writeString(Gson().toJson(rawOverrideCosts))
    }
    
    fun readRawDataFromServer(buf: PacketByteBuf){
        ITEM_COSTS.clear()
        rawItemCosts.clear()
        rawOverrideCosts.clear()
        val data1 = Gson().fromJson(buf.readString(),HashMultiMap::class.java)
        rawItemCosts.putAll(data1)
        val data2 = Gson().fromJson(buf.readString(),HashMultiMap::class.java)
        rawOverrideCosts.putAll(data2)
    }

    override fun reload(manager: ResourceManager) {
        loadItemCosts(manager)
    }

    override fun getFabricId(): Identifier {
        return Identifier(Gearifiers.MOD_ID,"reroll_cost_loader")
    }

}
