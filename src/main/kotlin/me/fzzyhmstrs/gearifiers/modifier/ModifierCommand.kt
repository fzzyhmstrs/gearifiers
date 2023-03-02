package me.fzzyhmstrs.gearifiers.modifier

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.fzzy_core.nbt_util.Nbt
import me.fzzyhmstrs.fzzy_core.nbt_util.NbtKeys
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper
import me.fzzyhmstrs.gearifiers.Gearifiers
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.Function
import java.util.function.Supplier

object ModifierCommand {

    internal val modifierList: MutableList<Identifier> = mutableListOf()

    fun registerAll(){
        ArgumentTypeRegistry.registerArgumentType(
            Identifier(Gearifiers.MOD_ID,"gear_modifier"),
            ModifierArgumentType::class.java,ConstantArgumentSerializer.of(
                Supplier {
                    ModifierArgumentType(
                        modifierList
                    )
                }
            )
        )
        ArgumentTypeRegistry.registerArgumentType(
            Identifier(Gearifiers.MOD_ID,"gear_modifier_removal"),
            ModifierRemovalArgumentType::class.java,ConstantArgumentSerializer.of(
                Supplier {
                    ModifierRemovalArgumentType(
                        modifierList
                    )
                }
            )
        )
        CommandRegistrationCallback.EVENT.register {commandDispatcher, _,_ -> register(commandDispatcher)}
    }

