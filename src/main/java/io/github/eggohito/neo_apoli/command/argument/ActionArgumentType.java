package io.github.eggohito.neo_apoli.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
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

public record ActionArgumentType(RegistryWrapper.WrapperLookup wrapperLookup, ActionCategory<?> category) implements ArgumentType<Action> {

	@Override
	public Action parse(StringReader reader) throws CommandSyntaxException {
		return this.parse(reader, StringNbtReader.fromOps(NbtOps.INSTANCE));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestIdentifiers(ActionManager.streamIds(category()), builder);
	}

	private <I> Action parse(StringReader reader, StringNbtReader<I> snbtReader) throws CommandSyntaxException {

		RegistryOps<I> registryOps = wrapperLookup().getOps(snbtReader.getOps());
		Dynamic<I> result = parseAsNbt(registryOps, reader, snbtReader);

		return category().entryCodec().parse(result).getOrThrow(err -> MiscUtil.createCommandException(() -> err));

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

	public static <A extends Action, C extends ActionCategory<A>> ActionArgumentType action(CommandRegistryAccess registryAccess, C category) {
		return new ActionArgumentType(registryAccess, category);
	}

	@SuppressWarnings("unchecked")
	public static <A extends Action> A getAction(CommandContext<ServerCommandSource> context, String argumentName) {
		return (A) context.getArgument(argumentName, Action.class);
	}

	public record Serializer() implements ArgumentSerializer<ActionArgumentType, Serializer.Properties> {

		@Override
		public void writePacket(Properties properties, PacketByteBuf buf) {
			buf.writeRegistryKey(NeoApoliRegistries.ACTION_CATEGORY.getKey(properties.category()).orElseThrow());
		}

		@Override
		public Properties fromPacket(PacketByteBuf buf) {
			return new Properties(this, NeoApoliRegistries.ACTION_CATEGORY.getValueOrThrow(buf.readRegistryKey(NeoApoliRegistryKeys.ACTION_CATEGORY)));
		}

		@Override
		public void writeJson(Properties properties, JsonObject json) {
			json.addProperty("category", Objects.requireNonNull(NeoApoliRegistries.ACTION_CATEGORY.getId(properties.category())).toString());
		}

		@Override
		public Properties getArgumentTypeProperties(ActionArgumentType argumentType) {
			return new Properties(this, argumentType.category());
		}

		public record Properties(Serializer serializer, ActionCategory<?> category) implements ArgumentTypeProperties<ActionArgumentType> {

			@Override
			public ActionArgumentType createType(CommandRegistryAccess registryAccess) {
				return new ActionArgumentType(registryAccess, category());
			}

			@Override
			public Serializer getSerializer() {
				return serializer();
			}

		}

	}

}
