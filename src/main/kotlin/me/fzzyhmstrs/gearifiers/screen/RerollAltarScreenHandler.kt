package me.fzzyhmstrs.gearifiers.screen

import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import me.fzzyhmstrs.gearifiers.config.ItemCostLoader
import me.fzzyhmstrs.gearifiers.registry.RegisterHandler
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ForgingScreenHandler
import net.minecraft.screen.Property
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents

class RerollAltarScreenHandler(syncId: Int, playerInventory: PlayerInventory, context: ScreenHandlerContext): ForgingScreenHandler(RegisterHandler.REROLL_ALTAR_HANDLER,syncId, playerInventory, context) {

    constructor(syncId: Int, playerInventory: PlayerInventory): this(syncId, playerInventory, ScreenHandlerContext.EMPTY)

    internal val enchants = Property.create()
    private val player = playerInventory.player
    
    init{
        addProperty(enchants).set(0)
    }
    
    override fun canUse(state: BlockState?): Boolean {
        return true
    }

    override fun canTakeOutput(player: PlayerEntity, present: Boolean): Boolean {
        return checkForMatch(player)
    }

    override fun onTakeOutput(player: PlayerEntity, stack: ItemStack) {
        player.applyEnchantmentCosts(stack, enchants.get())
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
        if (!nbt.contains("rerolls")){
            nbt.putInt("rerolls",1)
        } else {
            val prev = nbt.getInt("rerolls")
            nbt.putInt("rerolls", prev + 1)
        }
        EquipmentModifierHelper.rerollModifiers(stack,playerWorld,player)
        if (stack.damage > stack.maxDamage) {
            stack.damage = stack.maxDamage - 1
        }
    }

    override fun updateResult() {
        val stack = this.input.getStack(0)
        if (stack.isEmpty || !checkForMatch(player)) {
            if (!stack.isEmpty){
                enchants.set(rerollCost(stack) * -1)
            } else {
                enchants.set(0)
            }
            output.setStack(0, ItemStack.EMPTY)
        } else {
            enchants.set(rerollCost(stack))
            output.setStack(0, stack.copy())
        }
        sendContentUpdates()
    }

    private fun checkForMatch(player: PlayerEntity): Boolean{
        val stack1 = this.input.getStack(0)
        if (stack1.isEmpty) return false
        if (GearifiersConfig.blackList.isItemBlackListed((stack1))) return false
        val item = stack1.item
        if (item !is Modifiable) return false
        if (item.modifierInitializer != EquipmentModifierHelper) return false
        if (GearifiersConfig.modifiers.enableRerollXpCost){
            val cost = rerollCost(stack1)
            if (player.experienceLevel < cost && !player.abilities.creativeMode) return false
        }
        return ItemCostLoader.itemCostMatches(item,this.input.getStack(1).item)
    }
    
    private fun rerollCost(stack: ItemStack): Int{
        val nbt = stack.nbt
        return if (nbt == null){
            GearifiersConfig.modifiers.firstRerollXpCost
        } else if (!nbt.contains("rerolls")) {
            GearifiersConfig.modifiers.firstRerollXpCost
        } else {
            val rerolls = nbt.getInt("rerolls")
            GearifiersConfig.modifiers.firstRerollXpCost + (rerolls * GearifiersConfig.modifiers.addedRerollXpCostPerRoll)
        }
    }

    override fun quickMove(player: PlayerEntity, index: Int): ItemStack? {
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
                //println("quick transferring")
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
            //println(itemStack)
            //println(itemStack2)
            if (itemStack2.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
            if (itemStack2.count == itemStack.count) {
                return ItemStack.EMPTY
            }
            //println("take iteming")
            slot.onTakeItem(player, itemStack2)
        }
        return itemStack
    }

}
