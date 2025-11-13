package io.github.eggohito.neo_apoli.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.eggohito.neo_apoli.codec.ValueSuppliedElementCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import lombok.AllArgsConstructor;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class ConditionArgumentType extends ObjectEntryArgumentType<Condition> {

	protected ConditionArgumentType(RegistryWrapper.WrapperLookup wrapperLookup, ValueSuppliedElementCodec<Condition> codec) {
		super(wrapperLookup, codec);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestIdentifiers(ConditionManager.ids(), builder);
	}

	public static ConditionArgumentType condition(CommandRegistryAccess registryAccess, boolean allowInlineDefinitions) {
		return new ConditionArgumentType(registryAccess, ConditionManager.createEntryCodec(allowInlineDefinitions));
	}

	public static ConditionArgumentType condition(CommandRegistryAccess registryAccess) {
		return condition(registryAccess, true);
	}

	public static Condition getCondition(CommandContext<ServerCommandSource> context, String argumentName) {
		return context.getArgument(argumentName, Condition.class);
	}

	public final static class Serializer implements ArgumentSerializer<ConditionArgumentType, Serializer.Properties> {

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
		public Properties getArgumentTypeProperties(ConditionArgumentType argumentType) {
			return new Properties(argumentType.codec.allowInlineDefinitions());
		}

		@AllArgsConstructor
		public final class Properties implements ArgumentTypeProperties<ConditionArgumentType> {

			private final boolean allowInlineDefinitions;

			@Override
			public ConditionArgumentType createType(CommandRegistryAccess registryAccess) {
				return condition(registryAccess, allowInlineDefinitions);
			}

			@Override
			public ArgumentSerializer<ConditionArgumentType, ?> getSerializer() {
				return Serializer.this;
			}

		}

	}

}
