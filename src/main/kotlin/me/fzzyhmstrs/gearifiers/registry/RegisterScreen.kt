package me.fzzyhmstrs.gearifiers.registry

import me.fzzyhmstrs.gearifiers.screen.RerollAltarScreen
import me.fzzyhmstrs.gearifiers.screen.RerollAltarScreenHandler
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

object RegisterScreen {

    fun registerAll(){
        HandledScreens.register(RegisterHandler.REROLL_ALTAR_HANDLER) {
                handler: RerollAltarScreenHandler, playerInventory: PlayerInventory, title: Text ->
            RerollAltarScreen(
                handler,
                playerInventory,
                title
            )
        }
    }

}