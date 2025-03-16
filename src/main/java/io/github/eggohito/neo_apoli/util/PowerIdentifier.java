package io.github.eggohito.neo_apoli.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.StringIdentifiable;

import java.util.Objects;

public sealed interface PowerIdentifier extends StringIdentifiable permits PowerIdentifier.Power, PowerIdentifier.SubPower {

	Codec<PowerIdentifier> CODEC = PrimitiveCodec.STRING.comapFlatMap(PowerIdentifier::validate, PowerIdentifier::asString);
	PacketCodec<ByteBuf, PowerIdentifier> PACKET_CODEC = PacketCodecs.STRING.xmap(PowerIdentifier::of, PowerIdentifier::asString);

	static PowerIdentifier ofPower(Identifier id) {
		return new Power(id);
	}

	static PowerIdentifier ofSubPower(Identifier superPowerId, String value) {
		return new SubPower(superPowerId, value);
	}

	static PowerIdentifier of(String value) {
		return validate(value).getOrThrow(InvalidIdentifierException::new);
	}

	static DataResult<PowerIdentifier> validate(String value) {

		try {
			return DataResult.success(parse(new StringReader(value)));
		}

		catch (CommandSyntaxException cse) {
			return DataResult.error(cse::getMessage);
		}

	}

	static PowerIdentifier parse(StringReader reader) throws CommandSyntaxException {

		int startIndex = reader.getCursor();
		while (reader.canRead() && isValidChar(reader.peek())) {
			reader.skip();
		}

		String value = reader.getString().substring(startIndex, reader.getCursor());
		int separatorIndex = value.indexOf(SubPower.SEPARATOR);

		if (value.isEmpty()) {
			throw MiscUtil.createCommandException(() -> "Power identifier cannot be empty!");
		}

		else if (separatorIndex >= 0) {

			int subStartIndex = separatorIndex + 1;

			String superPowerId = value.substring(0, separatorIndex);
			String subPowerId = value.substring(subStartIndex);

			if (superPowerId.isEmpty()) {
				reader.setCursor(startIndex);
				throw MiscUtil.createCommandExceptionWithContext(reader, () -> "Disallowed empty super-power identifier in power identifier \"" + value + "\"");
			}

			else if (subPowerId.isEmpty()) {
				reader.setCursor(startIndex);
				throw MiscUtil.createCommandExceptionWithContext(reader, () -> "Disallowed empty sub-power identifier in power identifier \"" + value + "\"");
			}

			else {

				try {
					return ofSubPower(IdentifierUtil.nonEmptySplit(superPowerId), subPowerId);
				}

				catch (InvalidIdentifierException iie) {
					throw MiscUtil.createCommandExceptionWithContext(reader, iie::getMessage);
				}

			}

		}

		else {

			try {
				return ofPower(IdentifierUtil.nonEmptySplit(value));
			}

			catch (InvalidIdentifierException iie) {
				throw MiscUtil.createCommandExceptionWithContext(reader, iie::getMessage);
			}

		}

	}

	static boolean isValidChar(char ch) {
		return ch == SubPower.SEPARATOR
			|| Identifier.isCharValid(ch);
	}

	record Power(Identifier value) implements PowerIdentifier {

		@Override
		public String asString() {
			return value().toString();
		}

		@Override
		public String toString() {
			return "Power \"" + value() + "\"";
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj) {
				return true;
			}

			else if (obj instanceof Power that) {
				return Objects.equals(value, that.value);
			}

			else {
				return false;
			}

		}

		@Override
		public int hashCode() {
			return Objects.hashCode(value);
		}

	}

	record SubPower(Identifier superPowerId, String value) implements PowerIdentifier {

		public static final char SEPARATOR = '@';

		public SubPower {
			MultiplePower.validateSubPowerName(value).getOrThrow();
		}

		@Override
		public String asString() {
			return superPowerId() + String.valueOf(SEPARATOR) + value();
		}

		@Override
		public String toString() {
			return "Sub-power \"" + value() + "\" of power \"" + superPowerId() + "\"";
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj) {
				return true;
			}

			else if (obj instanceof SubPower that) {
				return Objects.equals(this.superPowerId, that.superPowerId)
					&& Objects.equals(this.value, that.value);
			}

			else {
				return false;
			}

		}

		@Override
		public int hashCode() {
			return Objects.hash(superPowerId, value);
		}

	}

}