    private fun register(commandDispatcher: CommandDispatcher<ServerCommandSource>){
        commandDispatcher.register(
            CommandManager.literal("gearifiers")
                .requires { source -> source.hasPermissionLevel(2) }
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("modifier",ModifierArgumentType(modifierList))
                            .executes { context -> addModifier(context) }
                        )
                )
                .then(CommandManager.literal("remove")
                    .then(CommandManager.argument("modifier",ModifierRemovalArgumentType(modifierList))
                        .executes { context -> removeModifier(context)  }
                    )
                )
                .then(CommandManager.literal("removeAll")
                    .executes {context -> removeAllModifiers(context)}
                )
                .then(CommandManager.literal("reroll")
                    .executes {context -> rerollModifiers(context) }
                )
                .then(CommandManager.literal("addRandom")
                    .then(CommandManager.argument("luckBoost", IntegerArgumentType.integer(0))
                        .executes {context -> addRandomModifiers(context, IntegerArgumentType.getInteger(context,"luckBoost"))}
                    )
                    .executes {context -> addRandomModifiers(context) }
                )

        )
    }

    private fun checkAndApplyModifierToStack(modifierResult: Identifier, context: CommandContext<ServerCommandSource>, function: CommandApplier, successText: Function<ItemStack,Text>): Int{
        val player = context.source.player
        if (player == null){
            context.source.sendError(AcText.translatable("commands.gearifiers.failed.no_player"))
            return 0
        }

        val stack1 = player.mainHandStack
        return if (stack1.isEmpty){
            val stack2 = player.offHandStack
            if (stack2.isEmpty) {
                context.source.sendError(AcText.translatable("commands.gearifiers.failed.no_stacks"))
                0
            } else {
                val item = stack2.item
                if (item !is Modifiable || item.modifierInitializer != EquipmentModifierHelper) {
                    context.source.sendError(AcText.translatable("commands.gearifiers.failed.not_modifiable",stack2.toHoverableText()))
                    return 0
                }
                if (function.apply(modifierResult,stack2, player) == 0){
                    0
                } else {
                    context.source.sendFeedback(
                        successText.apply(player.offHandStack), true
                    )
                    1
                }
            }
        } else {
            val item = stack1.item
            if (item !is Modifiable || item.modifierInitializer != EquipmentModifierHelper) {
                context.source.sendError(AcText.translatable("commands.gearifiers.failed.not_modifiable",stack1.toHoverableText()))
                return 0
            }
            if (function.apply(modifierResult,stack1,player) == 0){
                0
            } else {
                context.source.sendFeedback(
                    successText.apply(player.mainHandStack), true
                )
                1
            }
        }
    }

    private fun addModifier(context: CommandContext<ServerCommandSource>): Int{
        val modifierResult = context.getArgument("modifier",Identifier::class.java)
        return checkAndApplyModifierToStack(
            modifierResult,
            context,
            {id,stack,_ ->
                if (EquipmentModifierHelper.addModifier(id,stack)) {
                    if (stack.damage > stack.maxDamage){
                        stack.damage = stack.maxDamage - 1
                    }
                    1
                } else {
                    context.source.sendError(AcText.translatable("commands.gearifiers.failed.failed_to_add",id,stack.toHoverableText()))
                    0
                }
            },
            { stack ->
                AcText.translatable(
                    "commands.gearifiers.success.add",
                    modifierResult,
                    stack.toHoverableText(),)
            }
        )

    }

    private fun removeModifier(context: CommandContext<ServerCommandSource>): Int{
        val modifierResult = context.getArgument("modifier",Identifier::class.java)
        return checkAndApplyModifierToStack(
            modifierResult,
            context,
            {id,stack,_ ->
                val nbt = stack.nbt
                if (nbt != null) {
                    val stackId = Nbt.getItemStackId(stack)
                    if (stackId != -1L) {
                        val mod = EquipmentModifierHelper.getModifierByType(id)
                        if (mod == null || mod.isPersistent()){
                            context.source.sendError(AcText.translatable("commands.gearifiers.failed.persistent",id))
                            0
                        } else {
                            EquipmentModifierHelper.removeModifier(id, stack)
                            1
                        }
                    } else {
                        context.source.sendError(AcText.translatable("commands.gearifiers.failed.failed_to_remove",id,stack.toHoverableText()))
                        0
                    }
                } else {
                    context.source.sendError(
                        AcText.translatable(
                            "commands.gearifiers.failed.failed_to_remove",
                            id,
                            stack.toHoverableText()
                        )
                    )
                    0
                }
            },
            { stack ->
                AcText.translatable(
                    "commands.gearifiers.success.remove",
                    modifierResult,
                    stack.toHoverableText())
            }
        )
    }

    private fun removeAllModifiers(context: CommandContext<ServerCommandSource>): Int{
        return checkAndApplyModifierToStack(
            Identifier("empty"),
            context,
            {_, stack,_ ->
                removeNonPersistentModifiers(stack)
            },
            { stack ->
                AcText.translatable(
                    "commands.gearifiers.success.remove_all",
                    stack.toHoverableText())
            }
        )
    }

    private fun removeNonPersistentModifiers(stack: ItemStack):Int{
        val nbt = stack.orCreateNbt
        val modList = EquipmentModifierHelper.getModifiersFromNbt(stack)
        if (modList.isEmpty()) return 0
        for (id in modList){
            val mod = EquipmentModifierHelper.getModifierByType(id) ?: continue
            if (mod.isPersistent()) continue
            Nbt.removeNbtFromList(NbtKeys.MODIFIERS.str(),nbt) { nbtEl: NbtCompound ->
                if (nbtEl.contains(NbtKeys.MODIFIER_ID.str())){
                    val chk = Identifier(nbtEl.getString(NbtKeys.MODIFIER_ID.str()))
                    chk == id
                } else {
                    false
                }
            }
        }
        val item = stack.item
        if (item is Modifiable){
            item.modifierInitializer.initializeModifiers(stack,nbt, listOf())
        }
        return 1
    }

    private fun rerollModifiers(context: CommandContext<ServerCommandSource>): Int{
        return checkAndApplyModifierToStack(
            Identifier("empty"),
            context,
            {_,stack, player ->
                EquipmentModifierHelper.rerollModifiers(stack,player.getWorld(),player)
                if (stack.damage > stack.maxDamage){
                    stack.damage = stack.maxDamage - 1
                }
                1
            },
            { stack ->
                AcText.translatable(
                    "commands.gearifiers.success.reroll",
                    stack.toHoverableText())
            }
        )
    }

    private fun addRandomModifiers(context: CommandContext<ServerCommandSource>, luckBoost: Int = 0): Int{
        return checkAndApplyModifierToStack(
            Identifier("empty"),
            context,
            {_,stack, player ->
                EquipmentModifierHelper.addRandomModifiers(stack,
                    LootContext.Builder(player.getWorld()).random(player.random).luck(player.luck + luckBoost).build(LootContextTypes.EMPTY)
                )
                if (stack.damage > stack.maxDamage){
                    stack.damage = stack.maxDamage - 1
                }
                1
            },
            { stack ->
                AcText.translatable(
                    "commands.gearifiers.success.add_random",
                    stack.toHoverableText())
            }
        )
    }

    private fun interface CommandApplier{
        fun apply(id: Identifier, stack: ItemStack, player: ServerPlayerEntity):Int
    }

}
