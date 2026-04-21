package io.github.eggohito.neo_apoli.power;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.netty.buffer.ByteBuf;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public record PowerIdentifier(ResourceLocation id, @Nullable String subName) implements StringDisplayable, ContextUser {

	public static final Codec<PowerIdentifier> CODEC = PrimitiveCodec.STRING.comapFlatMap(PowerIdentifier::parseAsResult, PowerIdentifier::toString);
	public static final StreamCodec<ByteBuf, PowerIdentifier> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(PowerIdentifier::parse, PowerIdentifier::toString);

	public static final char SEPARATOR = '@';

	@Deprecated
	public PowerIdentifier {

		ResourceLocationUtil.validateNonEmpty(id).getOrThrow();

		if (subName != null) {
			MultiplePower.validateSubPowerName(subName).getOrThrow();
		}

	}

	@Override
	public String asDisplayString() {
		return isSubPower()
			? "Sub-power \"" + subName() + "\" of power \"" + id() + "\""
			: "Power \"" + id() + "\"";
	}

	@Override
	public void validate(Context.Validator validator) {
		PowerManager.getAsResult(this).resultOrPartial(validator::reportProblem);
	}

	@Override
	public @NotNull String toString() {
		return id() + (isSubPower() ? SEPARATOR + subName() : "");
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		else if (obj instanceof PowerIdentifier(ResourceLocation thatId, String thatSubName)) {
			return Objects.equals(this.id(), thatId)
				&& Objects.equals(this.subName(), thatSubName);
		}

		else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return Objects.hash(id(), subName());
	}

	public String createTranslationKey() {
		return Util.makeDescriptionId("power", id()) + (isSubPower() ? SEPARATOR + subName() : "");
	}

	public boolean isSubPower() {
		return subName() != null;
	}

	public void validate(Context.Validator validator, Class<? extends Power> powerClass, Supplier<String> errorSupplier) {
		PowerManager.getAsResult(this)
			.map(PowerHolder::value)
			.flatMap(MiscUtil.validateType(powerClass, errorSupplier))
			.resultOrPartial(validator::reportProblem);
	}

	public static PowerIdentifier of(@NotNull ResourceLocation id) {
		return new PowerIdentifier(id, null);
	}

	public static PowerIdentifier subPower(@NotNull ResourceLocation id, @NotNull String name) {
		return new PowerIdentifier(id, name);
	}

	public static PowerIdentifier parse(String input) {

		int separatorIndex = input.indexOf(SEPARATOR);
		ResourceLocation id = separatorIndex >= 0
			? ResourceLocation.parse(input.substring(0, separatorIndex))
			: ResourceLocation.parse(input);

		return separatorIndex >= 0
			? subPower(id, input.substring(separatorIndex + 1))
			: of(id);

	}

	public static DataResult<PowerIdentifier> parseAsResult(String input) {

		try {
			return DataResult.success(parse(input));
		}

		catch (ResourceLocationException e) {
			return DataResult.error(e::getMessage);
		}

	}

	public static PowerIdentifier read(StringReader reader) throws CommandSyntaxException {

		int cursor = reader.getCursor();
		String input = readGreedily(reader);

		try {
			return parse(input);
		}

		catch (ResourceLocationException e) {
			reader.setCursor(cursor);
			throw MiscUtil.createCommandExceptionWithContext(reader, e::getMessage);
		}

	}

	public static boolean isAllowed(char ch) {
		return ch == SEPARATOR
			|| ResourceLocation.isAllowedInResourceLocation(ch)
			|| ResourceLocationUtil.isEnabledAndPlaceholder(ch);
	}

	private static String readGreedily(StringReader reader) {

		int cursor = reader.getCursor();
		while (reader.canRead() && isAllowed(reader.peek())) {
			reader.skip();
		}

		return reader.getString().substring(cursor, reader.getCursor());

	}

}
