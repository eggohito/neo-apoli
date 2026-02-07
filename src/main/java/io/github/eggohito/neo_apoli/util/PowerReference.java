package io.github.eggohito.neo_apoli.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.netty.buffer.ByteBuf;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public sealed interface PowerReference extends StringDisplayable, ContextUser permits PowerReference.Power, PowerReference.SubPower {

	Codec<PowerReference> CODEC = PrimitiveCodec.STRING.comapFlatMap(PowerReference::ofValidated, PowerReference::toString);

	StreamCodec<ByteBuf, PowerReference> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(PowerReference::of, PowerReference::toString);

	String createTranslationKey();

	boolean isSubPower();

	@Override
	default void validate(Context.Validator validator) {
		PowerManager.getAsResult(this).ifError(error -> validator.reportProblem(error.message()));
	}

	static PowerReference.Power ofPower(ResourceLocation id) {
		return new Power(id);
	}

	static PowerReference.SubPower ofSubPower(ResourceLocation parentId, String value) {
		return new SubPower(parentId, value);
	}

	static PowerReference of(String value) {
		return ofValidated(value).getOrThrow(ResourceLocationException::new);
	}

	static DataResult<PowerReference> ofValidated(String value) {

		try {
			return DataResult.success(parse(value));
		}

		catch (Exception e) {
			return DataResult.error(e::getMessage);
		}

	}

	static PowerReference parse(String value) {

		int separatorIndex = value.indexOf(SubPower.SEPARATOR);

		if (separatorIndex >= 0) {

			String parent = value.substring(0, separatorIndex);
			String name = value.substring(separatorIndex + 1);

			ResourceLocation parentId = ResourceLocation.parse(parent);
			return ofSubPower(ResourceLocationUtil.nonEmpty(parentId), name);

		}

		else {

			ResourceLocation id = ResourceLocation.parse(value);

			return ofPower(id);

		}

	}

	static PowerReference read(StringReader reader) throws CommandSyntaxException {

		int cursor = reader.getCursor();
		String value = readGreedy(reader);

		try {
			return parse(value);
		}

		catch (ResourceLocationException rle) {
			reader.setCursor(cursor);
			throw MiscUtil.createCommandExceptionWithContext(reader, rle::getMessage);
		}

	}

	static String readGreedy(StringReader reader) {

		int cursor = reader.getCursor();
		while (reader.canRead() && isAllowed(reader.peek())) {
			reader.skip();
		}

		return reader.getString().substring(cursor, reader.getCursor());

	}

	static boolean isAllowed(char ch) {
		return ch == SubPower.SEPARATOR
			|| ResourceLocation.isAllowedInResourceLocation(ch)
			|| ResourceLocationUtil.isEnabledAndPlaceholder(ch);
	}

	record Power(ResourceLocation id) implements PowerReference {

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
			return Util.makeDescriptionId("power", id());
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

	record SubPower(ResourceLocation parentId, String name) implements PowerReference {

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
			return Util.makeDescriptionId("power", parentId()) + SEPARATOR + name();
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
