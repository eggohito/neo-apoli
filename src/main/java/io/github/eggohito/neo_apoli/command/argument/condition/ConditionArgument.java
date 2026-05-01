package io.github.eggohito.neo_apoli.command.argument.condition;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.command.argument.ObjectEntryArgument;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.condition.kind.ConditionKind;
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

public record ConditionArgument(HolderLookup.Provider lookupProvider, ConditionKind<?> kind, boolean allowInlineDefinitions) implements ObjectEntryArgument<ConditionArgument.Type> {

	@Override
	public Type mapType(Either<Dynamic<Tag>, ResourceLocation> either) {
		return either.map(Type.Inline::new, Type.Reference::new);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(ConditionManager.ids(kind()), builder);
	}

	public static ConditionArgument condition(HolderLookup.Provider lookupProvider, ConditionKind<?> category) {
		return new ConditionArgument(lookupProvider, category, false);
	}

	public static ConditionArgument inlineCondition(HolderLookup.Provider lookupProvider, ConditionKind<?> category) {
		return new ConditionArgument(lookupProvider, category, true);
	}

	public static <C extends Condition> C getCondition(CommandContext<CommandSourceStack> context, ConditionKind<C> category, String argument) throws CommandSyntaxException {
		return context.getArgument(argument, Type.class).get(category);
	}

	public sealed interface Type extends ObjectEntryArgument.Type {

		<C extends Condition> C get(ConditionKind<C> category) throws CommandSyntaxException;

		record Reference(ResourceLocation id) implements Type {

			@Override
			public <C extends Condition> C get(ConditionKind<C> category) throws CommandSyntaxException {
				return ConditionManager.getAsResult(category, id()).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

		}

		record Inline(Dynamic<Tag> packed) implements Type {

			@Override
			public <C extends Condition> C get(ConditionKind<C> category) throws CommandSyntaxException {
				return category.codec().parse(packed()).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

		}

	}

	public enum Info implements ArgumentTypeInfo<ConditionArgument, Info.Template> {

		INSTANCE;

		@Override
		public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
			buffer.writeResourceKey(NeoApoliRegistries.CONDITION_KIND.getResourceKey(template.kind()).orElseThrow());
			buffer.writeBoolean(template.allowInlineDefinitions());
		}

		@Override
		public @NotNull Template deserializeFromNetwork(FriendlyByteBuf buffer) {

			var category = NeoApoliRegistries.CONDITION_KIND.getValueOrThrow(buffer.readResourceKey(NeoApoliRegistryKeys.CONDITION_KIND));
			boolean allowInlineDefinitions = buffer.readBoolean();

			return new Template(this, category, allowInlineDefinitions);

		}

		@Override
		public void serializeToJson(Template template, JsonObject json) {
			json.addProperty("allow_inline_definitions", template.allowInlineDefinitions());
		}

		@Override
		public @NotNull Template unpack(ConditionArgument argument) {
			return new Template(this, argument.kind(), argument.allowInlineDefinitions());
		}

		public record Template(Info type, ConditionKind<?> kind, boolean allowInlineDefinitions) implements ArgumentTypeInfo.Template<ConditionArgument> {

			@Override
			public @NotNull ConditionArgument instantiate(CommandBuildContext context) {
				return new ConditionArgument(context, kind(), allowInlineDefinitions());
			}

		}

	}

}
