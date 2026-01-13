package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.event.PowerParsingEvents;
import io.github.eggohito.neo_apoli.util.ComponentUtil;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record PowerEntry<P extends Power>(PowerReference reference, P power, Component name, Component description, boolean hidden) {

	public static final String REFERENCE_KEY = "reference";

	private static final MapCodec<PowerEntry<?>> FULL_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		PowerReference.CODEC.fieldOf(REFERENCE_KEY).forGetter(PowerEntry::reference),
		Power.MAP_CODEC.forGetter(PowerEntry::power),
		ComponentSerialization.CODEC.optionalFieldOf("name", Component.empty()).forGetter(PowerEntry::name),
		ComponentSerialization.CODEC.optionalFieldOf("description", Component.empty()).forGetter(PowerEntry::description),
		Codec.BOOL.optionalFieldOf("hidden", false).forGetter(PowerEntry::hidden)
	).apply(instance, PowerEntry::new));

	public static final MapCodec<PowerEntry<?>> MAP_CODEC = FULL_MAP_CODEC.mapResult(new MapCodec.ResultFunction<>() {

		@Override
		public <T> DataResult<PowerEntry<?>> apply(DynamicOps<T> ops, MapLike<T> mapInput, DataResult<PowerEntry<?>> result) {
			return result.ifSuccess(entry -> PowerParsingEvents.DECODING.invoker().decode(entry, ops, mapInput));
		}

		@Override
		public <T> RecordBuilder<T> coApply(DynamicOps<T> ops, PowerEntry<?> entry, RecordBuilder<T> prefix) {
			PowerParsingEvents.ENCODING.invoker().encode(entry, ops, prefix);
			return prefix;
		}

	});

	public static final Codec<PowerEntry<?>> CODEC = MAP_CODEC.codec();

	public static final StreamCodec<RegistryFriendlyByteBuf, PowerEntry<?>> STREAM_CODEC = StreamCodec.composite(
		PowerReference.STREAM_CODEC, PowerEntry::reference,
		Power.STREAM_CODEC, PowerEntry::power,
		ComponentSerialization.TRUSTED_STREAM_CODEC, PowerEntry::name,
		ComponentSerialization.TRUSTED_STREAM_CODEC, PowerEntry::description,
		ByteBufCodecs.BOOL, PowerEntry::hidden,
		PowerEntry::new
	);

	public PowerEntry {

		String translationKey = reference.createTranslationKey();

		name = ComponentUtil.forceTranslatable(translationKey + ".name", name);
		description = ComponentUtil.forceTranslatable(translationKey + ".description", description);
		hidden = hidden || reference.isSubPower();

	}

	public Context.Validator createValidator() {
		return new Context.Validator()
			.withKeySet(power().getType().keySet())
			.forChildWithReference("{\"" + reference() + "\"}", reference());
	}

	public boolean canBePartiallyParsed() {
		return power().canBePartiallyParsed();
	}

	public boolean isSubPower() {
		return reference().isSubPower();
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		else if (obj instanceof PowerEntry<?> that) {
			return Objects.equals(this.reference(), that.reference());
		}

		else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.reference());
	}

}
