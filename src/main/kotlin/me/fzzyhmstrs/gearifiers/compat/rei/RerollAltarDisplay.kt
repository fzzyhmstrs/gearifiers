package me.fzzyhmstrs.gearifiers.compat.rei

import me.fzzyhmstrs.gearifiers.Gearifiers
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryIngredients
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.Item
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.*

class RerollAltarDisplay(inputs: MutableList<EntryIngredient>, outputs: MutableList<EntryIngredient>, location: Optional<Identifier>):
    BasicDisplay(inputs, outputs, location) {

    constructor(input: Item, cost: Set<Item>): this(
        getRecipeInputEntries(input,cost),
        getRecipeOutputEntries(input),
        getRecipeId(input,cost))
        
    override fun getInputEntries(): MutableList<EntryIngredient> {
        return inputs
    }

    override fun getOutputEntries(): MutableList<EntryIngredient> {
        return outputs
    }

    override fun getCategoryIdentifier(): CategoryIdentifier<RerollAltarDisplay> {
        return CategoryIdentifier.of(Identifier(Gearifiers.MOD_ID, "rerolling"))
    }

    companion object{

        private fun getRecipeInputEntries(input: Item, cost: Set<Item>): MutableList<EntryIngredient>{
            val builder = EntryIngredient.builder()
            cost.forEach { item -> builder.add(EntryStacks.of(item)) }
            return mutableListOf(EntryIngredients.of(input),builder.build())
        }

        private fun getRecipeOutputEntries(input: Item): MutableList<EntryIngredient>{
            return mutableListOf(EntryIngredients.of(input))
        }
        
        private fun getRecipeId(input: Item, cost: Set<Item>): Optional<Identifier>{
            val itemId1 = Registry.ITEM.getId(input)
            var itemId2 = ""
            cost.forEach {
                itemId2 += "/"
                itemId2 += Registry.ITEM.getId(it).namespace + "." + Registry.ITEM.getId(it).path
            }


            return Optional.ofNullable(Identifier(Gearifiers.MOD_ID,itemId1.namespace + "." + itemId1.path + "/paid_with/" + itemId2))
        }
    }
}
