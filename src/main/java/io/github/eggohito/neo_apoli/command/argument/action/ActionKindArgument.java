package io.github.eggohito.neo_apoli.command.argument.action;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.DynamicOps;
import io.github.eggohito.neo_apoli.action.kind.ActionKind;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public record ActionKindArgument() implements ArgumentType<ActionKind<?>> {

	@Override
	public ActionKind<?> parse(StringReader reader) throws CommandSyntaxException {

		String id = ResourceLocation.readNonEmpty(reader).toString();
		DynamicOps<Tag> ops = NbtOps.INSTANCE;

		return ActionKind.CODEC.parse(ops, ops.createString(id)).getOrThrow(error -> MiscUtil.createCommandException(() -> error));

	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(NeoApoliRegistries.ACTION_KIND.keySet().stream(), builder);
	}

	public static ActionKindArgument category() {
		return new ActionKindArgument();
	}

	public static ActionKind<?> getCategory(CommandContext<CommandSourceStack> context, String argumentName) {
		return context.getArgument(argumentName, ActionKind.class);
	}

}
