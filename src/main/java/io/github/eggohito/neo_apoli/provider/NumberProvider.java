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
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.Function;

public interface NumberProvider extends ContextAware {

	String TYPE_KEY = "type";
	PacketCodec<RegistryByteBuf, NumberProvider> PACKET_CODEC = NumberProviderTypes.PACKET_CODEC.dispatch(NumberProvider::getType, NumberProviderType::packetCodec);

	MapCodec<NumberProvider> MAP_CODEC = NumberProviderTypes.CODEC.dispatchMap(TYPE_KEY, NumberProvider::getType, NumberProviderType::mapCodec);
	Codec<NumberProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(MAP_CODEC.codec(), ConstantNumberProvider.INLINE_CODEC));

	NumberProviderType<?> getType();

	double doubleValue(Context context);

	default float floatValue(Context context) {
		return (float) doubleValue(context);
	}

	default long longValue(Context context) {
		return Math.round(this.doubleValue(context));
	}

	default int intValue(Context context) {
		return (int) longValue(context);
	}

	@Override
	default String asDisplayString() {
		return "Number provider (with type \"" + RegistryUtil.getId(NeoApoliRegistries.NUMBER_PROVIDER_TYPE, this.getType()) + "\")";
	}

	static Codec<NumberProvider> clamped(Number min, Number max) {
		return clamped(new ConstantNumberProvider(min), new ConstantNumberProvider(max));
	}

	static Codec<NumberProvider> clamped(NumberProvider min, NumberProvider max) {
		return CODEC.xmap(value -> new ClampedNumberProvider(value, min, max), Function.identity());
	}

}
