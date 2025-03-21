package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.event.PowerParsingEvents;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Objects;
import java.util.stream.Stream;

public record PowerEntry<P extends Power>(PowerReference reference, P value) {

	public static final String REFERENCE_KEY = "reference";
	public static final String VALUE_KEY = "value";

	private static final MapCodec<PowerEntry<?>> BASE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		PowerReference.CODEC.fieldOf(REFERENCE_KEY).forGetter(PowerEntry::reference),
		Power.BASE_CODEC.fieldOf(VALUE_KEY).forGetter(PowerEntry::value)
	).apply(instance, PowerEntry::new));

	private static final MapCodec<PowerEntry<?>> MAP_CODEC = new MapCodec<>() {

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return BASE_CODEC.keys(ops);
		}

		@Override
		public <T> DataResult<PowerEntry<?>> decode(DynamicOps<T> ops, MapLike<T> mapInput) {

			DataResult<PowerReference> powerReferenceResult = PowerReference.CODEC.fieldOf(REFERENCE_KEY).decode(ops, mapInput);
			DataResult<MapLike<T>> powerInputResult = ops.getMap(mapInput.get(VALUE_KEY));

			DataResult<PowerType<?>> powerTypeResult = powerInputResult.flatMap(powerInput -> PowerTypes.CODEC.parse(ops, powerInput.get(Power.TYPE_KEY)));

			return powerReferenceResult
				.flatMap(powerReference -> powerInputResult
					.flatMap(powerInput -> powerTypeResult
						.flatMap(powerType -> PowerParsingEvents.DECODING.invoker().decode(powerReference, powerType, ops, powerInput))
						.map(power -> new PowerEntry<>(powerReference, power))));

		}

		@Override
		public <T> RecordBuilder<T> encode(PowerEntry<?> entry, DynamicOps<T> ops, RecordBuilder<T> prefix) {

			RecordBuilder<T> powerBuilder = Power.MAP_CODEC.encode(entry.value(), ops, ops.mapBuilder());
			PowerParsingEvents.ENCODING.invoker().encode(entry, ops, powerBuilder);

			return prefix
				.add(REFERENCE_KEY, PowerReference.CODEC.encodeStart(ops, entry.reference()))
				.add(VALUE_KEY, powerBuilder.build(ops.empty()));

		}

	};

	public static final Codec<PowerEntry<?>> CODEC = MAP_CODEC.codec();
	public static final PacketCodec<RegistryByteBuf, PowerEntry<?>> PACKET_CODEC = PacketCodec.tuple(
		PowerReference.PACKET_CODEC, PowerEntry::reference,
		Power.BASE_PACKET_CODEC, PowerEntry::value,
		PowerEntry::new
	);

	public boolean isSubPower() {
		return reference() instanceof PowerReference.SubPower;
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
