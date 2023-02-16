package me.fzzyhmstrs.gearifiers.compat.rei

import me.fzzyhmstrs.gearifiers.Gearifiers
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryIngredients
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.Item
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.util.*

class RerollAltarDisplay(inputs: MutableList<EntryIngredient>, outputs: MutableList<EntryIngredient>, location: Optional<Identifier>):
    BasicDisplay(inputs, outputs, location) {

    constructor(input: Item, cost: Item): this(
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

        private fun getRecipeInputEntries(input: Item, cost: Item): MutableList<EntryIngredient>{
            return mutableListOf(EntryIngredients.of(input),EntryIngredients.of(cost))
        }

        private fun getRecipeOutputEntries(input: Item): MutableList<EntryIngredient>{
            return mutableListOf(EntryIngredients.of(input))
        }
        
        private fun getRecipeId(input: Item, cost: Item): Optional<Identifier>{
            val itemId1 = Registries.ITEM.getId(input)
            val itemId2 = Registries.ITEM.getId(cost)
            return Optional.ofNullable(Identifier(Gearifiers.MOD_ID,itemId1.namespace + "." + itemId1.path + "/paid_with/" + itemId2.namespace + "." + itemId2.path))  
        }
    }
}
