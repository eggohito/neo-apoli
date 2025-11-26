package io.github.eggohito.neo_apoli.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.eggohito.neo_apoli.codec.ValueSuppliedElementCodec;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

public abstract class ObjectEntryArgumentType<E> implements ArgumentType<E> {

	protected final HolderLookup.Provider wrapperLookup;
	protected final ValueSuppliedElementCodec<E> codec;

	protected ObjectEntryArgumentType(HolderLookup.Provider wrapperLookup, ValueSuppliedElementCodec<E> codec) {
		this.wrapperLookup = wrapperLookup;
		this.codec = codec;
	}

	@Override
	public E parse(StringReader reader) throws CommandSyntaxException {

		RegistryOps<Tag> ops = wrapperLookup.createSerializationContext(NbtOps.INSTANCE);
		TagParser<Tag> sNbtReader = TagParser.create(ops);

		int prevCursor = reader.getCursor();
		Tag element = sNbtReader.parseAsArgument(reader);

		if (MiscUtil.hasFinishedReading(reader)) {
			return codec.parse(ops, element).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
		}

		else {

			reader.setCursor(prevCursor);
			ResourceLocation id = ResourceLocation.readNonEmpty(reader);

			if (MiscUtil.hasFinishedReading(reader)) {
				return codec.parse(ops, ops.createString(id.toString())).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

			else {
				reader.setCursor(prevCursor);
				throw MiscUtil.createCommandExceptionWithContext(reader, Component.translatable("argument.resource_or_id.invalid"));
			}

		}

	}

}
