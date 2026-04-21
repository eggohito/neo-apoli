package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.event.PowerParsingEvents;
import io.github.eggohito.neo_apoli.util.ComponentUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record PowerHolder<P extends Power>(PowerIdentifier id, P value, Component name, Component description, boolean hidden) {

	public static final String ID_KEY = "id";

	private static final MapCodec<PowerHolder<?>> FULL_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		PowerIdentifier.CODEC.fieldOf(ID_KEY).forGetter(PowerHolder::id),
		Power.MAP_CODEC.forGetter(PowerHolder::value),
		ComponentSerialization.CODEC.optionalFieldOf("name", Component.empty()).forGetter(PowerHolder::name),
		ComponentSerialization.CODEC.optionalFieldOf("description", Component.empty()).forGetter(PowerHolder::description),
		Codec.BOOL.optionalFieldOf("hidden", false).forGetter(PowerHolder::hidden)
	).apply(instance, PowerHolder::new));

	public static final MapCodec<PowerHolder<?>> MAP_CODEC = FULL_MAP_CODEC.mapResult(new MapCodec.ResultFunction<>() {

		@Override
		public <T> DataResult<PowerHolder<?>> apply(DynamicOps<T> ops, MapLike<T> mapInput, DataResult<PowerHolder<?>> result) {
			return result.ifSuccess(entry -> PowerParsingEvents.DECODING.invoker().decode(entry, ops, mapInput));
		}

		@Override
		public <T> RecordBuilder<T> coApply(DynamicOps<T> ops, PowerHolder<?> powerHolder, RecordBuilder<T> prefix) {
			PowerParsingEvents.ENCODING.invoker().encode(powerHolder, ops, prefix);
			return prefix;
		}

	});

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

		name = ComponentUtil.forceTranslatable(translationKey + ".name", name);
		description = ComponentUtil.forceTranslatable(translationKey + ".description", description);
		hidden = hidden || id.isSubPower();

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

}
