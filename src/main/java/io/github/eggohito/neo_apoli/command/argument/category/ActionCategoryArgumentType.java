package io.github.eggohito.neo_apoli.command.argument.category;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ActionCategoryArgumentType implements ArgumentType<ActionCategory<?>> {

	@Override
	public ActionCategory<?> parse(StringReader reader) throws CommandSyntaxException {
		return this.parse(reader, NbtOps.INSTANCE);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestIdentifiers(NeoApoliRegistries.ACTION_CATEGORY.getIds().stream(), builder);
	}

	private <I> ActionCategory<?> parse(StringReader reader, DynamicOps<I> ops) throws CommandSyntaxException {

		Identifier id = Identifier.fromCommandInputNonEmpty(reader);
		Dynamic<I> dynamic = new Dynamic<>(ops, ops.createString(id.toString()));

		return ActionCategory.CODEC.parse(dynamic).getOrThrow(err -> MiscUtil.createCommandException(() -> err));

	}

	public static ActionCategoryArgumentType category() {
		return new ActionCategoryArgumentType();
	}

	public static ActionCategory<?> getCategory(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		return context.getArgument(argumentName, ActionCategory.class);
	}

}
