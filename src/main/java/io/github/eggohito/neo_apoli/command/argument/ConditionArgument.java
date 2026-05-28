package io.github.eggohito.neo_apoli.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.manager.ConditionManager;
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

public record ConditionArgument(HolderLookup.Provider registries, boolean allowInlineDefinitions) implements ObjectEntryArgument<ConditionArgument.Result> {

	@Override
	public Result mapType(Either<Dynamic<Tag>, ResourceLocation> either) {
		return either.map(Result.Inline::new, Result.Reference::new);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(ConditionManager.ids(), builder);
	}

	public static ConditionArgument condition(HolderLookup.Provider registries) {
		return new ConditionArgument(registries, false);
	}

	public static ConditionArgument inlineCondition(HolderLookup.Provider registries) {
		return new ConditionArgument(registries, true);
	}

	public static Condition getCondition(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		return context.getArgument(name, Result.class).get();
	}

	public sealed interface Result extends ObjectEntryArgument.Result {

		Condition get() throws CommandSyntaxException;

		record Reference(ResourceLocation id) implements Result {

			@Override
			public Condition get() throws CommandSyntaxException {
				return ConditionManager.getAsResult(id).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

		}

		record Inline(Dynamic<Tag> packed) implements Result {

			@Override
			public Condition get() throws CommandSyntaxException {
				return Condition.CODEC.parse(packed()).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

		}

	}

	public enum Info implements ArgumentTypeInfo<ConditionArgument, Template> {

		INSTANCE;

		@Override
		public void serializeToNetwork(ConditionArgument.Template template, FriendlyByteBuf buffer) {
			buffer.writeBoolean(template.allowInlineDefinitions());
		}

		@Override
		public ConditionArgument.@NotNull Template deserializeFromNetwork(FriendlyByteBuf buffer) {
			return new ConditionArgument.Template(this, buffer.readBoolean());
		}

		@Override
		public void serializeToJson(ConditionArgument.Template template, JsonObject json) {
			json.addProperty("allow_inline_definitions", template.allowInlineDefinitions());
		}

		@Override
		public ConditionArgument.@NotNull Template unpack(ConditionArgument argument) {
			return new ConditionArgument.Template(this, argument.allowInlineDefinitions());
		}

	}

	public record Template(Info type, boolean allowInlineDefinitions) implements ArgumentTypeInfo.Template<ConditionArgument> {

		@Override
		public @NotNull ConditionArgument instantiate(CommandBuildContext context) {
			return new ConditionArgument(context, allowInlineDefinitions());
		}

	}

}
