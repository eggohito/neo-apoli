package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.NumberType;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface NumberProvider extends ValueProvider {

	Codec<NumberProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(Type.CODEC.dispatch(NumberProvider::getType, Type::mapCodec), ContextNumberProvider.INLINE_CODEC, ConstantNumberProvider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, NumberProvider> STREAM_CODEC = Type.STREAM_CODEC.dispatch(NumberProvider::getType, Type::streamCodec);

	@NotNull
	NumberProvider.Type<?> getType();

	double getDouble(Context context);

	default float getFloat(Context context) {
		return (float) this.getDouble(context);
	}

	default long getLong(Context context) {
		return Math.round(this.getDouble(context));
	}

	default int getInt(Context context) {
		return (int) this.getLong(context);
	}

	default short getShort(Context context) {
		return (short) this.getInt(context);
	}

	default byte getByte(Context context) {
		return (byte) this.getInt(context);
	}

	default Number getAsType(NumberType type, Context context) {
		return switch (type) {
			case DOUBLE ->
				this.getDouble(context);
			case FLOAT ->
				this.getFloat(context);
			case LONG ->
				this.getLong(context);
			case INT ->
				this.getInt(context);
			case SHORT ->
				this.getShort(context);
			case BYTE ->
				this.getByte(context);
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
