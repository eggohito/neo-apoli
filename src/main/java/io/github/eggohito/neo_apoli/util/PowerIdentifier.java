package io.github.eggohito.neo_apoli.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.github.eggohito.neo_apoli.power.MultiplePower;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class PowerIdentifier {

	public static final Codec<PowerIdentifier> CODEC = PrimitiveCodec.STRING.comapFlatMap(PowerIdentifier::validate, PowerIdentifier::toString);
	public static final PacketCodec<ByteBuf, PowerIdentifier> PACKET_CODEC = PacketCodecs.STRING.xmap(PowerIdentifier::of, PowerIdentifier::toString);

	private static final String SUB_POWER_SEPARATOR = "@";

	private final Identifier id;
	private final String subName;

	PowerIdentifier(Identifier id, String subName) {
		this.id = id;
		this.subName = subName;
	}

	@Override
	public String toString() {
		return id + (subName.isEmpty() ? "" : SUB_POWER_SEPARATOR) + subName;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		else if (obj instanceof PowerIdentifier that) {
			return Objects.equals(this.toString(), that.toString());
		}

		else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return Objects.hash(id, subName);
	}

	public static PowerIdentifier of(String value) {

		try {
			return parse(new StringReader(value));
		}

		catch (CommandSyntaxException cse) {
			throw new InvalidIdentifierException(cse.getMessage());
		}

	}

	public static PowerIdentifier parse(StringReader reader) throws CommandSyntaxException {

		int prevCursor = reader.getCursor();
		while (reader.canRead() && (String.valueOf(reader.peek()).equals(SUB_POWER_SEPARATOR) || Identifier.isCharValid(reader.peek()))) {
			reader.skip();
		}

		String value = reader.getString().substring(prevCursor, reader.getCursor());
		int separatorIndex = value.indexOf(SUB_POWER_SEPARATOR);

		if (value.isEmpty()) {
			throw MiscUtil.PASSTHROUGH_COMMAND_EXCEPTION_TYPE.create("Power identifier cannot be empty!");
		}

		else if (separatorIndex >= 0) {

			int subBeginIndex = separatorIndex + 1;

			String superPowerId = value.substring(0, separatorIndex);
			String subPowerName = value.substring(subBeginIndex);

			if (superPowerId.isEmpty()) {
				reader.setCursor(prevCursor);
				throw MiscUtil.PASSTHROUGH_COMMAND_EXCEPTION_TYPE.createWithContext(reader, "Disallowed empty super-power identifier in power identifier \"" + value + "\"");
			}

			else if (subPowerName.isEmpty()) {
				reader.setCursor(prevCursor + subBeginIndex);
				throw MiscUtil.PASSTHROUGH_COMMAND_EXCEPTION_TYPE.createWithContext(reader, "Disallowed empty sub-power name in power identifier \"" + value + "\"");
			}

			else {

				try {
					return subPower(IdentifierUtil.emptyStrictSplit(superPowerId), subPowerName);
				}

				catch (InvalidIdentifierException iie) {
					throw MiscUtil.PASSTHROUGH_COMMAND_EXCEPTION_TYPE.createWithContext(reader, iie.getMessage());
				}

			}

		}

		else {

			try {
				return of(IdentifierUtil.emptyStrictSplit(value));
			}

			catch (InvalidIdentifierException iie) {
				throw MiscUtil.PASSTHROUGH_COMMAND_EXCEPTION_TYPE.createWithContext(reader, iie.getMessage());
			}

		}

	}

	public static PowerIdentifier of(Identifier id) {
		return new PowerIdentifier(id, "");
	}

	public static PowerIdentifier subPower(Identifier id, @NotNull String name) {
		return MultiplePower.validateSubPowerName(name).map(str -> new PowerIdentifier(id, str)).getOrThrow();
	}

	private static DataResult<PowerIdentifier> validate(String value) {

		try {
			return DataResult.success(of(value));
		}

		catch (Exception e) {
			return DataResult.error(() -> "Invalid power identifier: " + value + " (" + e.getMessage() + ")");
		}

	}

}
