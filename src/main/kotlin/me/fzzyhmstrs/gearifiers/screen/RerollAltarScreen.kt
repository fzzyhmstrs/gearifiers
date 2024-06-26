package me.fzzyhmstrs.gearifiers.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.gearifiers.compat.ClientItemCostLoader
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.GameRenderer
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import kotlin.math.abs
import kotlin.math.max

class RerollAltarScreen(handler: RerollAltarScreenHandler,playerInventory: PlayerInventory, title: net.minecraft.text.Text):
    HandledScreen<RerollAltarScreenHandler>(handler,playerInventory,title) {

    init {
        titleX = 60
        titleY = 14
    }

    override fun drawForeground(context: DrawContext, mouseX: Int, mouseY: Int) {
        RenderSystem.disableBlend()
        super.drawForeground(context, mouseX, mouseY)
    }

    override fun drawBackground(context: DrawContext,delta: Float, mouseX: Int, mouseY: Int){
        RenderSystem.setShaderTexture(0, TEXTURE)
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight)
        drawInvalidRecipeArrow(context, x, y)
        if (!GearifiersConfig.getInstance().modifiers.rerollCosts.enabled || handler.enchants.get() == 0) return
        val i = (width - backgroundWidth) / 2
        val j = (height - backgroundHeight) / 2

        val item = handler.getRerollItem()
        val payments = ClientItemCostLoader.getItemCosts(item.item)
        if (payments.isNotEmpty() && !GearifiersConfig.getInstance().isItemBlackListed(item)){
            val payment = payments.elementAt(0)
            RenderSystem.disableDepthTest()
            val stack = ItemStack(payment,handler.items.get())
            context.drawItem(stack,i+76,j+47-18)
            context.drawItemInSlot(textRenderer,stack,i+76,j+47-18)
            RenderSystem.enableDepthTest()
        }

        //RenderSystem.restoreProjectionMatrix()
        DiffuseLighting.enableGuiDepthLighting()
        RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, TEXTURE)

        var tens: Int
        var ones: Int
        var tensOfst = 1
        var onesOfst = 8
        val tensImageOfst: Int
        var onesImageOfst = 9

        var power = handler.enchants.get()
        val hOffset = max(0,abs(power) - 1)
        val vOffset: Int
        val numeralOffset: Int
        if(power < 0){
            vOffset = 190
            numeralOffset = 222
        } else {
            vOffset = 174
            numeralOffset = 212
        }
        power = abs(power)
        if (hOffset <= 5){
            context.drawTexture(TEXTURE,
                    i + 104,
                    j + 29,
                    108 + 16 * (hOffset),
                    vOffset,
                    16,
                    16
                )
        } else{
            context.drawTexture(TEXTURE,
                    i + 104,
                    j + 29,
                    108 + 16 * 6,
                    vOffset,
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
                context.drawTexture(TEXTURE,
                    i + 104 + onesOfst,
                    j + 29 + 3, //three additional offset to align the number with the usual position
                    108 + 9 * onesImageOfst, //grab the image off the texture, using the 10 abstract numerals
                    numeralOffset,
                    9,
                    9
                )
                if (tens>0) context.drawTexture(TEXTURE,
                    i+ 104 + tensOfst,
                    j + 29 + 3, //three additional offset to align the number with the usual position
                    108 + 9 * tensImageOfst, //grab the image off the texture, using the 10 abstract numerals
                    numeralOffset,
                    9,
                    9
                )
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
        val i = (width - backgroundWidth) / 2
        val j = (height - backgroundHeight) / 2
        val h = mouseX - i - 104
        val v = mouseY - j - 29
        val hovered = (h >= 0 && v >= 0 && h < 32 && v < 16)
        if (hovered){
            val list = if (handler.enchants.get() > 0){
                listOf(AcText.translatable("screen.gearifiers.xp_clue_good"))
            } else if (handler.enchants.get() < 0){
                listOf(AcText.translatable("screen.gearifiers.xp_clue_bad").formatted(Formatting.RED))
            } else {
                listOf()
            }
            context.drawTooltip(textRenderer,list, mouseX, mouseY)
        }
    }

    companion object{
        internal val TEXTURE = Identifier(Gearifiers.MOD_ID,"textures/reroll_altar_gui.png")
    }

    fun drawInvalidRecipeArrow(context: DrawContext, x: Int, y: Int) {
            if ((handler.getSlot(0).hasStack() || handler.getSlot(1)
                    .hasStack()) && !handler.getSlot(2)
                    .hasStack()
            ) {
                context.drawTexture(TEXTURE, x + 99, y + 45, backgroundWidth, 0, 28, 21)
            }
    }
}