package io.github.eggohito.neo_apoli.command.argument.action;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.command.argument.ObjectEntryArgument;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
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

public record ActionArgument(HolderLookup.Provider lookupProvider, Action.Kind<?> kind, boolean allowInlineDefinitions) implements ObjectEntryArgument<ActionArgument.Type> {

	@Override
	public Type mapType(Either<Dynamic<Tag>, ResourceLocation> either) {
		return either.map(Type.Inline::new, Type.Reference::new);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(ActionManager.ids(kind()), builder);
	}

	public static ActionArgument action(HolderLookup.Provider lookupProvider, Action.Kind<?> category) {
		return new ActionArgument(lookupProvider, category, false);
	}

	public static ActionArgument inlineAction(HolderLookup.Provider lookupProvider, Action.Kind<?> category) {
		return new ActionArgument(lookupProvider, category, true);
	}

	public static <A extends Action> A getAction(CommandContext<CommandSourceStack> context, Action.Kind<A> category, String argument) throws CommandSyntaxException {
		return context.getArgument(argument, Type.class).get(category);
	}

	public sealed interface Type extends ObjectEntryArgument.Type {

		<A extends Action> A get(Action.Kind<A> category) throws CommandSyntaxException;

		record Reference(ResourceLocation id) implements Type {

			@Override
			public <A extends Action> A get(Action.Kind<A> category) throws CommandSyntaxException {
				return ActionManager.getAsResult(category, id()).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

		}

		record Inline(Dynamic<Tag> packed) implements Type {

			@Override
			public <A extends Action> A get(Action.Kind<A> category) throws CommandSyntaxException {
				return category.codec().parse(packed).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

		}

	}

	public enum Info implements ArgumentTypeInfo<ActionArgument, Info.Template> {

		INSTANCE;

		@Override
		public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
			buffer.writeResourceKey(NeoApoliRegistries.ACTION_KIND.getResourceKey(template.kind()).orElseThrow());
			buffer.writeBoolean(template.allowInlineDefinitions());
		}

		@Override
		public @NotNull Template deserializeFromNetwork(FriendlyByteBuf buffer) {

			var category = NeoApoliRegistries.ACTION_KIND.getValueOrThrow(buffer.readResourceKey(NeoApoliRegistryKeys.ACTION_KIND));
			boolean allowInlineDefinitions = buffer.readBoolean();

			return new Template(this, category, allowInlineDefinitions);

		}

		@Override
		public void serializeToJson(Template template, JsonObject json) {
			json.addProperty("allow_inline_definitions", template.allowInlineDefinitions());
		}

		@Override
		public @NotNull Template unpack(ActionArgument argument) {
			return new Template(this, argument.kind(), argument.allowInlineDefinitions());
		}

		public record Template(Info type, Action.Kind<?> kind, boolean allowInlineDefinitions) implements ArgumentTypeInfo.Template<ActionArgument> {

			@Override
			public @NotNull ActionArgument instantiate(CommandBuildContext context) {
				return new ActionArgument(context, kind(), allowInlineDefinitions());
			}

		}

	}

}
