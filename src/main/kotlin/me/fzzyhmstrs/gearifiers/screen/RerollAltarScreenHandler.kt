package me.fzzyhmstrs.gearifiers.screen

import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import me.fzzyhmstrs.gearifiers.config.ItemCostLoader
import me.fzzyhmstrs.gearifiers.registry.RegisterHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.screen.Property
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class RerollAltarScreenHandler(syncId: Int, playerInventory: PlayerInventory, val context: ScreenHandlerContext): ScreenHandler(RegisterHandler.REROLL_ALTAR_HANDLER,syncId) {

    constructor(syncId: Int, playerInventory: PlayerInventory): this(syncId, playerInventory, ScreenHandlerContext.EMPTY)

    internal val enchants = Property.create()
    internal val items = Property.create()
    private val world: World? = null
    protected val input: Inventory = object : SimpleInventory(2) {
        override fun markDirty() {
            super.markDirty()
            this@RerollAltarScreenHandler.onContentChanged(this)
        }
    }
    protected val output = CraftingResultInventory()
    protected val player: PlayerEntity = playerInventory.player
    
    init{
        addProperty(enchants).set(0)
        addProperty(items).set(1)
        var i: Int
        addSlot(Slot(input, 0, 27, 47))
        addSlot(Slot(input, 1, 76, 47))
        addSlot(object : Slot(output, 0, 134, 47) {
            override fun canInsert(stack: ItemStack): Boolean {
                return false
            }

            override fun canTakeItems(playerEntity: PlayerEntity): Boolean {
                return this@RerollAltarScreenHandler.canTakeOutput(playerEntity, hasStack())
            }

            override fun onTakeItem(player: PlayerEntity, stack: ItemStack) {
                this@RerollAltarScreenHandler.onTakeOutput(player, stack)
            }
        })

        i = 0
        while (i < 3) {
            for (j in 0..8) {
                addSlot(Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
            ++i
        }
        i = 0
        while (i < 9) {
            this.addSlot(Slot(playerInventory, i, 8 + i * 18, 142))
            ++i
        }

    }

    fun getRerollItem(): ItemStack{
        return input.getStack(0)
    }

    fun canTakeOutput(player: PlayerEntity, present: Boolean): Boolean {
        return checkForMatch(player)
    }

    fun onTakeOutput(player: PlayerEntity, stack: ItemStack) {
        player.applyEnchantmentCosts(stack, enchants.get())
        decrementStack(0)
        decrementStack(1, items.get())
        this.context.run{world,pos ->
            world.playSound(null,pos,SoundEvents.BLOCK_SMITHING_TABLE_USE,SoundCategory.BLOCKS,1.0f,world.random.nextFloat() * 0.1f + 0.9f)
        }
        reroll(player, stack)
    }

    override fun onClosed(player: PlayerEntity?) {
        super.onClosed(player)
        context.run { world: World?, pos: BlockPos? ->
            dropInventory(
                player,
                input
            )
        }
    }

    override fun onContentChanged(inventory: Inventory) {
        super.onContentChanged(inventory)
        if (inventory === input) {
            updateResult()
        }
    }

    private fun decrementStack(slot: Int, amount: Int = 1) {
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

    private fun updateResult() {
        val stack = this.input.getStack(0)
        if (stack.isEmpty || !checkForMatch(player)) {
            if (!stack.isEmpty){
                enchants.set(rerollCost(stack) * -1)
                items.set(itemCost(stack))
            } else {
                enchants.set(0)
            }
            output.setStack(0, ItemStack.EMPTY)
        } else {
            enchants.set(rerollCost(stack))
            items.set(itemCost(stack))
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
        if (!item.canBeModifiedBy(EquipmentModifierHelper.getType())) return false
        if (GearifiersConfig.modifiers.enableRerollXpCost){
            val cost = rerollCost(stack1)
            if (player.experienceLevel < cost && !player.abilities.creativeMode) return false
        }
        val stack2 = this.input.getStack(1)
        if (itemCost(stack1) > stack2.count) return false
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

    private fun itemCost(stack: ItemStack): Int{
        val nbt = stack.nbt
        return if (nbt == null){
            1
        } else if (!nbt.contains("rerolls")) {
            1
        } else {
            val rerolls = nbt.getInt("rerolls")
            GearifiersConfig.modifiers.getItemCountNeeded(rerolls)
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

    override fun canUse(player: PlayerEntity?): Boolean {
        return context.get(
            { _, pos: BlockPos ->
                player!!.squaredDistanceTo(
                    pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5
                ) <= 64.0
            }, true
        )
    }

    private fun isUsableAsAddition(stack: ItemStack): Boolean {
        return !(stack.isDamageable || stack.item is ToolItem || stack.item is ArmorItem)
    }

}
