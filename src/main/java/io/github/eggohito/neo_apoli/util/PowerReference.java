package io.github.eggohito.neo_apoli.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.github.eggohito.neo_apoli.power.internal.MultiplePower;
import io.github.eggohito.neo_apoli.util.context.ContextKey;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public sealed interface PowerReference extends ContextKey permits PowerReference.Power, PowerReference.SubPower {

	Codec<PowerReference> CODEC = PrimitiveCodec.STRING.comapFlatMap(PowerReference::ofValidated, PowerReference::toString);
	PacketCodec<ByteBuf, PowerReference> PACKET_CODEC = PacketCodecs.STRING.xmap(PowerReference::of, PowerReference::toString);

	default String asDisplayString(boolean capitalized) {
		String displayString = this.asDisplayString();
		return capitalized
			? StringUtils.capitalize(displayString)
			: StringUtils.uncapitalize(asDisplayString());
	}

	String asDisplayString();

	String createTranslationKey();

	boolean isSubPower();

	static PowerReference ofPower(Identifier id) {
		return new Power(id);
	}

	static PowerReference ofSubPower(Identifier parentId, String value) {
		return new SubPower(parentId, value);
	}

	static PowerReference of(String value) {
		return ofValidated(value).getOrThrow(InvalidIdentifierException::new);
	}

	static DataResult<PowerReference> ofValidated(String value) {

		try {
			return DataResult.success(parse(new StringReader(value)));
		}

		catch (CommandSyntaxException cse) {
			return DataResult.error(cse::getMessage);
		}

	}

	static PowerReference parse(StringReader reader) throws CommandSyntaxException {

		int startIndex = reader.getCursor();
		while (reader.canRead() && isValidChar(reader.peek())) {
			reader.skip();
		}

		String value = reader.getString().substring(startIndex, reader.getCursor());
		int subSeparatorIndex = value.indexOf(SubPower.SEPARATOR);

		if (value.isEmpty()) {
			throw MiscUtil.createCommandException(() -> "Power references cannot be empty!");
		}

		else if (subSeparatorIndex >= 0) {

			String parent = value.substring(0, subSeparatorIndex);
			String name = value.substring(subSeparatorIndex + 1);

			if (parent.isEmpty()) {
				reader.setCursor(startIndex);
				throw MiscUtil.createCommandExceptionWithContext(reader, () -> "Disallowed empty parent ID in power reference \"" + value + "\"");
			}

			else if (name.isEmpty()) {
				reader.setCursor(startIndex);
				throw MiscUtil.createCommandExceptionWithContext(reader, () -> "Disallowed empty sub-power name in power reference \"" + value + "\"");
			}

			else {

				try {
					return ofSubPower(IdentifierUtil.nonEmptySplit(parent), name);
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

	record Power(Identifier id) implements PowerReference {

		@Override
		public String toString() {
			return id.toString();
		}

		@Override
		public String asDisplayString() {
			return "Power \"" + id() + "\"";
		}

		@Override
		public String createTranslationKey() {
			return Util.createTranslationKey("power", id());
		}

		@Override
		public boolean isSubPower() {
			return false;
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj) {
				return true;
			}

			else if (obj instanceof Power that) {
				return Objects.equals(id(), that.id());
			}

			else {
				return false;
			}

		}

		@Override
		public int hashCode() {
			return Objects.hashCode(id());
		}

	}

	record SubPower(Identifier parentId, String name) implements PowerReference {

		public static final char SEPARATOR = '@';

		public SubPower {
			MultiplePower.validateSubPowerName(name).getOrThrow();
		}

		@Override
		public String toString() {
			return parentId() + String.valueOf(SEPARATOR) + name();
		}

		@Override
		public String asDisplayString() {
			return "Sub-power \"" + name() + "\" of power \"" + parentId() + "\"";
		}

		@Override
		public String createTranslationKey() {
			return Util.createTranslationKey("power", parentId()) + SEPARATOR + name();
		}

		@Override
		public boolean isSubPower() {
			return true;
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj) {
				return true;
			}

			else if (obj instanceof SubPower that) {
				return Objects.equals(this.parentId(), that.parentId())
					&& Objects.equals(this.name(), that.name());
			}

			else {
				return false;
			}

		}

		@Override
		public int hashCode() {
			return Objects.hash(parentId(), name());
		}

	}

}
