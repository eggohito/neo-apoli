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
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public sealed abstract class PowerIdentifier permits PowerIdentifier.Power, PowerIdentifier.SubPower {

	public static final Codec<PowerIdentifier> CODEC = PrimitiveCodec.STRING.comapFlatMap(PowerIdentifier::validate, PowerIdentifier::toString);
	public static final PacketCodec<ByteBuf, PowerIdentifier> PACKET_CODEC = PacketCodecs.STRING.xmap(PowerIdentifier::of, PowerIdentifier::toString);

	public String asDisplayString(boolean capitalized) {
		return capitalized
			? asDisplayString()
			: StringUtils.uncapitalize(asDisplayString());
	}

	public abstract String asDisplayString();

	public static PowerIdentifier ofPower(Identifier id) {
		return new Power(id);
	}

	public static PowerIdentifier ofSubPower(PowerIdentifier superPowerId, String value) {
		return new SubPower(superPowerId, value);
	}

	public static PowerIdentifier of(String value) {
		return validate(value).getOrThrow(InvalidIdentifierException::new);
	}

	public static DataResult<PowerIdentifier> validate(String value) {

		try {
			return DataResult.success(parse(new StringReader(value)));
		}

		catch (CommandSyntaxException cse) {
			return DataResult.error(cse::getMessage);
		}

	}

	public static PowerIdentifier parse(StringReader reader) throws CommandSyntaxException {

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

			String power = value.substring(0, separatorIndex);
			String subPower = value.substring(subStartIndex);

			if (power.isEmpty()) {
				reader.setCursor(startIndex);
				throw MiscUtil.createCommandExceptionWithContext(reader, () -> "Disallowed empty super-power identifier in power identifier \"" + value + "\"");
			}

			else if (subPower.isEmpty()) {
				reader.setCursor(startIndex);
				throw MiscUtil.createCommandExceptionWithContext(reader, () -> "Disallowed empty sub-power identifier in power identifier \"" + value + "\"");
			}

			else {

				try {
					Identifier powerId = IdentifierUtil.nonEmptySplit(power);
					return ofSubPower(ofPower(powerId), subPower);
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

	public static boolean isValidChar(char ch) {
		return ch == SubPower.SEPARATOR
			|| Identifier.isCharValid(ch);
	}

	static final class Power extends PowerIdentifier {

		private final Identifier value;

		public Power(Identifier value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value.toString();
		}

		@Override
		public String asDisplayString() {
			return "Power \"" + value + "\"";
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

	static final class SubPower extends PowerIdentifier {

		public static final char SEPARATOR = '@';

		private final PowerIdentifier superPowerId;
		private final String value;

		public SubPower(PowerIdentifier superPowerId, String value) {

			if (superPowerId instanceof SubPower subPowerId) {
				throw new IllegalArgumentException(subPowerId + " cannot be a super-power of another sub-power!");
			}

			this.superPowerId = superPowerId;
			this.value = MultiplePower.validateSubPowerName(value).getOrThrow();

		}

		@Override
		public String toString() {
			return superPowerId + String.valueOf(SEPARATOR) + value;
		}

		@Override
		public String asDisplayString() {
			return "Sub-power \"" + value + "\" of power \"" + superPowerId + "\"";
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
