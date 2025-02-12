package io.github.eggohito.neo_apoli.power;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.network.codec.PowerPacketDecoder;
import io.github.eggohito.neo_apoli.network.codec.PowerPacketEncoder;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.Validatable;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public abstract class Power implements Validatable {

	public static final String TYPE_KEY = "type";

	public static final Codec<Power> BASE_CODEC = NeoApoliRegistries.POWER_TYPE.getCodec().dispatch(TYPE_KEY, Power::getType, PowerType::mapCodec);
	public static final Codec<RegistryEntry<Power>> ENTRY_CODEC = RegistryElementCodec.of(NeoApoliRegistryKeys.POWER, BASE_CODEC);

	private final Metadata metadata;

	public Power(Metadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public void validate(RegistryWrapper.WrapperLookup wrapperLookup) {

	}

	public abstract PowerType<? extends Power> getType();

	public Metadata getMetadata() {
		return metadata;
	}

	public Text getName() {
		return getMetadata().name();
	}

	public Text getDescription() {
		return getMetadata().description();
	}

	public boolean isHidden() {
		return getMetadata().hidden();
	}

	protected static <P extends Power> Products.P1<RecordCodecBuilder.Mu<P>, Metadata> addCommonFields(RecordCodecBuilder.Instance<P> instance) {
		return instance.group(Metadata.CODEC.forGetter(Power::getMetadata));
	}

	protected static <P extends Power> PacketCodec<RegistryByteBuf, P> createCommonPacketCodec(PowerPacketEncoder<P> encoder, PowerPacketDecoder<P> decoder) {
		return new PacketCodec<>() {

			@Override
			public P decode(RegistryByteBuf buf) {
				Metadata metadata = Metadata.PACKET_CODEC.decode(buf);
				return decoder.decode(buf, metadata);
			}

			@Override
			public void encode(RegistryByteBuf buf, P value) {
				Metadata.PACKET_CODEC.encode(buf, value.getMetadata());
				encoder.encode(buf, value);
			}

		};
	}

	public record Metadata(Text name, Text description, boolean hidden) {

		public static final MapCodec<Metadata> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			TextCodecs.CODEC.optionalFieldOf("name", Text.empty()).forGetter(Metadata::name),
			TextCodecs.CODEC.optionalFieldOf("description", Text.empty()).forGetter(Metadata::description),
			PrimitiveCodec.BOOL.optionalFieldOf("hidden", false).forGetter(Metadata::hidden)
		).apply(instance, Metadata::new));

		public static final PacketCodec<RegistryByteBuf, Metadata> PACKET_CODEC = PacketCodec.tuple(
			TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, Metadata::name,
			TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, Metadata::description,
			PacketCodecs.BOOLEAN, Metadata::hidden,
			Metadata::new
		);

	}

	public interface DataContainer {
		Codec<? extends DataContainer> codec();
	}

	public interface Builder {
		Power build();
	}

}
