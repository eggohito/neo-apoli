package io.github.eggohito.neo_apoli.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.manager.ActionManager;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public record ActionArgument(HolderLookup.Provider registries, boolean allowInlineDefinitions) implements ObjectEntryArgument<ObjectEntryArgument.Result> {

	@Override
	public ObjectEntryArgument.Result mapType(Either<Dynamic<Tag>, ResourceLocation> either) {
		return either.map(Result.Inline::new, Result.Reference::new);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(ActionManager.ids(), builder);
	}

	public static ActionArgument action(HolderLookup.Provider registries) {
		return new ActionArgument(registries, false);
	}

	public static ActionArgument inlineAction(HolderLookup.Provider registries) {
		return new ActionArgument(registries, true);
	}

	public static Action getAction(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		return context.getArgument(name, Result.class).get();
	}

	public sealed interface Result extends ObjectEntryArgument.Result {

		Action get() throws CommandSyntaxException;

		record Reference(ResourceLocation id) implements Result {

			@Override
			public Action get() throws CommandSyntaxException {
				return ActionManager.getAsResult(id()).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

		}

		record Inline(Dynamic<Tag> packed) implements Result {

			@Override
			public Action get() throws CommandSyntaxException {
				return Action.CODEC.parse(packed()).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

		}

	}

	public enum Info implements ArgumentTypeInfo<ActionArgument, Template> {

		INSTANCE;

		@Override
		public void serializeToNetwork(ActionArgument.Template template, FriendlyByteBuf buffer) {
			buffer.writeBoolean(template.allowInlineDefinitions());
		}

		@Override
		public ActionArgument.@NotNull Template deserializeFromNetwork(FriendlyByteBuf buffer) {
			return new ActionArgument.Template(this, buffer.readBoolean());
		}

		@Override
		public void serializeToJson(ActionArgument.Template template, JsonObject json) {
			json.addProperty("allow_inline_definitions", template.allowInlineDefinitions());
		}

		@Override
		public ActionArgument.@NotNull Template unpack(ActionArgument argument) {
			return new ActionArgument.Template(this, argument.allowInlineDefinitions());
		}

	}

	public record Template(Info type, boolean allowInlineDefinitions) implements ArgumentTypeInfo.Template<ActionArgument> {

		@Override
		public @NotNull ActionArgument instantiate(CommandBuildContext context) {
			return new ActionArgument(context, allowInlineDefinitions());
		}

	}

}
