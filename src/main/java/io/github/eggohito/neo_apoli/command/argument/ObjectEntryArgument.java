package io.github.eggohito.neo_apoli.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

public interface ObjectEntryArgument<T extends ObjectEntryArgument.Result> extends ArgumentType<T> {

	HolderLookup.Provider registries();

	boolean allowInlineDefinitions();

	T mapType(Either<Dynamic<Tag>, ResourceLocation> either);

	@Override
	default T parse(StringReader reader) throws CommandSyntaxException {

		RegistryOps<Tag> ops = registries().createSerializationContext(NbtOps.INSTANCE);
		int prevCursor = reader.getCursor();

		if (allowInlineDefinitions()) {

			TagParser<Tag> parser = TagParser.create(ops);
			Tag tag = parser.parseAsArgument(reader);

			if (MiscUtil.hasFinishedReading(reader)) {
				return this.mapType(Either.left(new Dynamic<>(ops, tag)));
			}

		}

		reader.setCursor(prevCursor);
		ResourceLocation id = ResourceLocation.readNonEmpty(reader);

		if (MiscUtil.hasFinishedReading(reader)) {
			return this.mapType(Either.right(id));
		}

		reader.setCursor(prevCursor);
		throw MiscUtil.createCommandExceptionWithContext(reader, Component.translatable("argument.resource_or_id.invalid"));

	}

	interface Result {

	}

}
