package io.github.eggohito.neo_apoli.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;

import java.util.concurrent.CompletableFuture;

public class ConditionArgumentType extends ObjectEntryArgumentType<Condition> {

	protected ConditionArgumentType(HolderLookup.Provider wrapperLookup, boolean allowInlineDefinitions) {
		super(wrapperLookup, ConditionManager.createEntryCodec(allowInlineDefinitions));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(ConditionManager.ids(), builder);
	}

	public static ConditionArgumentType inlineCondition(CommandBuildContext registryAccess) {
		return new ConditionArgumentType(registryAccess, true);
	}

	public static ConditionArgumentType condition(CommandBuildContext registryAccess) {
		return new ConditionArgumentType(registryAccess, false);
	}

	public static Condition getCondition(CommandContext<CommandSourceStack> context, String argumentName) {
		return context.getArgument(argumentName, Condition.class);
	}

	public record Info() implements ArgumentTypeInfo<ConditionArgumentType, Info.Template> {

		@Override
		public void serializeToNetwork(Template template, FriendlyByteBuf buf) {
			buf.writeBoolean(template.allowInlineDefinitions());
		}

		@Override
		public Template deserializeFromNetwork(FriendlyByteBuf buf) {
			return new Template(this, buf.readBoolean());
		}

		@Override
		public void serializeToJson(Template template, JsonObject jsonObject) {
			jsonObject.addProperty("allow_inline_definitions", template.allowInlineDefinitions());
		}

		@Override
		public Template unpack(ConditionArgumentType argumentType) {
			return new Template(this, argumentType.codec.allowInlineDefinitions());
		}

		public record Template(Info type, boolean allowInlineDefinitions) implements ArgumentTypeInfo.Template<ConditionArgumentType> {

			@Override
			public ConditionArgumentType instantiate(CommandBuildContext commandBuildContext) {
				return new ConditionArgumentType(commandBuildContext, allowInlineDefinitions());
			}

		}

	}

}
