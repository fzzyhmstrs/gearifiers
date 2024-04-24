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
object GearifiersConfigNew: Config(Identifier(Gearifiers.MOD_ID,"config")){

    var modifiers: Modifiers
    var chances: Chances
    var blackList: BlackList

    init{
        try {
            val (dir, dirCreated) = SyncedConfigHelper.makeDir("", Gearifiers.MOD_ID)
            if (dirCreated) {
                val f = File(dir, "modifiers_v1.json")
                if (f.exists()) {
                    val tempModifiers = JsonParser.parseString(f.readLines().joinToString(""))
                    if (tempModifiers is JsonObject) {
                        if (tempModifiers.has("fallbackId\$delegate")) {
                            tempModifiers.remove("fallbackId\$delegate")
                            f.writeText(gson.toJson(tempModifiers))
                        }
                    }
                }
            }
        } catch (e:Exception){
            e.printStackTrace()
        }

        modifiers = readOrCreateUpdated("modifiers_v4.json","modifiers_v3.json", base = Gearifiers.MOD_ID, configClass = { Modifiers() }, previousClass = { Modifiers() })
        chances = readOrCreateUpdated("chances_v2.json","chances_v1.json", base = Gearifiers.MOD_ID, configClass =  { Chances() },previousClass = {Chances()})
        blackList = readOrCreateUpdated("blackList_v1.json","blackList_v0.json",base = Gearifiers.MOD_ID, configClass =  { BlackList() }, previousClass = {BlackList()})
    }
    class BlackList: SyncedConfigHelper.OldClass<BlackList>{

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

        var namespaceBlackList: List<String> = listOf()
        var individualItemBlackList: List<String> = listOf()
        var blackListedScreenHandlers: List<String> = listOf()
        override fun generateNewClass(): BlackList {
            return this
        }

    }

    class Chances: SyncedConfigHelper.OldClass<Chances>{

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
        override fun generateNewClass(): Chances {
            this.shieldingChance = 0.0125f
            return this
        }
    }

    class Modifiers: SyncedConfigHelper.OldClass<Modifiers>{

        fun fallbackItem(): Item {
            val fallbackId = Identifier(defaultRerollPaymentItem)
            return if(FzzyPort.ITEM.containsId(fallbackId)){
                FzzyPort.ITEM.get(fallbackId)
            } else {
                Items.DIAMOND
            }
        }

        fun isModifierEnabled(id: Identifier): Boolean{
            return !disabledModifier.contains(id)
        }

        fun getItemCountNeeded(rerolls: Int): Int{
            return 1 + (rerolls * paymentItemCountIncreasePerLevel).toInt()
        }

        var enableRerollXpCost: Boolean = true
        var firstRerollXpCost: Int = 5
        var addedRerollXpCostPerRoll: Int = 2
        var useRepairIngredientAsRerollCost: Boolean = false
        var repairIngredientOverrideDefinedCosts: Boolean = false
        var defaultRerollPaymentItem: String = "minecraft:diamond"
        var paymentItemCountIncreasePerLevel: Double = 0.0
        var maxLegendarySealUses = 1

        var disabledModifier: List<Identifier> = listOf(
        )

        override fun generateNewClass(): Modifiers {
            return this
        }
    }
}