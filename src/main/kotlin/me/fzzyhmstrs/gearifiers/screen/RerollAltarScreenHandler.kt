package me.fzzyhmstrs.gearifiers.screen

import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.fzzy_core.nbt_util.NbtKeys
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper
import me.fzzyhmstrs.gearifiers.config.ItemCostLoader
import me.fzzyhmstrs.gearifiers.registry.RegisterHandler
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.screen.ForgingScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents

class RerollAltarScreenHandler(syncId: Int, playerInventory: PlayerInventory, context: ScreenHandlerContext): ForgingScreenHandler(RegisterHandler.REROLL_ALTAR_HANDLER,syncId, playerInventory, context) {

    constructor(syncId: Int, playerInventory: PlayerInventory): this(syncId, playerInventory, ScreenHandlerContext.EMPTY)

    override fun canUse(state: BlockState?): Boolean {
        return true
    }

    override fun canTakeOutput(player: PlayerEntity?, present: Boolean): Boolean {
        return checkForMatch()
    }

    override fun onTakeOutput(player: PlayerEntity, stack: ItemStack) {
        decrementStack(0)
        decrementStack(1)
        this.context.run{world,pos ->
            world.playSound(null,pos,SoundEvents.BLOCK_SMITHING_TABLE_USE,SoundCategory.BLOCKS,1.0f,world.random.nextFloat() * 0.1f + 0.9f)
        }
        reroll(player, stack)
    }

    private fun decrementStack(slot: Int) {
        val itemStack = input.getStack(slot)
        itemStack.decrement(1)
        input.setStack(slot, itemStack)
    }

    private fun reroll(player: PlayerEntity, stack: ItemStack){
        val playerWorld = player.world
        if (playerWorld !is ServerWorld) return
        val nbt = stack.orCreateNbt
        nbt.remove(NbtKeys.MODIFIERS.str())
        nbt.remove(NbtKeys.ITEM_STACK_ID.str())
        val contextBuilder = LootContext.Builder(playerWorld).random(playerWorld.random).luck(player.luck)
        EquipmentModifierHelper.addRandomModifiers(stack,contextBuilder.build(LootContextTypes.EMPTY))
    }

    override fun updateResult() {
        val stack = this.input.getStack(0)
        if (stack.isEmpty || !checkForMatch()) {
            output.setStack(0, ItemStack.EMPTY)
        } else {
            output.setStack(0, stack.copy())
        }
    }

    private fun checkForMatch(): Boolean{
        val stack1 = this.input.getStack(0)
        if (stack1.isEmpty) return false
        val item = stack1.item
        if (item !is Modifiable) return false
        if (item.modifierInitializer != EquipmentModifierHelper) return false
        val itemCost = ItemCostLoader.getItemCost(item)
        val stack2 = this.input.getStack(1)
        return stack2.item == itemCost
    }

    override fun transferSlot(player: PlayerEntity, index: Int): ItemStack? {
        var itemStack = ItemStack.EMPTY
        val slot = slots[index]
        if (slot.hasStack()) {
            val itemStack2 = slot.stack
            itemStack = itemStack2.copy()
            if (index == 2) {
                reroll(player,itemStack2)
                if (!insertItem(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY
                }
                println("quick transferring")
                slot.onQuickTransfer(itemStack2, itemStack)
            } else if (index == 0 || index == 1) {
                if (!insertItem(itemStack2, 3, 39, false)) {
                    return ItemStack.EMPTY
                }
            } else if (index in 3..38) {
                val i: Int = if (isUsableAsAddition(itemStack)) 1 else 0
                if (!insertItem(itemStack2, i, 2, false)) {
                    return ItemStack.EMPTY
                }
            }
            println(itemStack)
            println(itemStack2)
            if (itemStack2.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
            if (itemStack2.count == itemStack.count) {
                return ItemStack.EMPTY
            }
            println("take iteming")
            onTakeOutput(player, itemStack)
        }
        return itemStack
    }

}