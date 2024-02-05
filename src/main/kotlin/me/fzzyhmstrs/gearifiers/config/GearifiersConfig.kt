package me.fzzyhmstrs.gearifiers.config


import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import me.fzzyhmstrs.fzzy_core.coding_util.SyncedConfigHelper
import me.fzzyhmstrs.fzzy_core.coding_util.SyncedConfigHelper.gson
import me.fzzyhmstrs.fzzy_core.coding_util.SyncedConfigHelper.readOrCreateUpdated
import me.fzzyhmstrs.fzzy_core.registry.SyncedConfigRegistry
import me.fzzyhmstrs.gearifiers.Gearifiers
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
object GearifiersConfig: SyncedConfigHelper.SyncedConfig{

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
    
    override fun initConfig(){
        SyncedConfigRegistry.registerConfig(Gearifiers.MOD_ID,this)
    }
    
    override fun writeToClient(buf:PacketByteBuf){
        val gson = GsonBuilder().create()
        buf.writeString(gson.toJson(modifiers))
        buf.writeString(gson.toJson(chances))
        buf.writeString(gson.toJson(blackList))
    }

    override fun readFromServer(buf:PacketByteBuf){
        modifiers = gson.fromJson(buf.readString(),Modifiers::class.java)
        chances = gson.fromJson(buf.readString(),Chances::class.java)
        blackList = gson.fromJson(buf.readString(),BlackList::class.java)
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

        fun fallbackItem(): Item{
            val fallbackId = Identifier(defaultRerollPaymentItem)
            return if(FzzyPort.ITEM.containsId(fallbackId)){
                FzzyPort.ITEM.get(fallbackId)
            } else {
                Items.DIAMOND
            }
        }

        fun isModifierEnabled(id: Identifier): Boolean{
            return enabledModifiers[id.toString()]?:false
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
        
        var enabledModifiers: Map<String,Boolean> = mapOf(
            "gearifiers:legendary" to true,
            "gearifiers:vorpal" to true,
            "gearifiers:demonic" to true,
            "gearifiers:demon_barbed" to true,
            "gearifiers:ungodly_sharp" to true,
            "gearifiers:razor_sharp" to true,
            "gearifiers:keen" to true,
            "gearifiers:honed" to true,
            "gearifiers:sharp" to true,
            "gearifiers:dull" to true,
            "gearifiers:blunt" to true,
            "gearifiers:useless" to true,
            "gearifiers:needlelike" to true,
            "gearifiers:barbed" to true,
            "gearifiers:acute" to true,
            "gearifiers:pointy" to true,
            "gearifiers:unpointy" to true,
            "gearifiers:rounded" to true,
            "gearifiers:indomitable" to true,
            "gearifiers:bulwark" to true,
            "gearifiers:protective" to true,
            "gearifiers:thick" to true,
            "gearifiers:thin" to true,
            "gearifiers:unsubstantial" to true,
            "gearifiers:everlasting" to true,
            "gearifiers:robust" to true,
            "gearifiers:durable" to true,
            "gearifiers:tattered" to true,
            "gearifiers:disrepaired" to true,
            "gearifiers:destroyed" to true,
            "gearifiers:manic" to true,
            "gearifiers:frenzied" to true,
            "gearifiers:energetic" to true,
            "gearifiers:clumsy" to true,
            "gearifiers:unwieldy" to true,
            "gearifiers:flourishing" to true,
            "gearifiers:hearty" to true,
            "gearifiers:healthy" to true,
            "gearifiers:infirm" to true,
            "gearifiers:wimpy" to true,
            "gearifiers:lucky" to true,
            "gearifiers:unlucky" to true,
            "gearifiers:racing" to true,
            "gearifiers:speedy" to true,
            "gearifiers:quick" to true,
            "gearifiers:slow" to true,
            "gearifiers:sluggish" to true,
            "gearifiers:greater_extension" to true,
            "gearifiers:extension" to true,
            "gearifiers:lesser_extension" to true,
            "gearifiers:lesser_stubby" to true,
            "gearifiers:stubby" to true,
            "gearifiers:grand_reach" to true,
            "gearifiers:greater_reach" to true,
            "gearifiers:reach" to true,
            "gearifiers:lesser_reach" to true,
            "gearifiers:lesser_limiting" to true,
            "gearifiers:limiting" to true,
            "gearifiers:blunting" to true,
            "gearifiers:dulling" to true,
            "gearifiers:harsh" to true,
            "gearifiers:reductive" to true,
            "gearifiers:additive" to true,
            "gearifiers:shielding" to true,
            "gearifiers:poisonous" to true,
            "gearifiers:toxic" to true,
            "gearifiers:desecrated" to true,
            "gearifiers:unholy" to true,
            "gearifiers:forceful" to true,
            "gearifiers:greater_thieving" to true,
            "gearifiers:thieving" to true,
            "gearifiers:lesser_thieving" to true,
            "gearifiers:splitting" to true,
            "gearifiers:enriched" to true,
            "gearifiers:metallic" to true,
            "gearifiers:anthracitic" to true,
            "gearifiers:explosive" to true,
            "gearifiers:hobbling" to true,
            "gearifiers:hungering" to true,
            "gearifiers:weakening" to true,
            "gearifiers:vampiric" to true,
            "gearifiers:double_edged" to true,
            "gearifiers:jarring" to true,
            "gearifiers:clanging" to true,
            "gearifiers:betraying" to true,
            "gearifiers:greater_crumbling" to true,
            "gearifiers:crumbling" to true
        )

        override fun generateNewClass(): Modifiers {
            return this
        }
    }
}
