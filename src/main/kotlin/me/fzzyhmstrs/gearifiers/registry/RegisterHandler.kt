package me.fzzyhmstrs.gearifiers.registry

import me.fzzyhmstrs.fzzy_core.coding_util.FzzyPort
import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.gearifiers.screen.RerollAltarScreenHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

object RegisterHandler {

    lateinit var REROLL_ALTAR_HANDLER: ScreenHandlerType<RerollAltarScreenHandler>

    fun registerAll(){
        REROLL_ALTAR_HANDLER = FzzyPort.buildHandlerType { syncID: Int, playerInventory: PlayerInventory ->
            RerollAltarScreenHandler(
                syncID,
                playerInventory
            )
        }

        FzzyPort.SCREEN_HANDLER.register(Identifier(Gearifiers.MOD_ID,"reroll_altar"), REROLL_ALTAR_HANDLER)
    }

}