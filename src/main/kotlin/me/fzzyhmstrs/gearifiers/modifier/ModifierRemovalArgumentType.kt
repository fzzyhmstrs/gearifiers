package me.fzzyhmstrs.gearifiers.modifier

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper
import me.fzzyhmstrs.gearifiers.GearifiersClient
import net.minecraft.client.network.ClientCommandSource
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

class ModifierRemovalArgumentType(private val ids: List<Identifier>): ArgumentType<Identifier> {

    private val invalidIdException = DynamicCommandExceptionType { id -> AcText.translatable("commands.gearifiers.failed.invalid_id",id)}

    override fun parse(reader: StringReader): Identifier {
        val i = reader.cursor
        val identifier = Identifier.fromCommandInput(reader)
        return if (ids.contains(identifier)){
            identifier
        } else {
            reader.cursor = i
            throw invalidIdException.create(identifier)
        }

    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val source = context.source
        if (source is ServerCommandSource){
            val player = source.player
            if (player != null){
                val mods = if (!player.mainHandStack.isEmpty){
                    EquipmentModifierHelper.getModifiers(player.mainHandStack)
                } else if (!player.offHandStack.isEmpty){
                    EquipmentModifierHelper.getModifiers(player.offHandStack)
                } else {
                    listOf()
                }
                if (mods.isNotEmpty()){
                    return CommandSource.suggestIdentifiers(mods,builder)
                }
            }
        } else if (source is ClientCommandSource){
            val player = GearifiersClient.getPlayer()
            if (player != null){
                val mods = if (!player.mainHandStack.isEmpty){
                    EquipmentModifierHelper.getModifiers(player.mainHandStack)
                } else if (!player.offHandStack.isEmpty){
                    EquipmentModifierHelper.getModifiers(player.offHandStack)
                } else {
                    listOf()
                }

                return CommandSource.suggestIdentifiers(mods,builder)

            }
        }
        return CommandSource.suggestIdentifiers(ids,builder)
    }
}
