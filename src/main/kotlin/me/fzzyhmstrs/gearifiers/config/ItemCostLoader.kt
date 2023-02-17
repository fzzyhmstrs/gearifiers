package me.fzzyhmstrs.gearifiers.config

import com.google.common.collect.HashMultimap
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import me.fzzyhmstrs.gearifiers.Gearifiers
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.command.argument.BlockArgumentParser
import net.minecraft.item.ArmorItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.item.ToolItem
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import java.util.function.Predicate

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
                if (!Registries.ITEM.containsId(paymentName)){
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
        return if (list.isEmpty()){
            if (GearifiersConfig.modifiers.useRepairIngredientAsRerollCost){
                val list2 = getRepairIngredient(item)
                if (list2.isEmpty()){
                    payment == GearifiersConfig.fallbackCost
                } else {
                    list2.contains(payment)
                }
            } else {
                payment == GearifiersConfig.fallbackCost
            }
        } else {
            if (GearifiersConfig.modifiers.useRepairIngredientAsRerollCost && GearifiersConfig.modifiers.repairIngredientOverrideDefinedCosts){
                val list3 = getRepairIngredient(item)
                if (list3.isEmpty()){
                    list.contains(payment)
                } else {
                    list3.contains(payment)
                }
            } else {
                list.contains(payment)
            }
        }
    }
    
    private fun getRepairIngredient(item: Item): List<Item>{
        if (item is ArmorItem){
            return item.material.repairIngredient.matchingItemIds.stream().map { id -> Registries.ITEM.get(id) }.toList()
        }
        if (item is ToolItem){
            return item.material.repairIngredient.matchingItemIds.stream().map { id -> Registries.ITEM.get(id) }.toList()
        }
        return listOf()
    }

    private fun processItemCostsMap(){
        //println(rawItemCosts)
        for (entry in rawItemCosts.entries()){
            val costItem = Registries.ITEM.get(entry.key)
            //println("cost item: $costItem")
            val targetItemString = entry.value
            if (targetItemString.startsWith('#') && targetItemString.length > 1){
                val tagId = Identifier.tryParse(targetItemString.substring(1))
                if (tagId != null){
                    val tagKey = TagKey.of(RegistryKeys.ITEM,tagId)
                    val entries = Registries.ITEM.getEntryList(tagKey)
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
                    if (Registries.ITEM.containsId(itemId)){
                        ITEM_COSTS.put(Registries.ITEM.get(itemId),costItem)
                    } else {
                        Gearifiers.LOGGER.warn("Item id $itemId referenced from reroll cost ${entry.key} couldn't be found in the Item Registry!")
                    }
                } else {
                    Gearifiers.LOGGER.warn("Item id $itemId referenced from reroll cost ${entry.key} couldn't be found in the Item Registry!")
                }
            }
        }
        for (entry in rawOverrideCosts.entries()){
            val costItem = Registries.ITEM.get(entry.key)
            val targetItemString = entry.value
            if (targetItemString.startsWith('#') && targetItemString.length > 1){
                val tagId = Identifier.tryParse(targetItemString.substring(1))
                if (tagId != null){
                    val tagKey = TagKey.of(RegistryKeys.ITEM,tagId)
                    val entries = Registries.ITEM.getEntryList(tagKey)
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
                    if (Registries.ITEM.containsId(itemId)){
                        ITEM_COSTS.removeAll(Registries.ITEM.get(itemId))
                        ITEM_COSTS.put(Registries.ITEM.get(itemId),costItem)
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
