package me.fzzyhmstrs.gearifiers.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import net.minecraft.client.gui.screen.ingame.ForgingScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.Identifier

class RerollAltarScreen(handler: RerollAltarScreenHandler,playerInventory: PlayerInventory, title: net.minecraft.text.Text):
    ForgingScreen<RerollAltarScreenHandler>(handler,playerInventory,title, TEXTURE) {

    init {
        titleX = 60
        titleY = 14
    }

    override fun drawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        RenderSystem.disableBlend()
        super.drawForeground(matrices, mouseX, mouseY)
    }
    
    override fun drawBackground(matrices: MatrixStack,delta: Float, mouseX: Int, mouseY: Int){
        super.drawBackground(matrices,delta,mouseX,mouseY)
        if (!GearifiersConfig.modifiers.enableRerollXpCost || handler.enchants.get() == 0) return
        val i = (width - backgrdWidth) / 2
        val j = (height - backgrdHeight) / 2
        RenderSystem.restoreProjectionMatrix()
        RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, TEXTURE)
        
        var tens: Int
        var ones: Int
        var tensOfst = 1
        var onesOfst = 8
        var tensImageOfst: Int
        var onesImageOfst = 9
        
        val power = handler.enchants.get()
        val hOffset = max(0,power - 1)
        val vOffset: Int
        val numeralOffset: Int
        if(power < 0){
            vOffset = 190
            numeralOffset = 222
        } else {
            vOffset = 174
            numeralOffset = 212
        }
        if (hOffset <= 5){
            this.drawTexture(
                    matrices,
                    i + 106,
                    j + 29,
                    108 + 16 * (hOffset),
                    vOffset,
                    16,
                    16
                )
        } else{
            this.drawTexture(
                    matrices,
                    i + 106,
                    j + 29,
                    108 + 16 * 6,
                    174 + 16,
                    16,
                    16
                )
            if (power > 9){
                tens = power/10
                ones = power - tens * 10
                if(tens > 9){
                    tens = 9
                    ones = 9
                }
            } else{
                tens = 0
                ones = power
            }
            if (tens == 1){tensOfst += 1}
            if (ones == 1){tensOfst += 2}
            tensImageOfst = tens - 1
            if(ones == 1) {onesOfst = 9}
            if (ones != 0) {
                onesImageOfst = ones - 1
            }
            //draw the ones place numeral
                this.drawTexture(
                    matrices,
                    i + 106 + onesOfst,
                    j + 29 + 3, //three additional offset to align the number with the usual position
                    108 + 9 * onesImageOfst, //grab the image off the texture, using the 10 abstract numerals
                    numeralOffset,
                    9,
                    9
                )
                if (tens>0) this.drawTexture(
                    matrices,
                    i+ 106 + tensOfst,
                    j + 29 + 3, //three additional offset to align the number with the usual position
                    108 + 9 * tensImageOfst, //grab the image off the texture, using the 10 abstract numerals
                    numeralOffset,
                    9,
                    9
                )
        }
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        drawMouseoverTooltip(matrices, mouseX, mouseY)
        val i = (width - backgrdWidth) / 2
        val j = (height - backgrdHeight) / 2
        val h = mouseX - i - 106
        val v = mouseY - j - 29
        val hovered = (u >= 0 && v >= 0 && u < 16 && v < 16)
        if (hovered){
            val list = if (handler.enchants.get() > 0){
                listOf(AcText.translatable("screen.gearifiers.xp_clue_good"))
            } else if (handler.enchants.get() < 0){
                listOf(AcText.translatable("screen.gearifiers.xp_clue_bad").formatted(Formatting.RED))
            } else {
                listOf()
            }
            this.renderTooltip(matrices, list, mouseX, mouseY)
        }
    }
    
    companion object{
        private val TEXTURE = Identifier(Gearifiers.MOD_ID,"textures/reroll_altar_gui.png")
    }
}
