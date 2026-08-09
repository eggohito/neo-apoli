package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.event.PowerParsingEvents;
import io.github.eggohito.neo_apoli.util.ComponentUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PowerHolder<P extends Power>(PowerIdentifier id, P value, Component name, Component description, boolean hidden) {

	public static final String ID_KEY = "id";

	public static final MapCodec<PowerHolder<?>> MAP_CODEC = mapCodec(Power.MAP_CODEC);
	public static final Codec<PowerHolder<?>> CODEC = MAP_CODEC.codec();

	public static final StreamCodec<RegistryFriendlyByteBuf, PowerHolder<?>> STREAM_CODEC = StreamCodec.composite(
		PowerIdentifier.STREAM_CODEC, PowerHolder::id,
		Power.STREAM_CODEC, PowerHolder::value,
		ComponentSerialization.TRUSTED_STREAM_CODEC, PowerHolder::name,
		ComponentSerialization.TRUSTED_STREAM_CODEC, PowerHolder::description,
		ByteBufCodecs.BOOL, PowerHolder::hidden,
		PowerHolder::new
	);

	public PowerHolder {

		String translationKey = id.createTranslationKey();

		name = ComponentUtil.translatable(translationKey + ".name", name);
		description = ComponentUtil.translatable(translationKey + ".description", description);
		hidden = hidden || id.isSubPower();

	}

	public boolean canBePartiallyParsed() {
		return value().canBePartiallyParsed();
	}

	public boolean isSubPower() {
		return id().isSubPower();
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		else if (obj instanceof PowerHolder<?> that) {
			return Objects.equals(this.id(), that.id());
		}

		else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id());
	}

	public static MapCodec<PowerHolder<?>> mapCodecWithId(ResourceLocation id, MapCodec<Power> powerCodec) {

		MapCodec<PowerHolder<?>> direct = RecordCodecBuilder.mapCodec(instance -> instance.group(
			powerCodec.forGetter(PowerHolder::value),
			ComponentSerialization.CODEC.optionalFieldOf("name", Component.empty()).forGetter(PowerHolder::name),
			ComponentSerialization.CODEC.optionalFieldOf("description", Component.empty()).forGetter(PowerHolder::description),
			Codec.BOOL.optionalFieldOf("hidden", false).forGetter(PowerHolder::hidden)
		).apply(instance, (power, name, description, hidden) -> new PowerHolder<>(PowerIdentifier.of(id), power, name, description, hidden)));

		return direct.mapResult(PowerParsingEvents.RESULT_MAPPER);

	}

	public static MapCodec<PowerHolder<?>> mapCodec(MapCodec<Power> powerCodec) {

		MapCodec<PowerHolder<?>> direct = RecordCodecBuilder.mapCodec(instance -> instance.group(
			PowerIdentifier.CODEC.fieldOf(ID_KEY).forGetter(PowerHolder::id),
			powerCodec.forGetter(PowerHolder::value),
			ComponentSerialization.CODEC.optionalFieldOf("name", Component.empty()).forGetter(PowerHolder::name),
			ComponentSerialization.CODEC.optionalFieldOf("description", Component.empty()).forGetter(PowerHolder::description),
			Codec.BOOL.optionalFieldOf("hidden", false).forGetter(PowerHolder::hidden)
		).apply(instance, PowerHolder::new));

		return direct.mapResult(PowerParsingEvents.RESULT_MAPPER);

	}

}
