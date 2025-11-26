package io.github.eggohito.neo_apoli.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;

import java.util.concurrent.CompletableFuture;

public class ActionArgumentType extends ObjectEntryArgumentType<Action> {

	protected ActionArgumentType(HolderLookup.Provider wrapperLookup, boolean allowInlineDefinitions) {
		super(wrapperLookup, ActionManager.createEntryCodec(allowInlineDefinitions));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(ActionManager.ids(), builder);
	}

	public static ActionArgumentType inlineAction(CommandBuildContext registryAccess) {
		return new ActionArgumentType(registryAccess, true);
	}

	public static ActionArgumentType action(CommandBuildContext registryAccess) {
		return new ActionArgumentType(registryAccess, false);
	}

	public static Action getAction(CommandContext<CommandSourceStack> context, String argumentName) {
		return context.getArgument(argumentName, Action.class);
	}

	public record Info() implements ArgumentTypeInfo<ActionArgumentType, Info.Template> {

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
		public Template unpack(ActionArgumentType argumentType) {
			return new Template(this, argumentType.codec.allowInlineDefinitions());
		}

		public record Template(Info type, boolean allowInlineDefinitions) implements ArgumentTypeInfo.Template<ActionArgumentType> {

			@Override
			public ActionArgumentType instantiate(CommandBuildContext commandBuildContext) {
				return new ActionArgumentType(commandBuildContext, allowInlineDefinitions());
			}

		}

	}

}
