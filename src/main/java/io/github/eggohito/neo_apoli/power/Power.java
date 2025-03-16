package io.github.eggohito.neo_apoli.power;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.network.codec.PowerPacketDecoder;
import io.github.eggohito.neo_apoli.network.codec.PowerPacketEncoder;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.Validatable;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.function.Function;

public abstract class Power implements Validatable {

	public static final String TYPE_KEY = "type";

	public static final Codec<Power> BASE_CODEC = NeoApoliRegistries.POWER_TYPE.getCodec().dispatch(TYPE_KEY, Power::getType, PowerType::mapCodec);
	public static final PacketCodec<RegistryByteBuf, Power> BASE_PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.POWER_TYPE).dispatch(Power::getType, PowerType::packetCodec);

	private final Metadata metadata;

	public Power(Metadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public void validate(RegistryWrapper.WrapperLookup wrapperLookup) {

	}

	public abstract PowerType<? extends Power> getType();

	public void onAdded(Entity entity) {

	}

	public void onGained(Entity entity) {

	}

	public void onRemoved(Entity entity) {

	}

	public void onLost(Entity entity) {

	}

	public <I> DataResult<I> encodeData(RegistryOps<I> registryOps) {
		return DataResult.success(registryOps.emptyMap());
	}

	public <I> void decodeData(RegistryOps<I> registryOps, I data) {

	}

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

	protected static <P extends Power> MapCodec<P> createSimpleCodec(Function<Metadata, P> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance).apply(instance, constructor));
	}

	protected static <P extends Power> PacketCodec<RegistryByteBuf, P> createSimplePacketCodec(Function<Metadata, P> constructor) {
		return createCommonPacketCodec((buf, power) -> {}, (buf, metadata) -> constructor.apply(metadata));
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

}
