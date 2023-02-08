package me.fzzyhmstrs.gearifiers.registry

import me.fzzyhmstrs.gearifiers.screen.RerollAltarScreenHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerType

object RegisterHandler {

    lateinit var REROLL_ALTAR_HANDLER: ScreenHandlerType<RerollAltarScreenHandler>

    fun registerAll(){
        REROLL_ALTAR_HANDLER = ScreenHandlerType { syncID: Int, playerInventory: PlayerInventory ->
            RerollAltarScreenHandler(
                syncID,
                playerInventory
            )
        }
    }

}