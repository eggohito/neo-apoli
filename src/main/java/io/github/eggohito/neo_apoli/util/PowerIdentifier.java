package io.github.eggohito.neo_apoli.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.eggohito.neo_apoli.power.MultiplePower;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class PowerIdentifier {

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

		if (value.isEmpty()) {
			throw new InvalidIdentifierException("Power identifier cannot be empty!");
		}

		else if (value.contains(SUB_POWER_SEPARATOR)) {

			String[] split = value.split(SUB_POWER_SEPARATOR);

			if (split.length > 2) {
				throw new InvalidIdentifierException("Invalid amount of sub-power separators (" + SUB_POWER_SEPARATOR + ") for power identifier: " + value + " (should only have one!)");
			}

			else if (split[0].isEmpty()) {
				throw new InvalidIdentifierException("Disallowed empty super-power identifier in power identifier: " + value);
			}

			else if (split[1].isEmpty()) {
				throw new InvalidIdentifierException("Disallowed empty sub-power name in power identifier: " + value);
			}

			else {
				return subPower(IdentifierUtil.emptyStrictSplit(split[0]), split[1]);
			}

		}

		else {
			return of(IdentifierUtil.emptyStrictSplit(value));
		}

	}

	public static PowerIdentifier of(Identifier id) {
		return new PowerIdentifier(id, "");
	}

	public static PowerIdentifier subPower(Identifier id, @NotNull String name) {
		return MultiplePower.validateSubPowerName(name).map(str -> new PowerIdentifier(id, str)).getOrThrow();
	}

	public static PowerIdentifier fromCommandInput(StringReader reader) throws CommandSyntaxException {

		int prevCursor = reader.getCursor();
		while (reader.canRead() && (reader.peek() == '@' || Identifier.isCharValid(reader.peek()))) {
			reader.skip();
		}

		try {
			return of(reader.getString().substring(prevCursor, reader.getCursor()));
		}

		catch (InvalidIdentifierException iie) {
			reader.setCursor(prevCursor);
			throw MiscUtil.PASSTHROUGH_COMMAND_EXCEPTION_TYPE.createWithContext(reader, iie.getLocalizedMessage());
		}

	}

}
