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

public record PowerReference(ResourceLocation id, @Nullable String suffix) implements StringDisplayable, ContextUser {

	public static final Codec<PowerReference> CODEC = PrimitiveCodec.STRING.comapFlatMap(PowerReference::parseAsResult, PowerReference::toString);
	public static final StreamCodec<ByteBuf, PowerReference> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(PowerReference::parse, PowerReference::toString);

	public static final char SEPARATOR = '@';

	@Deprecated
	public PowerReference {
		id = ResourceLocationUtil.validateNonEmpty(id).getOrThrow();
		suffix = suffix != null
			? MultiplePower.validateSubPowerName(suffix).getOrThrow()
			: null;
	}

	@Override
	public String asDisplayString() {
		return isSubPower()
			? "Sub-power \"" + suffix() + "\" of power \"" + id() + "\""
			: "Power \"" + id() + "\"";
	}

	@Override
	public void validate(Context.Validator validator) {
		PowerManager.getAsResult(this).resultOrPartial(validator::reportProblem);
	}

	@Override
	public @NotNull String toString() {
		return id() + (isSubPower() ? SEPARATOR + suffix() : "");
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		else if (obj instanceof PowerReference(ResourceLocation thatId, String thatSuffix)) {
			return Objects.equals(this.id(), thatId)
				&& Objects.equals(this.suffix(), thatSuffix);
		}

		else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return Objects.hash(id(), suffix());
	}

	public String createTranslationKey() {
		return Util.makeDescriptionId("power", id()) + (isSubPower() ? SEPARATOR + suffix() : "");
	}

	public boolean isSubPower() {
		return suffix() != null;
	}

	public void validate(Context.Validator validator, Class<? extends Power> powerClass, Supplier<String> errorSupplier) {
		PowerManager.getAsResult(this)
			.flatMap(MiscUtil.validateType(powerClass, errorSupplier))
			.resultOrPartial(validator::reportProblem);
	}

	public static PowerReference of(@NotNull ResourceLocation id) {
		return new PowerReference(id, null);
	}

	public static PowerReference subPower(@NotNull ResourceLocation id, @NotNull String name) {
		return new PowerReference(id, name);
	}

	public static PowerReference parse(String input) {

		int separatorIndex = input.indexOf(SEPARATOR);
		ResourceLocation id = separatorIndex >= 0
			? ResourceLocation.parse(input.substring(0, separatorIndex))
			: ResourceLocation.parse(input);

		return separatorIndex >= 0
			? subPower(id, input.substring(separatorIndex + 1))
			: of(id);

	}

	public static DataResult<PowerReference> parseAsResult(String input) {

		try {
			return DataResult.success(parse(input));
		}

		catch (ResourceLocationException e) {
			return DataResult.error(e::getMessage);
		}

	}

	public static PowerReference read(StringReader reader) throws CommandSyntaxException {

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
