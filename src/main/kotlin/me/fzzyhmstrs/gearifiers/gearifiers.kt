package me.fzzyhmstrs.gearifiers

import me.fzzyhmstrs.gearifiers.modifier.RegisterModifier
import net.fabricmc.api.ClientModInitializer


object Gearifiers: ClientModInitializer {
    const val MOD_ID = "gearifiers"

    override fun onInitializeClient() {
        RegisterModifier.registerAll()
    }
}