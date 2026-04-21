package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.util.PrimitiveNumberType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface NumberProvider extends ValueProvider {

	Codec<NumberProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(NumberProviderType.CODEC.dispatch(NumberProvider::getType, NumberProviderType::mapCodec), ConstantNumberProvider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, NumberProvider> STREAM_CODEC = NumberProviderType.STREAM_CODEC.dispatch(NumberProvider::getType, NumberProviderType::streamCodec);

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

	default short nextShort(Context context) {
		return (short) this.nextInt(context);
	}

	default byte nextByte(Context context) {
		return (byte) this.nextInt(context);
	}

	default Number next(PrimitiveNumberType type, Context context) {
		return switch (type) {
			case DOUBLE ->
				this.nextDouble(context);
			case FLOAT ->
				this.nextFloat(context);
			case LONG ->
				this.nextLong(context);
			case INT ->
				this.nextInt(context);
			case SHORT ->
				this.nextShort(context);
			case BYTE ->
				this.nextByte(context);
		};
	}

	static Codec<NumberProvider> clamped(NumberProvider min, NumberProvider max) {
		return CODEC.xmap(value -> new ClampedNumberProvider(value, min, max), Function.identity());
	}

	static Codec<NumberProvider> clamped(double min, double max) {
		return clamped(new ConstantNumberProvider(min), new ConstantNumberProvider(max));
	}

}
