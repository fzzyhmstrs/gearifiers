package me.fzzyhmstrs.gearifiers.config

import com.google.gson.GsonBuilder
import me.fzzyhmstrs.fzzy_core.coding_util.SyncedConfigHelper
import me.fzzyhmstrs.fzzy_core.coding_util.SyncedConfigHelper.gson
import me.fzzyhmstrs.fzzy_core.coding_util.SyncedConfigHelper.readOrCreate
import me.fzzyhmstrs.fzzy_core.registry.SyncedConfigRegistry
import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.gearifiers.registry.RegisterModifier
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.Registry

object GearifersConfig: SyncedConfigHelper.SyncedConfig{

    var modifiers: Modifiers
    val fallbackCost: Item
    
    init{
        modifiers = readOrCreate("modifiers_v0.json", base = Gearifiers.MOD_ID) { Modifiers() }
        val fallbackId = Identifier(modifiers.defaultRerollPaymentItem)
        fallbackCost = if(Registry.ITEM.containsId(fallbackId)){
            Registry.ITEM.get(fallbackId)
        } else {
            Items.DIAMOND
        }
    }
    
    override fun initConfig(){
        SyncedConfigRegistry.registerConfig(Gearifiers.MOD_ID,this)
    }
    
    override fun writeToClient(buf:PacketByteBuf){
        val gson = GsonBuilder().create()
        buf.writeString(gson.toJson(items))
    }

    override fun readFromServer(buf:PacketByteBuf){
        items = gson.fromJson(buf.readString(),Items::class.java)
    }
    
    class Modifiers{
        
        fun isModifierEnabled(id: Identifier): Boolean{
            return enabledModifiers[id.toString()]?:false
        }
    
        var enableRerollXpCost: Boolean = true
        var firstRerollXpCost: Int = 1
        var addedRerollXpCostPerRoll: Int = 1
        var defaultRerollPaymentItem: String = "minecraft:diamond"
        
        var enabledModifiers: Map<String,Boolean> = mapOf(
            "gearifiers:legendary" to true,
            "gearifiers:vorpal" to true,
            "gearifiers:demonic" to true,
            "gearifiers:ungodly_sharp" to true,
            "gearifiers:razor_sharp" to true,
            "gearifiers:keen" to true,
            "gearifiers:honed" to true,
            "gearifiers:sharp" to true,
            "gearifiers:dull" to true,
            "gearifiers:blunt" to true,
            "gearifiers:useless" to true,
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
            "gearifiers:double_edged" to true,
            "gearifiers:jarring" to true,
            "gearifiers:clanging" to true,
            "gearifiers:greater_crumbling" to true,
            "gearifiers:crumbling" to true
        )
    }
    
}
