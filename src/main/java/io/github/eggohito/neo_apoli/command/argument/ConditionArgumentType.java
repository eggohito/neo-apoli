package io.github.eggohito.neo_apoli.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.codec.ValueSuppliedElementCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public record ConditionArgumentType(RegistryWrapper.WrapperLookup wrapperLookup, ConditionCategory<? extends Condition> category, Codec<? extends Condition> entryCodec) implements ArgumentType<Condition> {

	@Override
	public Condition parse(StringReader reader) throws CommandSyntaxException {
		return this.parse(reader, StringNbtReader.fromOps(NbtOps.INSTANCE));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestIdentifiers(ConditionManager.streamIds(category()), builder);
	}

	private <I> Condition parse(StringReader reader, StringNbtReader<I> snbtReader) throws CommandSyntaxException {

		RegistryOps<I> registryOps = wrapperLookup().getOps(snbtReader.getOps());
		Dynamic<I> result = parseAsNbt(registryOps, reader, snbtReader);

		return entryCodec().parse(result).getOrThrow(err -> MiscUtil.createCommandException(() -> err));

	}

	static <I> Dynamic<I> parseAsNbt(RegistryOps<I> ops, StringReader reader, StringNbtReader<I> snbtReader) throws CommandSyntaxException {

		int prevCursor = reader.getCursor();
		I read = snbtReader.readAsArgument(reader);

		if (hasFinishedReading(reader)) {
			return new Dynamic<>(ops, read);
		}

		else {

			reader.setCursor(prevCursor);
			Identifier id = Identifier.fromCommandInput(reader);

			if (hasFinishedReading(reader)) {
				return new Dynamic<>(ops, ops.createString(id.toString()));
			}

			else {
				reader.setCursor(prevCursor);
				throw MiscUtil.createCommandExceptionWithContext(reader, Text.translatable("argument.resource_or_id.invalid"));
			}

		}

	}

	static boolean hasFinishedReading(StringReader reader) {
		return !reader.canRead() || reader.peek() == ' ';
	}

	public static <C extends Condition, CT extends ConditionCategory<C>> ConditionArgumentType condition(CommandRegistryAccess registryAccess, CT category) {
		return new ConditionArgumentType(registryAccess, category, new ValueSuppliedElementCodec<>(category.codec(), true, id -> ConditionManager.getAsResult(category, id), ConditionManager::getIdAsResult));
	}

	public static <C extends Condition> C getCondition(CommandContext<ServerCommandSource> context, String argumentName, Class<C> conditionClass) {
		return context.getArgument(argumentName, conditionClass);
	}

	public record Serializer() implements ArgumentSerializer<ConditionArgumentType, Serializer.Properties> {

		@Override
		public void writePacket(Properties properties, PacketByteBuf buf) {
			buf.writeRegistryKey(NeoApoliRegistries.CONDITION_CATEGORY.getKey(properties.category()).orElseThrow());
		}

		@Override
		public Properties fromPacket(PacketByteBuf buf) {
			return new Properties(this, NeoApoliRegistries.CONDITION_CATEGORY.getValueOrThrow(buf.readRegistryKey(NeoApoliRegistryKeys.CONDITION_CATEGORY)));
		}

		@Override
		public void writeJson(Properties properties, JsonObject json) {
			json.addProperty("category", Objects.requireNonNull(NeoApoliRegistries.CONDITION_CATEGORY.getId(properties.category())).toString());
		}

		@Override
		public Properties getArgumentTypeProperties(ConditionArgumentType argumentType) {
			return new Properties(this, argumentType.category());
		}

		public record Properties(Serializer serializer, ConditionCategory<?> category) implements ArgumentTypeProperties<ConditionArgumentType> {

			@Override
			public ConditionArgumentType createType(CommandRegistryAccess registryAccess) {
				return ConditionArgumentType.condition(registryAccess, category());
			}

			@Override
			public ArgumentSerializer<ConditionArgumentType, ?> getSerializer() {
				return serializer();
			}

		}

	}

}
