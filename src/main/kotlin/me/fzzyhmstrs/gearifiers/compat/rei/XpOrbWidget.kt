package me.fzzyhmstrs.gearifiers.compat.rei

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.gearifiers.screen.RerollAltarScreen
import me.shedaniel.math.Dimension
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.min

class XpOrbWidget(private val x: Int, private val y: Int, private val cost: Int): WidgetWithBounds() {

    private val bounds = Rectangle(Point(x,y), Dimension(16,16))
    private val offset = determineOffset(cost)
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
    override fun children(): MutableList<out Element> {
        return mutableListOf()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        context.drawTexture(
            RerollAltarScreen.TEXTURE,
            x,
            y,
            offset.toFloat(),
            174f,
            16,
            16,
            256,
            256
        )


        if (cost < 7) return

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        context.drawTexture(
            RerollAltarScreen.TEXTURE,
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
            RerollAltarScreen.TEXTURE,
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

    override fun getBounds(): Rectangle {
        return bounds
    }

    companion object{
        private fun determineOffset(cost: Int): Int{
            return if(cost > 0) {(108 + 16 * min(6, cost - 1))} else { 108 + (16 * 7)}
        }
    }
}