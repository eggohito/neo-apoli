package io.github.eggohito.neo_apoli.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public record ActionArgument(HolderLookup.Provider lookupProvider, boolean allowInlineDefinitions) implements ObjectEntryArgument<ActionArgument.Type> {

	@Override
	public Type mapType(Either<Dynamic<Tag>, ResourceLocation> either) {
		return either.map(Type.Inline::new, Type.Reference::new);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(ActionManager.ids(), builder);
	}

	public static ActionArgument action(HolderLookup.Provider lookupProvider) {
		return new ActionArgument(lookupProvider, false);
	}

	public static ActionArgument inlineAction(HolderLookup.Provider lookupProvider) {
		return new ActionArgument(lookupProvider, true);
	}

	public static Action getAction(CommandContext<CommandSourceStack> context, String argument) throws CommandSyntaxException {
		return context.getArgument(argument, Type.class).get();
	}

	public sealed interface Type extends ObjectEntryArgument.Type<Action> {

		record Reference(ResourceLocation id) implements Type {

			@Override
			public Action get() throws CommandSyntaxException {
				return ActionManager.getAsResult(id()).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

		}

		record Inline(Dynamic<Tag> packed) implements Type {

			@Override
			public Action get() throws CommandSyntaxException {
				return Action.CODEC.parse(packed).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

		}

	}

	public enum Info implements ArgumentTypeInfo<ActionArgument, Info.Template> {

		INSTANCE;

		@Override
		public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
			buffer.writeBoolean(template.allowInlineDefinitions());
		}

		@Override
		public Template deserializeFromNetwork(FriendlyByteBuf buffer) {
			return new Template(this, buffer.readBoolean());
		}

		@Override
		public void serializeToJson(Template template, JsonObject json) {
			json.addProperty("allow_inline_definitions", template.allowInlineDefinitions());
		}

		@Override
		public Template unpack(ActionArgument argument) {
			return new Template(this, argument.allowInlineDefinitions());
		}

		public record Template(Info type, boolean allowInlineDefinitions) implements ArgumentTypeInfo.Template<ActionArgument> {

			@Override
			public ActionArgument instantiate(CommandBuildContext context) {
				return new ActionArgument(context, allowInlineDefinitions());
			}

		}

	}

}
