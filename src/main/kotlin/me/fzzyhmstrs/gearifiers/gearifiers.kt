package me.fzzyhmstrs.gearifiers

import me.fzzyhmstrs.gearifiers.modifier.RegisterModifier
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory


object Gearifiers: ModInitializer {
    const val MOD_ID = "gearifiers"
    val LOGGER = LoggerFactory.getLogger("gearifiers")

    override fun onInitialize() {
        RegisterModifier.registerAll()
    }
}