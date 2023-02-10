package me.fzzyhmstrs.gearifiers.registry

import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.gearifiers.screen.RerollAltarScreenHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RegisterHandler {

    lateinit var REROLL_ALTAR_HANDLER: ScreenHandlerType<RerollAltarScreenHandler>

    fun registerAll(){
        REROLL_ALTAR_HANDLER = ScreenHandlerType { syncID: Int, playerInventory: PlayerInventory ->
            RerollAltarScreenHandler(
                syncID,
                playerInventory
            )
        }

        Registry.register(Registry.SCREEN_HANDLER, Identifier(Gearifiers.MOD_ID,"reroll_altar"), REROLL_ALTAR_HANDLER)
    }

}