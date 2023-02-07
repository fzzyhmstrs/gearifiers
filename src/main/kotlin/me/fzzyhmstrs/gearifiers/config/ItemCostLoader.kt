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

    private val rawItemCosts: HashMultimap<Identifier,String> = HashMultimap.create()
    internal val ITEM_COSTS: MutableMap<Item,Item> = mutableMapOf()
    private val BLANK = Identifier("blank")

    fun loadItemCosts(resourceManager: ResourceManager){
        rawItemCosts.clear()
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
            for (el in json.entrySet()){
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
                rawItemCosts.putAll(paymentName,items)
            }
        } catch (e: Exception){
            Gearifiers.LOGGER.error("failed to open or read item cost file: $id")
        }
    }

    internal fun getItemCost(item:Item): Item{
        if (ITEM_COSTS.isEmpty()){
            processItemCostsMap()
        }
        return ITEM_COSTS.getOrDefault(item, Items.DIAMOND)
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
                            ITEM_COSTS[it.value()] = costItem
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
                        ITEM_COSTS[Registry.ITEM.get(itemId)] = costItem
                    } else {
                        Gearifiers.LOGGER.warn("Item id $itemId referenced from reroll cost ${entry.key} couldn't be found in the Item Registry!")
                    }
                } else {
                    Gearifiers.LOGGER.warn("Item id $itemId referenced from reroll cost ${entry.key} couldn't be found in the Item Registry!")
                }
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