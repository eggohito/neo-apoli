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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public record PowerArgumentType(boolean allowTags) implements ArgumentType<PowerArgumentType.PowerArgument> {

	@Override
	public PowerArgument parse(StringReader reader) throws CommandSyntaxException {

		if (reader.canRead() && reader.peek() == '#' && allowTags()) {

			reader.skip();
			ResourceLocation id = ResourceLocation.read(reader);

			return new Tag(id);

		}

		else {
			PowerReference reference = PowerReference.read(reader);
			return new Power(reference);
		}

	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

		if (allowTags()) {
			SharedSuggestionProvider.suggestResource(PowerManager.getTags(), builder, "#");
		}

		return SharedSuggestionProvider.suggest(PowerManager.streamReferences().map(PowerReference::toString), builder);

	}

	public static PowerArgumentType power() {
		return new PowerArgumentType(false);
	}

	public static PowerArgumentType powerOrTag() {
		return new PowerArgumentType(true);
	}

	public static PowerArgument getArgument(CommandContext<CommandSourceStack> context, String name) {
		return context.getArgument(name, PowerArgument.class);
	}

	public static PowerEntry<?> getPower(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		return switch (getArgument(context, name)) {
			case Power power ->
				power.get(context).getFirst();
			case Tag tag ->
				throw MiscUtil.createCommandException(() -> "Expected a power, but got power tag with ID \"" + tag.id() + "\"!");
		};
	}

	public static List<PowerEntry<?>> getTag(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		return switch (getArgument(context, name)) {
			case Power power ->
				throw MiscUtil.createCommandException(() -> "Expected a tag, but got " + power.reference().asDisplayString(false) + "!");
			case Tag tag ->
				tag.get(context);
		};
	}

	public static Either<Power, Tag> getPowerOrTag(CommandContext<CommandSourceStack> context, String name) {
		return switch (getArgument(context, name)) {
			case Power power ->
				Either.left(power);
			case Tag tag ->
				Either.right(tag);
		};
	}

	public sealed interface PowerArgument permits Power, Tag {

		List<PowerEntry<?>> get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;

	}

	public record Power(PowerReference reference) implements PowerArgument {

		@Override
		public List<PowerEntry<?>> get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
			return List.of(PowerManager.getEntryAsResult(reference()).getOrThrow(error -> MiscUtil.createCommandException(() -> error)));
		}

	}

	public record Tag(ResourceLocation id) implements PowerArgument {

		@Override
		public List<PowerEntry<?>> get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
			return PowerManager.getEntriesFromTag(id()).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
		}

	}

	public record Info() implements ArgumentTypeInfo<PowerArgumentType, Info.Template> {

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
		public Template unpack(PowerArgumentType argumentType) {
			return new Template(this, argumentType.allowTags());
		}

		public record Template(Info type, boolean allowTags) implements ArgumentTypeInfo.Template<PowerArgumentType> {

			@Override
			public PowerArgumentType instantiate(CommandBuildContext commandBuildContext) {
				return new PowerArgumentType(allowTags());
			}

		}

	}

}
