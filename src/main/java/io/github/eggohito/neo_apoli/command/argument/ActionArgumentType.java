package io.github.eggohito.neo_apoli.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.codec.ValueSuppliedElementCodec;
import lombok.AllArgsConstructor;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class ActionArgumentType extends ObjectEntryArgumentType<Action> {

	protected ActionArgumentType(RegistryWrapper.WrapperLookup wrapperLookup, ValueSuppliedElementCodec<Action> codec) {
		super(wrapperLookup, codec);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestIdentifiers(ActionManager.ids(), builder);
	}

	public static ActionArgumentType action(CommandRegistryAccess registryAccess, boolean allowInlineDefinitions) {
		return new ActionArgumentType(registryAccess, ActionManager.createEntryCodec(allowInlineDefinitions));
	}

	public static ActionArgumentType action(CommandRegistryAccess registryAccess) {
		return action(registryAccess, true);
	}

	public static Action getAction(CommandContext<ServerCommandSource> context, String argumentName) {
		return context.getArgument(argumentName, Action.class);
	}

	public final static class Serializer implements ArgumentSerializer<ActionArgumentType, Serializer.Properties> {

		@Override
		public void writePacket(Properties properties, PacketByteBuf buf) {
			buf.writeBoolean(properties.allowInlineDefinitions);
		}

		@Override
		public Properties fromPacket(PacketByteBuf buf) {
			return new Properties(buf.readBoolean());
		}

		@Override
		public void writeJson(Properties properties, JsonObject json) {
			json.addProperty("allow_inline_definitions", properties.allowInlineDefinitions);
		}

		@Override
		public Properties getArgumentTypeProperties(ActionArgumentType argumentType) {
			return new Properties(argumentType.codec.allowInlineDefinitions());
		}

		@AllArgsConstructor
		public final class Properties implements ArgumentTypeProperties<ActionArgumentType> {

			private final boolean allowInlineDefinitions;

			@Override
			public ActionArgumentType createType(CommandRegistryAccess registryAccess) {
				return action(registryAccess, allowInlineDefinitions);
			}

			@Override
			public ArgumentSerializer<ActionArgumentType, ?> getSerializer() {
				return Serializer.this;
			}

		}

	}

}
