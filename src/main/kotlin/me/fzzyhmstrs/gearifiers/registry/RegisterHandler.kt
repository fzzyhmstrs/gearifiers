package me.fzzyhmstrs.gearifiers.registry

import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.gearifiers.screen.RerollAltarScreenHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

object RegisterHandler {

    lateinit var REROLL_ALTAR_HANDLER: ScreenHandlerType<RerollAltarScreenHandler>

    fun registerAll(){
        REROLL_ALTAR_HANDLER = ScreenHandlerType { syncID: Int, playerInventory: PlayerInventory ->
            RerollAltarScreenHandler(
                syncID,
                playerInventory
            )
        }

        Registry.register(Registries.SCREEN_HANDLER, Identifier(Gearifiers.MOD_ID,"reroll_altar"), REROLL_ALTAR_HANDLER)
    }

}