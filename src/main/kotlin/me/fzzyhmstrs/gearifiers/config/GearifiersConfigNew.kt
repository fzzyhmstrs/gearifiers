package me.fzzyhmstrs.gearifiers.config


import com.google.gson.JsonObject
import com.google.gson.JsonParser
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import me.fzzyhmstrs.fzzy_core.coding_util.SyncedConfigHelper
import me.fzzyhmstrs.fzzy_core.coding_util.SyncedConfigHelper.gson
import me.fzzyhmstrs.fzzy_core.coding_util.SyncedConfigHelper.readOrCreateUpdated
import me.fzzyhmstrs.gearifiers.Gearifiers
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
class GearifiersConfigNew: Config(Identifier(Gearifiers.MOD_ID,"config")){

    companion object{
        private val INSTANCE = ConfigApi.registerAndLoadConfig({ GearifiersConfigNew() })

        @JvmStatic
        fun getInstance(): GearifiersConfigNew{
            return INSTANCE
        }
    }

    fun isItemBlackListed(stack: ItemStack): Boolean{
        val item = stack.item
        val id = FzzyPort.ITEM.getId(item)
        if (namespaceBlackList.contains(id.namespace)) return true
        return individualItemBlackList.contains(id.toString())
    }

    fun isItemBlackListed(item: Item): Boolean{
        val id = FzzyPort.ITEM.getId(item)
        if (namespaceBlackList.contains(id.namespace)) return true
        return individualItemBlackList.contains(id.toString())
    }

    fun isScreenHandlerBlackListed(playerEntity: PlayerEntity): Boolean{
        val handler = playerEntity.currentScreenHandler
        return try {
            blackListedScreenHandlers.contains(FzzyPort.SCREEN_HANDLER.getId(handler.type)?.toString()?:"")
        } catch (e: Exception){
            false
        }

    }

    var namespaceBlackList:ValidatedList<String> = ValidatedString.fromList(FabricLoader.getInstance().getAllMods().map{ it.getMetadata().getId() }).toList()
    var itemBlackList: ValidatedList<Identifier> = ValidatedIdentifier.ofRegistry(Registries.ITEM).toList()
    var blackListedScreenHandlers: = ValidatedIdentifier.fromRegistry(Registries.SCREEN_HANDLER).toList()

    var chances = Chances()
    
    class Chances: ConfigSection(){

        var vorpalChance: Float = 0.025f
        var demonicChance: Float = 0.3333333f
        var manicChance: Float = 0.25f
        var jarringChance: Float = 0.2f
        var clangingChance: Float = 0.2f
        var doubleEdgedChance: Float = 0.25f
        var splittingChance: Float = 0.1f
        var anthraciticChance: Float = 0.04f
        var metallicChance: Float = 0.04f
        var enrichedChance: Float = 0.03f
        var indomitableChance: Float = 0.15f
        var shieldingChance: Float = 0.0125f
        var commonLoot: Float = 0.10f
        var uncommonLoot: Float = 0.06f
        var rareLoot: Float = 0.04f
        var epicLoot: Float = 0.02f
    }

    var modifiers = Modifiers()
    
    class Modifiers: ConfigSection() {

        fun fallbackItem(): Item {
            return if(FzzyPort.ITEM.containsId(defaultPaymentItem)){
                FzzyPort.ITEM.get(defaultPaymentItem)
            } else {
                Items.DIAMOND
            }
        }

        fun isModifierEnabled(id: Identifier): Boolean {
            return !disabledModifiers.contains(id)
        }

        fun getItemCountNeeded(rerolls: Int): Int {
            return 1 + (rerolls * paymentItemScaling).toInt()
        }

        fun getRerollCost(rerolls: Int) {
            if (!rerollCosts.enabled) return 0
            return rerollCosts.firstRerollCost + (rerollCosts.addedPerReroll * rerolls)
        }

        fun getRepairIngredients(item: Item, set: Set<Item>): Set<Item> {
            return when(useRepairIngredient){
                useRepairIngredient.NO -> if(set.isEmpty()) setOf(fallbackItem()) else set
                useRepairIngredient.YES -> if(set.isEmpty()) getRepairIngredient(item) else set
                useRepairIngredient.ALWAYS -> getRepairIngredient(item)
            }
        }
        private fun getRepairIngredient(item: Item): Set<Item> {
            if (item is ArmorItem) {
                return item.material.repairIngredient.matchingItemIds.map { id -> FzzyPort.ITEM.get(id) }.toSet()
            }
            if (item is ToolItem){
                return item.material.repairIngredient.matchingItemIds.map { id -> FzzyPort.ITEM.get(id) }.toSet()
            }
            return setOf(fallbackItem())
        }

        fun getApplicableModifiers(stack: ItemStack, predicate: Predicate<EquipmentModifier> = { _ -> true }): List<EquipmentModifier> {
            return EquipmentModifierHelper.getTargetsForItem(stack).filter { predicate.test(it) }
        }

        var rerollCosts = RerollCosts()

        class RerollCosts: Walkable {
            var enabled: Boolean = true
            @ValidatedInt.Restrict(0,50)
            var firstRerollCost: Int = 5
            @ValidatedInt.Restrict(0,50)
            var addedPerReroll: Int = 2
        }
        
        
        var useRepairIngredient: RepairIngredientUsage = RepairIngredientUsage.NO
        var defaultPaymentItem: ValidatedIdentifier = ValidatedIdentifier.ofRegistry(Identifier("diamond"),Registries.ITEM)
        @ValidatedDouble.Restrict(0.0,4.0)
        var paymentItemScaling: Double = 0.0
        @ValidatedInt.Restrict(1,Int.MAX_VALUE)
        var maxLegendarySealUses = 1
        var disabledModifiers: ValidatedList<Identifier> = ValidatedIdentifier.ofSuppliedList { ModifierRegistry.getAllByType<EquimentModifier>().filter{ it.id.namespace == Gearifiers.MOD_ID } }.toList()
    }

    enum class RepairIngredientUsage: EnumTranslatable {
        NO,
        YES,
        ALWAYS;

        override fun prefix(): String{
            return "${Gearifiers.MOD_ID}.config.repair_ingredient_usage"
        }
    }
}
