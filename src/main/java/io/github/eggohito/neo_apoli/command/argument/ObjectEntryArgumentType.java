package io.github.eggohito.neo_apoli.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.eggohito.neo_apoli.codec.ValueSuppliedElementCodec;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class ObjectEntryArgumentType<E> implements ArgumentType<E> {

	protected final RegistryWrapper.WrapperLookup wrapperLookup;
	protected final ValueSuppliedElementCodec<E> codec;

	protected ObjectEntryArgumentType(RegistryWrapper.WrapperLookup wrapperLookup, ValueSuppliedElementCodec<E> codec) {
		this.wrapperLookup = wrapperLookup;
		this.codec = codec;
	}

	@Override
	public E parse(StringReader reader) throws CommandSyntaxException {

		RegistryOps<NbtElement> ops = wrapperLookup.getOps(NbtOps.INSTANCE);
		StringNbtReader<NbtElement> sNbtReader = StringNbtReader.fromOps(ops);

		int prevCursor = reader.getCursor();
		NbtElement element = sNbtReader.readAsArgument(reader);

		if (MiscUtil.hasFinishedReading(reader)) {
			return codec.parse(ops, element).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
		}

		else {

			reader.setCursor(prevCursor);
			Identifier id = Identifier.fromCommandInputNonEmpty(reader);

			if (MiscUtil.hasFinishedReading(reader)) {
				return codec.parse(ops, ops.createString(id.toString())).getOrThrow(error -> MiscUtil.createCommandException(() -> error));
			}

			else {
				reader.setCursor(prevCursor);
				throw MiscUtil.createCommandExceptionWithContext(reader, Text.translatable("argument.resource_or_id.invalid"));
			}

		}

	}

}
