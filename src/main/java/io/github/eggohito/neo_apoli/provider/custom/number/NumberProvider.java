package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface NumberProvider extends ValueProvider {

	Codec<NumberProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(NumberProviderType.CODEC.dispatch(NumberProvider::getType, NumberProviderType::mapCodec), ConstantNumberProvider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, NumberProvider> STREAM_CODEC = NumberProviderType.STREAM_CODEC.dispatch(NumberProvider::getType, NumberProviderType::packetCodec);

	@NotNull
	NumberProviderType<?> getType();

	double nextDouble(Context context);

	default float nextFloat(Context context) {
		return (float) this.nextDouble(context);
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

	static Codec<NumberProvider> clamped(double min, double max) {
		return clamped(new ConstantNumberProvider(min), new ConstantNumberProvider(max));
	}

}
