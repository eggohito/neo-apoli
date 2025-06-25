package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.meta.number.ClampedNumberProvider;
import io.github.eggohito.neo_apoli.provider.meta.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.Function;

public abstract class NumberProvider implements ContextAware, StringDisplayable {

	public static final String TYPE_KEY = "type";
	public static final PacketCodec<RegistryByteBuf, NumberProvider> PACKET_CODEC = NumberProviderTypes.PACKET_CODEC.dispatch(NumberProvider::getType, NumberProviderType::packetCodec);

	public static final MapCodec<NumberProvider> MAP_CODEC = NumberProviderTypes.CODEC.dispatchMap(TYPE_KEY, NumberProvider::getType, NumberProviderType::mapCodec);
	public static final Codec<NumberProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(MAP_CODEC.codec(), ConstantNumberProvider.INLINE_CODEC));

	public abstract NumberProviderType<?> getType();

	public final double doubleValue(Context context) {
		return MiscUtil.provideValue("double", context, this::doubleImpl);
	}

	protected abstract double doubleImpl(Context context);

	public final float floatValue(Context context) {
		return MiscUtil.provideValue("float", context, this::floatImpl);
	}

	protected float floatImpl(Context context) {
		return (float) this.doubleImpl(context);
	}

	public final long longValue(Context context) {
		return MiscUtil.provideValue("long", context, this::longImpl);
	}

	protected long longImpl(Context context) {
		return Math.round(this.doubleImpl(context));
	}

	public final int intValue(Context context) {
		return MiscUtil.provideValue("integer", context, this::intImpl);
	}

	protected int intImpl(Context context) {
		return (int) this.longValue(context);
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
