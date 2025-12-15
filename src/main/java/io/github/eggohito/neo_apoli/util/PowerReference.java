package io.github.eggohito.neo_apoli.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.util.context.ReferenceKey;
import io.netty.buffer.ByteBuf;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public sealed interface PowerReference extends ReferenceKey, StringDisplayable permits PowerReference.Power, PowerReference.SubPower {

	Codec<PowerReference> CODEC = PrimitiveCodec.STRING.comapFlatMap(PowerReference::ofValidated, PowerReference::toString);

	StreamCodec<ByteBuf, PowerReference> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(PowerReference::of, PowerReference::toString);

	String createTranslationKey();

	boolean isSubPower();

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
			return DataResult.success(parse(new StringReader(value)));
		}

		catch (CommandSyntaxException cse) {
			return DataResult.error(cse::getMessage);
		}

	}

	static PowerReference parse(StringReader reader) throws CommandSyntaxException {

		int startIndex = reader.getCursor();
		while (reader.canRead() && isAllowed(reader.peek())) {
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

					Pair<String, String> namespaceAndPath = DynamicResourceLocation.split(parent);
					ResourceLocation parentId = DynamicResourceLocation.of(namespaceAndPath.getFirst(), namespaceAndPath.getSecond());

					return ofSubPower(ResourceLocationUtil.nonEmpty(parentId), name);

				}

				catch (ResourceLocationException iie) {
					throw MiscUtil.createCommandExceptionWithContext(reader, iie::getMessage);
				}

			}

		}

		else {

			try {

				Pair<String, String> namespaceAndPath = DynamicResourceLocation.split(value);
				ResourceLocation id = DynamicResourceLocation.of(namespaceAndPath.getFirst(), namespaceAndPath.getSecond());

				return ofPower(ResourceLocationUtil.nonEmpty(id));

			}

			catch (ResourceLocationException iie) {
				throw MiscUtil.createCommandExceptionWithContext(reader, iie::getMessage);
			}

		}

	}

	static boolean isAllowed(char ch) {
		return ch == SubPower.SEPARATOR
			|| DynamicResourceLocation.isAllowed(ch);
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
