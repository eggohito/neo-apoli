package io.github.eggohito.neo_apoli.util.modifier;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.BiConsumer;

public abstract class SimplePhasedModifier extends SimpleModifier {

	private final Phase phase;

	public SimplePhasedModifier(NumberProvider value, int order, Phase phase) {
		super(value, order);
		this.phase = phase;
	}

	@Override
	public abstract ModifierType<?> getType();

	@Override
	public Phase getPhase() {
		return phase;
	}

	protected static <M extends SimplePhasedModifier> Products.P3<RecordCodecBuilder.Mu<M>, NumberProvider, Integer, Phase> addCommonAndPhaseFields(RecordCodecBuilder.Instance<M> instance, int defaultOrder) {
		return addCommonFields(instance, defaultOrder).and(Phase.CODEC.fieldOf("phase").forGetter(SimpleModifier::getPhase));
	}

	protected static <M extends SimplePhasedModifier> MapCodec<M> simplePhasedCommonCodec(int defaultOrder, Function3<NumberProvider, Integer, Phase, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> addCommonAndPhaseFields(instance, defaultOrder).apply(instance, constructor));
	}

	protected static <M extends SimplePhasedModifier> PacketCodec<RegistryByteBuf, M> createPhasedCommonPacketCodec(BiConsumer<RegistryByteBuf, M> encoder, Function4<RegistryByteBuf, NumberProvider, Integer, Phase, M> decoder) {
		return createCommonPacketCodec(
			(buf, m) -> {
				Phase.PACKET_CODEC.encode(buf, m.getPhase());
				encoder.accept(buf, m);
			},
			(buf, numberProvider, order) -> {
				Phase phase = Phase.PACKET_CODEC.decode(buf);
				return decoder.apply(buf, numberProvider, order, phase);
			}
		);
	}

	protected static <M extends SimplePhasedModifier> PacketCodec<RegistryByteBuf, M> simplePhasedCommonPacketCodec(Function3<NumberProvider, Integer, Phase, M> constructor) {
		return createPhasedCommonPacketCodec((buf, m) -> {}, (buf, numberProvider, order, phase) -> constructor.apply(numberProvider, order, phase));
	}

}
