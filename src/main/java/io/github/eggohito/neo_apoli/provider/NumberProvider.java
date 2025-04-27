package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.number.ClampedNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.type.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.function.Function;

public interface NumberProvider extends ValueProvider<Number> {

	String TYPE_KEY = "type";
	PacketCodec<RegistryByteBuf, NumberProvider> PACKET_CODEC = NumberProviderTypes.PACKET_CODEC.dispatch(NumberProvider::getType, Type::packetCodec);

	MapCodec<NumberProvider> MAP_CODEC = NumberProviderTypes.CODEC.dispatchMap(TYPE_KEY, NumberProvider::getType, Type::mapCodec);
	Codec<NumberProvider> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(MAP_CODEC.codec(), ConstantNumberProvider.INLINE_CODEC));

	Type<?> getType();

	@Override
	Number get(Context context);

	@Override
	default String asDisplayString() {
		return "Number provider (with type \"" + RegistryUtil.getId(NeoApoliRegistries.NUMBER_PROVIDER_TYPE, this.getType()) + "\")";
	}

	static Codec<NumberProvider> ranged(Number min, Number max) {
		return ranged(new ConstantNumberProvider(min), new ConstantNumberProvider(max));
	}

	static Codec<NumberProvider> ranged(NumberProvider min, NumberProvider max) {
		return CODEC.xmap(value -> new ClampedNumberProvider(value, min, max), Function.identity());
	}

	record Type<P extends NumberProvider>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) implements ValueProvider.Type<P> {

	}

}
