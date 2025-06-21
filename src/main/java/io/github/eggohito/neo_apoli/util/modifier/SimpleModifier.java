package io.github.eggohito.neo_apoli.util.modifier;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public abstract class SimpleModifier implements Modifier {

	private final NumberProvider valueProvider;
	private final int order;

	public SimpleModifier(NumberProvider valueProvider, int order) {
		this.valueProvider = valueProvider;
		this.order = order;
	}

	@Override
	public abstract ModifierType<?> getType();

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public double apply(Context context, double base, double total) {

		Context valueContext = context.makeChild(".value");
		double value = this.getValueProvider().doubleValue(valueContext);

		if (valueContext.hasErrors()) {
			return 0;
		}

		else {
			return this.calculate(value, base, total);
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		Modifier.super.validate(reporter);
		this.getValueProvider().validate(reporter.makeChild(".value"));
	}

	public NumberProvider getValueProvider() {
		return valueProvider;
	}

	protected abstract double calculate(double value, double base, double total);

	protected static <M extends SimpleModifier> Products.P2<RecordCodecBuilder.Mu<M>, NumberProvider, Integer> addCommonFields(RecordCodecBuilder.Instance<M> instance, int defaultOrder) {
		return instance.group(
			NumberProvider.CODEC.fieldOf("value").forGetter(SimpleModifier::getValueProvider),
			Codec.INT.optionalFieldOf("order", defaultOrder).forGetter(SimpleModifier::getOrder)
		);
	}

	protected static <M extends SimpleModifier> MapCodec<M> simpleCommonCodec(int defaultOrder, BiFunction<NumberProvider, Integer, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance, defaultOrder).apply(instance, constructor));
	}

	protected static <M extends SimpleModifier> PacketCodec<RegistryByteBuf, M> createCommonPacketCodec(BiConsumer<RegistryByteBuf, M> encoder, Function3<RegistryByteBuf, NumberProvider, Integer, M> decoder) {
		return new PacketCodec<>() {

			@Override
			public M decode(RegistryByteBuf buf) {

				NumberProvider value = NumberProvider.PACKET_CODEC.decode(buf);
				int order = buf.readVarInt();

				return decoder.apply(buf, value, order);

			}

			@Override
			public void encode(RegistryByteBuf buf, M m) {

				NumberProvider.PACKET_CODEC.encode(buf, m.getValueProvider());
				buf.writeVarInt(m.getOrder());

				encoder.accept(buf, m);

			}

		};
	}

	protected static <M extends SimpleModifier> PacketCodec<RegistryByteBuf, M> simpleCommonPacketCodec(BiFunction<NumberProvider, Integer, M> constructor) {
		return createCommonPacketCodec((buf, m) -> {}, (buf, numberProvider, order) -> constructor.apply(numberProvider, order));
	}

}
