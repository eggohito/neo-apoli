package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.event.PowerParsingEvents;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.TextUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.Objects;

public record PowerEntry<P extends Power>(PowerReference reference, P power, Text name, Text description, boolean hidden) {

	public static final String REFERENCE_KEY = "reference";

	private static final MapCodec<PowerEntry<?>> UNMAPPED_RESULT_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		PowerReference.CODEC.fieldOf(REFERENCE_KEY).forGetter(PowerEntry::reference),
		Power.BASE_MAP_CODEC.forGetter(PowerEntry::power),
		TextCodecs.CODEC.optionalFieldOf("name", Text.empty()).forGetter(PowerEntry::name),
		TextCodecs.CODEC.optionalFieldOf("description", Text.empty()).forGetter(PowerEntry::description),
		Codec.BOOL.optionalFieldOf("hidden", false).forGetter(PowerEntry::hidden)
	).apply(instance, PowerEntry::new));

	public static final MapCodec<PowerEntry<?>> CODEC = UNMAPPED_RESULT_CODEC.mapResult(new MapCodec.ResultFunction<>() {

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

	public static final PacketCodec<RegistryByteBuf, PowerEntry<?>> PACKET_CODEC = PacketCodec.tuple(
		PowerReference.PACKET_CODEC, PowerEntry::reference,
		Power.BASE_PACKET_CODEC, PowerEntry::power,
		TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, PowerEntry::name,
		TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, PowerEntry::description,
		PacketCodecs.BOOLEAN, PowerEntry::hidden,
		PowerEntry::new
	);

	public PowerEntry {

		String translationKey = reference.createTranslationKey();

		name = TextUtil.forceTranslatable(translationKey + ".name", name);
		description = TextUtil.forceTranslatable(translationKey + ".description", description);
		hidden = hidden || reference.subPower();

	}

	public boolean subPower() {
		return reference().subPower();
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
