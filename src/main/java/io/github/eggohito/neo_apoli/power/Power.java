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
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
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
	public static final MapCodec<Power> MAP_CODEC = PowerTypes.CODEC.dispatchMap(TYPE_KEY, Power::getType, PowerType::mapCodec);

	public static final Codec<Power> BASE_CODEC = MAP_CODEC.codec();
	public static final PacketCodec<RegistryByteBuf, Power> BASE_PACKET_CODEC = PowerTypes.PACKET_CODEC.dispatch(Power::getType, PowerType::packetCodec);

	private final Properties properties;

	public Power(Properties properties) {
		this.properties = properties;
	}

	@Override
	public void validate(RegistryWrapper.WrapperLookup wrapperLookup) {

	}

	public abstract PowerType<? extends Power> getType();

	public void onAdded(Entity entity) {

	}

	public void onGranted(Entity entity) {

	}

	public void onRemoved(Entity entity) {

	}

	public void onRevoked(Entity entity) {

	}

	public <I> DataResult<I> encodeData(RegistryOps<I> registryOps) {
		return DataResult.success(registryOps.emptyMap());
	}

	public <I> void decodeData(RegistryOps<I> registryOps, I data) {

	}

	public Properties getProperties() {
		return properties;
	}

	public Text getName() {
		return getProperties().name();
	}

	public Text getDescription() {
		return getProperties().description();
	}

	public boolean isHidden() {
		return getProperties().hidden();
	}

	protected static <P extends Power> MapCodec<P> createSimpleCodec(Function<Properties, P> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance).apply(instance, constructor));
	}

	protected static <P extends Power> PacketCodec<RegistryByteBuf, P> createSimplePacketCodec(Function<Properties, P> constructor) {
		return createCommonPacketCodec((buf, power) -> {}, (buf, metadata) -> constructor.apply(metadata));
	}

	protected static <P extends Power> Products.P1<RecordCodecBuilder.Mu<P>, Properties> addCommonFields(RecordCodecBuilder.Instance<P> instance) {
		return instance.group(Properties.CODEC.forGetter(Power::getProperties));
	}

	protected static <P extends Power> PacketCodec<RegistryByteBuf, P> createCommonPacketCodec(PowerPacketEncoder<P> encoder, PowerPacketDecoder<P> decoder) {
		return new PacketCodec<>() {

			@Override
			public P decode(RegistryByteBuf buf) {
				Properties properties = Properties.PACKET_CODEC.decode(buf);
				return decoder.decode(buf, properties);
			}

			@Override
			public void encode(RegistryByteBuf buf, P value) {
				Properties.PACKET_CODEC.encode(buf, value.getProperties());
				encoder.encode(buf, value);
			}

		};
	}

	public record Properties(Text name, Text description, boolean hidden) {

		public static final MapCodec<Properties> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			TextCodecs.CODEC.optionalFieldOf("name", Text.empty()).forGetter(Properties::name),
			TextCodecs.CODEC.optionalFieldOf("description", Text.empty()).forGetter(Properties::description),
			PrimitiveCodec.BOOL.optionalFieldOf("hidden", false).forGetter(Properties::hidden)
		).apply(instance, Properties::new));

		public static final PacketCodec<RegistryByteBuf, Properties> PACKET_CODEC = PacketCodec.tuple(
			TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, Properties::name,
			TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, Properties::description,
			PacketCodecs.BOOLEAN, Properties::hidden,
			Properties::new
		);

	}

}
