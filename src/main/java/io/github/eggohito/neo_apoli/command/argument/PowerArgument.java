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
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public record PowerArgument(boolean allowTags) implements ArgumentType<PowerArgument.Type> {

	@Override
	public Type parse(StringReader reader) throws CommandSyntaxException {

		if (reader.canRead() && reader.peek() == '#' && allowTags()) {

			reader.skip();
			ResourceLocation id = ResourceLocation.read(reader);

			return new Type.Collection(TagKey.create(NeoApoliRegistryKeys.POWER, id));

		}

		else {
			PowerIdentifier powerId = PowerIdentifier.read(reader);
			return new Type.Singleton(powerId);
		}

	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

		if (allowTags()) {
			SharedSuggestionProvider.suggestResource(PowerManager.getTags(), builder, "#");
		}

		return SharedSuggestionProvider.suggest(PowerManager.streamIds().map(PowerIdentifier::toString), builder);

	}

	public static PowerArgument power() {
		return new PowerArgument(false);
	}

	public static PowerArgument powerOrTag() {
		return new PowerArgument(true);
	}

	public static Type getArgument(CommandContext<CommandSourceStack> context, String name) {
		return context.getArgument(name, Type.class);
	}

	public static PowerEntry<?> getPower(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		return switch (getArgument(context, name)) {
			case Type.Singleton singleton ->
				singleton.get(context).getFirst();
			case Type.Collection collection ->
				throw MiscUtil.createCommandException(() -> "Expected a power, but got tag \"#" + collection.id() + "\"");
		};
	}

	public static List<PowerEntry<?>> getTag(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		return switch (getArgument(context, name)) {
			case Type.Singleton singleton ->
				throw MiscUtil.createCommandException(() -> "Expected a tag, but got power " + singleton.id() + "!");
			case Type.Collection collection ->
				collection.get(context);
		};
	}

	public static Either<Type.Singleton, Type.Collection> getPowerOrTag(CommandContext<CommandSourceStack> context, String name) {
		return switch (getArgument(context, name)) {
			case Type.Singleton singleton ->
				Either.left(singleton);
			case Type.Collection collection ->
				Either.right(collection);
		};
	}

	public sealed interface Type permits Type.Singleton, Type.Collection {

		List<PowerEntry<?>> get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;

		record Singleton(PowerIdentifier id) implements Type {

			@Override
			public List<PowerEntry<?>> get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
				return List.of(PowerManager.getEntryAsResult(id()).getOrThrow(error -> MiscUtil.createCommandException(() -> error)));
			}

		}

		record Collection(TagKey<PowerEntry<?>> tag) implements Type {

			@Override
			public List<PowerEntry<?>> get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
				return PowerManager.getEntriesFromTag(tag()).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

			public ResourceLocation id() {
				return tag().location();
			}

		}

	}

	public enum Info implements ArgumentTypeInfo<PowerArgument, Info.Template> {

		INSTANCE;

		@Override
		public void serializeToNetwork(Template template, FriendlyByteBuf buf) {
			buf.writeBoolean(template.allowTags());
		}

		@Override
		public Template deserializeFromNetwork(FriendlyByteBuf buf) {
			return new Template(this, buf.readBoolean());
		}

		@Override
		public void serializeToJson(Template template, JsonObject jsonObject) {
			jsonObject.addProperty("allow_tags", template.allowTags());
		}

		@Override
		public Template unpack(PowerArgument argumentType) {
			return new Template(this, argumentType.allowTags());
		}

		public record Template(Info type, boolean allowTags) implements ArgumentTypeInfo.Template<PowerArgument> {

			@Override
			public PowerArgument instantiate(CommandBuildContext commandBuildContext) {
				return new io.github.eggohito.neo_apoli.command.argument.PowerArgument(allowTags());
			}

		}

	}

}
