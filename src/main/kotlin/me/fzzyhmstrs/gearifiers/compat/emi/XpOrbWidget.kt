package me.fzzyhmstrs.gearifiers.compat.emi

import com.mojang.blaze3d.systems.RenderSystem
import dev.emi.emi.api.widget.TextureWidget
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.gearifiers.screen.RerollAltarScreen
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.min

class XpOrbWidget(x: Int, y: Int, private val cost: Int, tooltipKey: String): TextureWidget(RerollAltarScreen.TEXTURE,x,y,16,16,determineOffset(cost),174 ) {

    private val tooltipComponent = mutableListOf(TooltipComponent.of(AcText.translatable(tooltipKey).asOrderedText()))
    private var tens: Int
    private var ones: Int

    private var tensOfst = 1
    private var onesOfst = 8
    private var onesImageOfst = 9
    private val tensImageOfst: Int

    init{
        if (cost > 9){
            tens = cost/10
            ones = cost - tens * 10
            if(tens > 9){
                tens = 9
                ones = 9
            }
        } else{
            tens = 0
            ones = cost
        }

        if (tens == 1){tensOfst += 1}
        if (ones == 1){tensOfst += 2}
        tensImageOfst = tens - 1
        if(ones == 1) {onesOfst = 9}
        if (ones != 0) {
            onesImageOfst = ones - 1
        }
    }

    override fun getTooltip(mouseX: Int, mouseY: Int): MutableList<TooltipComponent> {
        return tooltipComponent
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        if (cost < 7) return

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        context.drawTexture(
            texture,
            x + onesOfst,
            y + 3,
            (108f+9*onesImageOfst),
            212f,
            9,
            9,
            256,
            256
        )

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        context.drawTexture(
            texture,
            x + tensOfst,
            y + 3,
            (108f+9*tensImageOfst),
            212f,
            9,
            9,
            256,
            256
        )
    }

    companion object{
        private fun determineOffset(cost: Int): Int{
            return if(cost > 0) {(108 + 16 * min(6, cost - 1))} else { 108 + (16 * 7)}
        }
    }

}