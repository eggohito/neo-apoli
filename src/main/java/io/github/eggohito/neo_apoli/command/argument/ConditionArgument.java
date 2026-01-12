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

public class ConditionArgument extends ObjectEntryArgument<Condition> {

	protected ConditionArgument(HolderLookup.Provider wrapperLookup, boolean allowInlineDefinitions) {
		super(wrapperLookup, ConditionManager.createEntryCodec(allowInlineDefinitions));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(ConditionManager.ids(), builder);
	}

	public static ConditionArgument inlineCondition(CommandBuildContext registryAccess) {
		return new ConditionArgument(registryAccess, true);
	}

	public static ConditionArgument condition(CommandBuildContext registryAccess) {
		return new ConditionArgument(registryAccess, false);
	}

	public static Condition getCondition(CommandContext<CommandSourceStack> context, String argumentName) {
		return context.getArgument(argumentName, Condition.class);
	}

	public record Info() implements ArgumentTypeInfo<ConditionArgument, Info.Template> {

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
		public Template unpack(ConditionArgument argument) {
			return new Template(this, argument.codec.allowInlineDefinitions());
		}

		public record Template(Info type, boolean allowInlineDefinitions) implements ArgumentTypeInfo.Template<ConditionArgument> {

			@Override
			public ConditionArgument instantiate(CommandBuildContext buildContext) {
				return new ConditionArgument(buildContext, allowInlineDefinitions());
			}

		}

	}

}
