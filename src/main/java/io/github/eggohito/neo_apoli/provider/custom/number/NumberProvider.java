package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface NumberProvider extends ValueProvider<Number> {

	Codec<NumberProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(NumberProviderType.CODEC.dispatch(NumberProvider::getType, NumberProviderType::mapCodec), ConstantNumberProvider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, NumberProvider> STREAM_CODEC = NumberProviderType.STREAM_CODEC.dispatch(NumberProvider::getType, NumberProviderType::packetCodec);

	@Override
	NumberProviderType<?> getType();

	@Override
	default String asDisplayString() {
		return "Number provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.NUMBER_PROVIDER_TYPE, this.getType()) + "\"";
	}

	default double nextDouble(Context context) {
		return this.next(context).doubleValue();
	}

	default float nextFloat(Context context) {
		return this.next(context).floatValue();
	}

	default long nextLong(Context context) {
		return Math.round(this.nextDouble(context));
	}

	default int nextInt(Context context) {
		return (int) this.nextLong(context);
	}

	static Codec<NumberProvider> clamped(NumberProvider min, NumberProvider max) {
		return CODEC.xmap(value -> new ClampedNumberProvider(value, min, max), Function.identity());
	}

	static <N extends Number & Comparable<N>> Codec<NumberProvider> clamped(N min, N max) {
		return clamped(new ConstantNumberProvider(min), new ConstantNumberProvider(max));
	}

}
