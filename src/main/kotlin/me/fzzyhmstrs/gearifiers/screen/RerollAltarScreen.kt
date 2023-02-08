package me.fzzyhmstrs.gearifiers.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.gearifiers.Gearifiers
import net.minecraft.client.gui.screen.ingame.ForgingScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.Identifier

class RerollAltarScreen(handler: RerollAltarScreenHandler,playerInventory: PlayerInventory, title: net.minecraft.text.Text):
    ForgingScreen<RerollAltarScreenHandler>(handler,playerInventory,title, TEXTURE) {

    init {
        titleX = 60
        titleY = 18
    }

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
        RenderSystem.disableBlend()
        super.drawForeground(matrices, mouseX, mouseY)
    }

    companion object{
        private val TEXTURE = Identifier(Gearifiers.MOD_ID,"textures/reroll_altar_gui.png")
    }
}