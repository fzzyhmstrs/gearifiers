package me.fzzyhmstrs.gearifiers.modifier

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.command.CommandSource
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

class ModifierArgumentType(private val ids: List<Identifier>): ArgumentType<Identifier> {

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
        return CommandSource.suggestIdentifiers(ids,builder)
    }
}