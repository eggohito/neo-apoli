package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.meta.number.ClampedNumberProvider;
import io.github.eggohito.neo_apoli.provider.meta.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.Function;

public abstract class NumberProvider extends ValueProvider<Number> {

	public static final String TYPE_KEY = "type";
	public static final PacketCodec<RegistryByteBuf, NumberProvider> PACKET_CODEC = NumberProviderTypes.PACKET_CODEC.dispatch(NumberProvider::getType, NumberProviderType::packetCodec);

	public static final MapCodec<NumberProvider> MAP_CODEC = NumberProviderTypes.CODEC.dispatchMap(TYPE_KEY, NumberProvider::getType, NumberProviderType::mapCodec);
	public static final Codec<NumberProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(MAP_CODEC.codec(), ConstantNumberProvider.INLINE_CODEC));

	@Override
	public abstract NumberProviderType<?> getType();

	@Override
	public final Number next(Context context) {
		return provideValue("numeric", context, this::impl, () -> 0.0D);
	}

	public final double nextDouble(Context context) {
		return provideValue("double", context, this::doubleImpl, () -> 0.0D);
	}

	public final float nextFloat(Context context) {
		return provideValue("float", context, this::floatImpl, () -> 0.0F);
	}

	public final long nextLong(Context context) {
		return provideValue("long", context, this::longImpl, () -> 0L);
	}

	public final int nextInt(Context context) {
		return provideValue("integer", context, this::intImpl, () -> 0);
	}

	protected abstract Number impl(Context context);

	protected double doubleImpl(Context context) {
		return this.impl(context).doubleValue();
	}

	protected float floatImpl(Context context) {
		return this.impl(context).floatValue();
	}

	protected long longImpl(Context context) {
		return Math.round(this.doubleImpl(context));
	}

	protected int intImpl(Context context) {
		return (int) this.longImpl(context);
	}

	@Override
	public String asDisplayString() {
		return "Number provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.NUMBER_PROVIDER_TYPE, this.getType()) + "\"";
	}

	public static Codec<NumberProvider> clamped(Number min, Number max) {
		return clamped(new ConstantNumberProvider(min), new ConstantNumberProvider(max));
	}

	public static Codec<NumberProvider> clamped(NumberProvider min, NumberProvider max) {
		return CODEC.xmap(value -> new ClampedNumberProvider(value, min, max), Function.identity());
	}

}
