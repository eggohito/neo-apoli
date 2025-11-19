package io.github.eggohito.neo_apoli.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PowerReference;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public record PowerArgumentType(boolean allowTags) implements ArgumentType<PowerArgumentType.PowerArgument> {

	@Override
	public PowerArgument parse(StringReader reader) throws CommandSyntaxException {

		if (reader.canRead() && reader.peek() == '#' && allowTags()) {

			reader.skip();
			Identifier id = Identifier.fromCommandInput(reader);

			return new Tag(id);

		}

		else {
			PowerReference reference = PowerReference.parse(reader);
			return new Power(reference);
		}

	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

		if (allowTags()) {
			CommandSource.suggestIdentifiers(PowerManager.getTags(), builder, "#");
		}

		return CommandSource.suggestMatching(PowerManager.streamReferences().map(PowerReference::toString), builder);

	}

	public static PowerArgumentType power() {
		return new PowerArgumentType(false);
	}

	public static PowerArgumentType powerOrTag() {
		return new PowerArgumentType(true);
	}

	public static PowerArgument getArgument(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, PowerArgument.class);
	}

	public static PowerEntry<?> getPower(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return switch (getArgument(context, name)) {
			case Power power ->
				power.get(context).getFirst();
			case Tag tag ->
				throw MiscUtil.createCommandException(() -> "Expected a power, but got power tag with ID \"" + tag.id() + "\"!");
		};
	}

	public static List<PowerEntry<?>> getTag(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return switch (getArgument(context, name)) {
			case Power power ->
				throw MiscUtil.createCommandException(() -> "Expected a tag, but got " + power.reference().asDisplayString(false) + "!");
			case Tag tag ->
				tag.get(context);
		};
	}

	public static Either<Power, Tag> getPowerOrTag(CommandContext<ServerCommandSource> context, String name) {
		return switch (getArgument(context, name)) {
			case Power power ->
				Either.left(power);
			case Tag tag ->
				Either.right(tag);
		};
	}

	public sealed interface PowerArgument permits Power, Tag {

		List<PowerEntry<?>> get(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;

	}

	public record Power(PowerReference reference) implements PowerArgument {

		@Override
		public List<PowerEntry<?>> get(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			return List.of(PowerManager.getEntryAsResult(reference()).getOrThrow(error -> MiscUtil.createCommandException(() -> error)));
		}

	}

	public record Tag(Identifier id) implements PowerArgument {

		@Override
		public List<PowerEntry<?>> get(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			return PowerManager.getEntriesFromTag(id()).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
		}

	}

	public record Serializer() implements ArgumentSerializer<PowerArgumentType, Serializer.Properties> {

		@Override
		public void writePacket(Properties properties, PacketByteBuf buf) {
			buf.writeBoolean(properties.allowTags());
		}

		@Override
		public Properties fromPacket(PacketByteBuf buf) {
			return new Properties(buf.readBoolean(), this);
		}

		@Override
		public void writeJson(Properties properties, JsonObject json) {
			json.addProperty("allow_tags", properties.allowTags());
		}

		@Override
		public Properties getArgumentTypeProperties(PowerArgumentType argumentType) {
			return new Properties(argumentType.allowTags(), this);
		}

		public record Properties(boolean allowTags, Serializer getSerializer) implements ArgumentTypeProperties<PowerArgumentType> {

			@Override
			public PowerArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
				return new PowerArgumentType(allowTags());
			}

		}

	}

}
