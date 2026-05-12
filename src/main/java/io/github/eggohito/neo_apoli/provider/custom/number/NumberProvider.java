package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.PrimitiveNumberType;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface NumberProvider extends ValueProvider {

	Codec<NumberProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(Type.CODEC.dispatch(NumberProvider::getType, Type::mapCodec), ConstantNumberProvider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, NumberProvider> STREAM_CODEC = Type.STREAM_CODEC.dispatch(NumberProvider::getType, Type::streamCodec);

	@NotNull
	NumberProvider.Type<?> getType();

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

	record Type<P extends NumberProvider>(MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec) implements ValueProvider.Type<P> {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.NUMBER_PROVIDER_TYPE);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.NUMBER_PROVIDER_TYPE);

	}

}
