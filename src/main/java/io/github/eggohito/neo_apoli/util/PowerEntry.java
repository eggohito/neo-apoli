package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.*;
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

	private static final MapCodec<PowerReference> REFERENCE_MAP_CODEC = PowerReference.CODEC.fieldOf(REFERENCE_KEY);
	private static final MapCodec<PowerType<?>> POWER_TYPE_MAP_CODEC = PowerTypes.CODEC.fieldOf(Power.TYPE_KEY);

	public static final MapCodec<PowerEntry<?>> MAP_CODEC = new MapCodec<>() {

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return Stream.of(REFERENCE_KEY, VALUE_KEY).map(ops::createString);
		}

		@Override
		public <T> DataResult<PowerEntry<?>> decode(DynamicOps<T> ops, MapLike<T> mapInput) {
			return REFERENCE_MAP_CODEC.decode(ops, mapInput)
				.flatMap(reference -> ops.getMap(mapInput.get(VALUE_KEY))
					.flatMap(input -> POWER_TYPE_MAP_CODEC.decode(ops, input)
						.flatMap(type -> Objects.requireNonNullElseGet(PowerParsingEvents.DECODING.invoker().decode(reference, type, ops, input), () -> type.mapCodec().decode(ops, input))
							.map(power -> new PowerEntry<>(reference, power)))));
		}

		@Override
		public <T> RecordBuilder<T> encode(PowerEntry<?> entry, DynamicOps<T> ops, RecordBuilder<T> prefix) {

			RecordBuilder<T> powerBuilder = Power.BASE_MAP_CODEC.encode(entry.value(), ops, ops.mapBuilder());
			PowerParsingEvents.ENCODING.invoker().encode(entry.reference(), entry.value(), ops, powerBuilder);

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
