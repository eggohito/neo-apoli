package io.github.eggohito.neo_apoli.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionHolder;
import io.github.eggohito.neo_apoli.action.manager.ActionManager;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public record ActionArgument(HolderLookup.Provider registries, boolean allowInlineDefinitions, boolean allowTags) implements ArgumentType<ActionArgument.Result> {

	private static final SimpleCommandExceptionType INVALID_REFERENCE_INLINE_OR_TAG = new SimpleCommandExceptionType(
		Component.translatable("argument.neo-apoli.action.invalid_reference_inline_or_tag")
	);

	@Override
	public Result parse(StringReader reader) throws CommandSyntaxException {

		RegistryOps<Tag> ops = registries().createSerializationContext(NbtOps.INSTANCE);
		int prevCursor = reader.getCursor();

		try {

			if (allowInlineDefinitions()) {

				TagParser<Tag> parser = TagParser.create(ops);
				Tag tag = parser.parseAsArgument(reader);

				if (MiscUtil.hasFinishedReading(reader)) {
					return new Result.Inline(new Dynamic<>(ops, tag));
				}

			}

		}

		catch (CommandSyntaxException ignored) {

		}

		reader.setCursor(prevCursor);

		try {

			if (allowTags() && reader.canRead() && reader.peek() == '#') {

				reader.skip();
				ResourceLocation tagId = ResourceLocation.readNonEmpty(reader);

				return new Result.Collection(TagKey.create(NeoApoliRegistryKeys.ACTION, tagId));

			}

		}

		catch (CommandSyntaxException ignored) {

		}

		reader.setCursor(prevCursor);

		try {

			ResourceLocation id = ResourceLocation.readNonEmpty(reader);

			if (MiscUtil.hasFinishedReading(reader)) {
				return new Result.Reference(id);
			}

		}

		catch (CommandSyntaxException ignored) {

		}

		reader.setCursor(prevCursor);
		throw INVALID_REFERENCE_INLINE_OR_TAG.createWithContext(reader);

	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

		if (allowTags()) {
			SharedSuggestionProvider.suggestResource(ActionManager.getInstance().tags(), builder, "#");
		}

		return SharedSuggestionProvider.suggestResource(ActionManager.getInstance().keys(), builder);

	}

	public static ActionArgument id(HolderLookup.Provider registries) {
		return new ActionArgument(registries, false, false);
	}

	public static ActionArgument idOrTag(HolderLookup.Provider registries) {
		return new ActionArgument(registries, false, true);
	}

	public static ActionArgument idOrTagOrInline(HolderLookup.Provider registries) {
		return new ActionArgument(registries, true, true);
	}

	public static List<Action> getActions(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
		return context.getArgument(name, Result.class).get();
	}

	public sealed interface Result extends ObjectEntryArgument.Result {

		List<Action> get() throws CommandSyntaxException;

		record Collection(TagKey<Action> tag) implements Result {

			@Override
			public List<Action> get() throws CommandSyntaxException {
				return ActionManager.getInstance().getTagAsResult(tag().location())
					.getOrThrow(error -> MiscUtil.createCommandException(() -> error))
					.stream()
					.map(ActionHolder::valueGeneric)
					.toList();
			}

		}

		record Reference(ResourceLocation id) implements Result {

			@Override
			public List<Action> get() throws CommandSyntaxException {
				return ActionManager.getInstance().getAsResult(id())
					.map(ActionHolder::valueGeneric)
					.map(List::of)
					.getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

		}

		record Inline(Dynamic<Tag> packed) implements Result {

			@Override
			public List<Action> get() throws CommandSyntaxException {
				return Action.CODEC.parse(packed())
					.map(List::of)
					.getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

		}

	}

	public enum Info implements ArgumentTypeInfo<ActionArgument, Template> {

		INSTANCE;

		@Override
		public void serializeToNetwork(ActionArgument.Template template, FriendlyByteBuf buffer) {
			buffer.writeBoolean(template.allowInlineDefinitions());
			buffer.writeBoolean(template.allowTags());
		}

		@Override
		public ActionArgument.@NotNull Template deserializeFromNetwork(FriendlyByteBuf buffer) {
			return new ActionArgument.Template(this, buffer.readBoolean(), buffer.readBoolean());
		}

		@Override
		public void serializeToJson(ActionArgument.Template template, JsonObject json) {
			json.addProperty("allow_inline_definitions", template.allowInlineDefinitions());
			json.addProperty("allow_tags", template.allowTags());
		}

		@Override
		public ActionArgument.@NotNull Template unpack(ActionArgument argument) {
			return new ActionArgument.Template(this, argument.allowInlineDefinitions(), argument.allowTags());
		}

	}

	public record Template(Info type, boolean allowInlineDefinitions, boolean allowTags) implements ArgumentTypeInfo.Template<ActionArgument> {

		@Override
		public @NotNull ActionArgument instantiate(CommandBuildContext context) {
			return new ActionArgument(context, allowInlineDefinitions(), allowTags());
		}

	}

}
